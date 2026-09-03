package com.pinodesk.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pinodesk.annotation.TargetActivity;
import com.pinodesk.constant.Activity;
import com.pinodesk.constant.CacheNameConstants;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.constant.PaymentStatus;
import com.pinodesk.constant.SellingMode;
import com.pinodesk.entity.Product;
import com.pinodesk.entity.ProductExpiry;
import com.pinodesk.entity.ProductPrice;
import com.pinodesk.entity.ProductStock;
import com.pinodesk.entity.Receivable;
import com.pinodesk.entity.Sale;
import com.pinodesk.entity.SaleDetail;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.PackageDetailRepository;
import com.pinodesk.repository.ProductExpiryRepository;
import com.pinodesk.repository.ProductPriceRepository;
import com.pinodesk.repository.ProductRepository;
import com.pinodesk.repository.ProductStockRepository;
import com.pinodesk.repository.ReceivablePaymentRepository;
import com.pinodesk.repository.ReceivableRepository;
import com.pinodesk.repository.SaleDetailRepository;
import com.pinodesk.repository.SaleRepository;
import com.pinodesk.util.ProductUtils;
import com.pinodesk.viewmodel.SaleAddVM;
import com.pinodesk.viewmodel.SaleEditVM;
import com.pinodesk.viewmodel.SaleFilterVM;
import com.pinodesk.viewmodel.SaleProductVM;
import com.pinodesk.viewmodel.SaleReportFilterVM;
import com.pinodesk.viewmodel.SaleReportVM;
import com.pinodesk.viewmodel.SaleVM;

@Service
public class SaleService extends BaseService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductExpiryRepository productExpiryRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private ReceivablePaymentRepository receivablePaymentRepository;

    @Autowired
    private PackageDetailRepository packageDetailRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SessionService sessionService;

    @TargetActivity(Activity.SEARCH_SALES_BY_FILTER)
    @Cacheable(CacheNameConstants.SALES_BY_FILTER)
    public List<SaleVM> searchSales(SaleFilterVM filter) {
        return saleRepository.findByFilter(filter);
    }

    @TargetActivity(Activity.REMOVE_SALES)
    @CacheEvict(value = {
            CacheNameConstants.SALES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.RECEIVABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void removeSales(List<Long> ids) {
        ids.forEach(id -> revertLastSaleProducts(id, Activity.REMOVE_SALES.toString()));
        receivableRepository.deleteBySaleIdIn(ids); // Cascade delete to receivable_payment
        saleRepository.deleteByIdIn(ids); // Cascade delete to sale_detail
    }

    private void handleSalePackageProduct(
            String activityName,
            SaleProductVM saleProduct,
            Long saleId,
            String invoiceNumber) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        packageDetailRepository.findByProductId(saleProduct.getProductId(), language).forEach(pp -> {
            productRepository.findByIdAndDeletedAtIsNull(pp.getId()).ifPresent(product -> {
                Integer finalQuantity = createProductStock(
                        activityName,
                        saleId,
                        pp.getId(),
                        pp.getName(),
                        pp.getQuantityInPackage() * saleProduct.getSaleQuantity(),
                        invoiceNumber,
                        saleProduct.getProductName());
                product.setQuantity(finalQuantity);
                productRepository.save(product);
            });
        });
        createSaleDetail(saleId, saleProduct);
    }

    private boolean isProductPricesUnset(Product product) {
        return product.getGeneralSellingPrice() == null && product.getPrescriptionSellingPrice() == null;
    }

    @TargetActivity(Activity.ADD_SALE)
    @CacheEvict(value = {
            CacheNameConstants.SALES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.RECEIVABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void createSale(SaleAddVM saleAdd, Activity activity) {
        Long currentUserId = sessionService.getCurrentSession().getUser().getId();
        String activityName = activity.toString();
        String invoiceNumber = saleAdd.getInvoiceNumber();
        if (saleRepository.existsByInvoiceNumberIgnoreCaseAndDeletedAtIsNull(invoiceNumber)) {
            throw new DomainException(DomainError.SALE_EXISTS_BY_INVOICE_NUMBER);
        }
        Sale sale = new Sale();
        sale.setCustomerId(saleAdd.getCustomerId());
        if (SellingMode.PRESCRIPTION.equals(saleAdd.getSellingMode())) {
            sale.setDoctorId(saleAdd.getDoctorId());
        }
        sale.setInvoiceNumber(invoiceNumber);
        sale.setInvoiceDate(saleAdd.getInvoiceDate());
        sale.setPaymentDueDate(saleAdd.getPaymentDueDate());
        sale.setPaymentStatus(saleAdd.getPaymentStatus().toString());
        sale.setSellingMode(saleAdd.getSellingMode().toString());
        sale.setTotalPayment(saleAdd.getTotalPayment());
        sale.setTotalProduct(saleAdd.getTotalProduct());
        sale.setTotalSale(saleAdd.getTotalSale());
        sale.setUserId(currentUserId);
        Sale created = saleRepository.save(sale);
        Long saleId = created.getId();
        saleAdd.getSaleProducts().forEach(saleProduct -> {
            if (ProductUtils.isProductCategoryCustomPackage(saleProduct.getProductCategoryCode())) {
                handleSalePackageProduct(activityName, saleProduct, saleId, invoiceNumber);
                return;
            }
            SaleDetail sd = createSaleDetail(saleId, saleProduct);
            Long productId = saleProduct.getProductId();
            Product product = productRepository.findByIdAndDeletedAtIsNull(productId).orElseThrow();
            boolean productNeedsUpdate = false;
            if (product.getQuantity() == null) {
                ProductStock ps = new ProductStock();
                ps.setActivity(activityName);
                ps.setFinalQuantity(saleProduct.getCurrentQuantity());
                ps.setProductId(productId);
                ps.setSaleId(saleId);
                ps.setSaleInvoiceNumber(invoiceNumber);
                ps.setUserId(currentUserId);
                productStockRepository.save(ps);
                product.setQuantity(saleProduct.getCurrentQuantity());
                productNeedsUpdate = true;
            }
            if (isProductPricesUnset(product)) {
                BigDecimal sellingPrice = saleProduct.getSellingPrice();
                ProductPrice pp = new ProductPrice();
                pp.setActivity(activityName);
                pp.setGeneralSellingPrice(sellingPrice);
                pp.setProductId(productId);
                pp.setUserId(currentUserId);
                pp.setSaleId(saleId);
                pp.setSaleInvoiceNumber(invoiceNumber);
                if (SellingMode.PRESCRIPTION.equals(saleAdd.getSellingMode())) {
                    pp.setPrescriptionSellingPrice(sellingPrice);
                    product.setPrescriptionSellingPrice(sellingPrice);
                }
                product.setGeneralSellingPrice(sellingPrice);
                productPriceRepository.save(pp);
                productNeedsUpdate = true;
            }
            if (productNeedsUpdate) {
                product = productRepository.save(product);
            }
            Integer finalQuantity = createProductStock(activityName, saleId, saleProduct, invoiceNumber);
            if (saleProduct.getExpiredDate() != null) {
                createProductExpiry(activityName, sd, saleProduct, invoiceNumber);
            }
            product.setQuantity(finalQuantity);
            productRepository.save(product);
        });
        createReceivable(sale);
    }

    private void createReceivable(Sale sale) {
        if (PaymentStatus.UNPAID.toString().equals(sale.getPaymentStatus()) && sale.getCustomerId() != null) {
            Receivable receivable = new Receivable();
            receivable.setInvoiceDate(sale.getCreatedAt().toLocalDate());
            receivable.setInvoiceNumber(sale.getInvoiceNumber());
            receivable.setAmount(sale.getTotalPayment());
            receivable.setDueDate(sale.getPaymentDueDate());
            receivable.setSaleId(sale.getId());
            receivable.setCustomerId(sale.getCustomerId());
            receivableRepository.save(receivable);
        }
    }

    private void createProductExpiry(
            String activityName,
            SaleDetail saleDetail,
            SaleProductVM saleProduct,
            String invoiceNumber) {
        Integer saleQuantity = saleProduct.getSaleQuantity();
        ProductExpiry px = new ProductExpiry();
        px.setActivity(activityName);
        px.setExpiredDate(saleProduct.getExpiredDate());
        px.setProductId(saleProduct.getProductId());
        px.setQuantityOut(saleQuantity);
        px.setSaleId(saleDetail.getSaleId());
        px.setSaleDetailId(saleDetail.getId());
        px.setSaleInvoiceNumber(invoiceNumber);
        px.setUserId(sessionService.getCurrentSession().getUser().getId());
        ProductExpiry pxByProductId = productExpiryRepository
                .findFirstByProductIdOrderByIdDesc(saleProduct.getProductId()).orElseThrow();
        px.setFinalQuantity(pxByProductId.getFinalQuantity() - saleQuantity);
        px.setFinalQuantityExpiredDate(pxByProductId.getFinalQuantityExpiredDate() - saleQuantity);
        if (!pxByProductId.getExpiredDate().isEqual(saleProduct.getExpiredDate())) {
            Integer finalQuantityExpiredDate = productExpiryRepository.findFirstByProductIdAndExpiredDateOrderByIdDesc(
                    saleProduct.getProductId(),
                    saleProduct.getExpiredDate()).map(ProductExpiry::getFinalQuantityExpiredDate).orElse(0);
            px.setFinalQuantityExpiredDate(finalQuantityExpiredDate - saleQuantity);
        }
        productExpiryRepository.save(px);
    }

    private Integer createProductStock(
            String activityName,
            Long saleId,
            SaleProductVM saleProduct,
            String invoiceNumber) {
        return createProductStock(
                activityName,
                saleId,
                saleProduct.getProductId(),
                saleProduct.getProductName(),
                saleProduct.getSaleQuantity(),
                invoiceNumber,
                null);
    }

    private Integer createProductStock(
            String activityName,
            Long saleId,
            Long productId,
            String productName,
            Integer saleQuantity,
            String invoiceNumber,
            String remarks) {
        Integer lastStockQuantity = productStockRepository
                .findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).stream()
                .map(ProductStock::getFinalQuantity).findAny().orElse(0);
        if (lastStockQuantity < saleQuantity) {
            throw new DomainException(DomainError.PRODUCT_QUANTITY_INSUFFICIENT_FOR_SALE, productName);
        }
        Integer finalQuantity = lastStockQuantity - saleQuantity;
        ProductStock ps = new ProductStock();
        ps.setActivity(activityName);
        ps.setFinalQuantity(finalQuantity);
        ps.setProductId(productId);
        ps.setQuantityOut(saleQuantity);
        ps.setSaleId(saleId);
        ps.setSaleInvoiceNumber(invoiceNumber);
        ps.setUserId(sessionService.getCurrentSession().getUser().getId());
        ps.setRemarks(remarks);
        productStockRepository.save(ps);
        return finalQuantity;
    }

    private SaleDetail createSaleDetail(Long saleId, SaleProductVM saleProduct) {
        SaleDetail sd = new SaleDetail();
        sd.setProductId(saleProduct.getProductId());
        sd.setQuantity(saleProduct.getSaleQuantity());
        sd.setSaleId(saleId);
        sd.setSellingPrice(saleProduct.getSellingPrice());
        sd.setSubtotal(saleProduct.getSubtotal());
        return saleDetailRepository.save(sd);
    }

    /**
     * Processes the change in payment status for a sale.
     * <p>
     * This method handles the logic for updating receivables based on the change in
     * payment status of a sale. If the status changes to PAID, it deletes the
     * corresponding receivable. If the status changes to UNPAID, it creates a new
     * receivable. If the status remains unchanged, it updates the existing
     * receivable information.
     * </p>
     *
     * @param saleEdit The SaleEditVM containing the updated sale information.
     * @param sale     The existing Sale entity.
     * 
     * @throws DomainException If a receivable payment already exists for the sale
     *                         when the status is changed to PAID, or if a
     *                         receivable is already completed when the status is
     *                         changed to UNPAID.
     */
    private void processPaymentStatusChange(SaleEditVM saleEdit, Sale sale) {
        boolean isChangedToPaid = !saleEdit.getPaymentStatus().toString().equals(sale.getPaymentStatus())
                && saleEdit.getPaymentStatus().equals(PaymentStatus.PAID);
        boolean isChangedToUnpaid = !saleEdit.getPaymentStatus().toString().equals(sale.getPaymentStatus())
                && saleEdit.getPaymentStatus().equals(PaymentStatus.UNPAID);
        Optional<Receivable> oreceivable = receivableRepository.findBySaleId(sale.getId());
        if (isChangedToPaid) {
            if (receivablePaymentRepository.existsBySaleId(sale.getId())) {
                throw new DomainException(DomainError.RECEIVABLE_PAYMENT_EXISTS_BY_SALE_ID);
            }
            oreceivable.ifPresent(receivable -> {
                receivablePaymentRepository.deleteByReceivableId(receivable.getId());
                receivableRepository.delete(receivable);
            });
            return;
        }
        if (isChangedToUnpaid) {
            if (oreceivable.isPresent()) {
                throw new DomainException(DomainError.RECEIVABLE_ALREADY_COMPLETED_BY_SALE_ID);
            }
            if (saleEdit.getCustomerId() != null) {
                Receivable receivable = new Receivable();
                receivable.setInvoiceDate(sale.getCreatedAt().toLocalDate());
                receivable.setInvoiceNumber(saleEdit.getInvoiceNumber());
                receivable.setAmount(saleEdit.getTotalPayment());
                receivable.setDueDate(saleEdit.getPaymentDueDate());
                receivable.setSaleId(sale.getId());
                receivable.setCustomerId(saleEdit.getCustomerId());
                receivableRepository.save(receivable);
            }
            return;
        }
        // No changes on payment status, update receivable if present
        oreceivable.ifPresent(receivable -> {
            receivable.setInvoiceDate(sale.getCreatedAt().toLocalDate());
            receivable.setInvoiceNumber(saleEdit.getInvoiceNumber());
            receivable.setAmount(saleEdit.getTotalPayment());
            receivable.setDueDate(saleEdit.getPaymentDueDate());
            receivable.setSaleId(sale.getId());
            receivable.setCustomerId(saleEdit.getCustomerId());
            receivableRepository.save(receivable);
        });
    }

    /**
     * Updates the sale data and the details. The sale might contain some deleted
     * products, which will maintain the sale product history as long as the deleted
     * products are not removed from the sold products.
     * <p>
     * This method handles updating the sale information, reverting previous sale
     * product details (stock, expiry, price), and then applying the new sale
     * product details. It also checks for invoice number uniqueness and handles
     * payment status changes which may affect receivables.
     * <p/>
     *
     * @param saleEdit The SaleEditVM model containing the updated sale data and
     *                 products.
     * @param saleId   The sale id that will be updated.
     * 
     * @throws DomainException If the sale is not found, if another sale with the
     *                         same invoice number exists, or if there are issues
     *                         with receivable payments.
     */
    @TargetActivity(Activity.EDIT_SALE)
    @CacheEvict(value = {
            CacheNameConstants.SALES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.RECEIVABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void updateSale(SaleEditVM saleEdit, Long saleId) {
        Long currentUserId = sessionService.getCurrentSession().getUser().getId();
        String activityName = Activity.EDIT_SALE.toString();
        String invoiceNumber = saleEdit.getInvoiceNumber();
        Sale sale = saleRepository.findByIdAndDeletedAtIsNull(saleId)
                .orElseThrow(() -> new DomainException(DomainError.SALE_NOT_FOUND_BY_ID));
        processPaymentStatusChange(saleEdit, sale);
        if (!sale.getInvoiceNumber().equals(invoiceNumber)
                && saleRepository.existsByInvoiceNumberIgnoreCaseAndDeletedAtIsNull(invoiceNumber)) {
            throw new DomainException(DomainError.SALE_OTHER_EXISTS_BY_INVOICE_NUMBER);

        }
        sale.setCustomerId(saleEdit.getCustomerId());
        if (SellingMode.PRESCRIPTION.equals(saleEdit.getSellingMode())) {
            sale.setDoctorId(saleEdit.getDoctorId());
        }
        sale.setInvoiceNumber(invoiceNumber);
        sale.setInvoiceDate(saleEdit.getInvoiceDate());
        sale.setPaymentDueDate(saleEdit.getPaymentDueDate());
        sale.setPaymentStatus(saleEdit.getPaymentStatus().toString());
        sale.setSellingMode(saleEdit.getSellingMode().toString());
        sale.setTotalPayment(saleEdit.getTotalPayment());
        sale.setTotalProduct(saleEdit.getTotalProduct());
        sale.setTotalSale(saleEdit.getTotalSale());
        saleRepository.save(sale);
        revertLastSaleProducts(saleId, activityName);
        saleDetailRepository.deleteBySaleId(saleId);
        List<Long> productIds = saleEdit.getSaleProducts().stream().map(SaleProductVM::getProductId).toList();
        Map<Long, SaleProductVM> mapSaleProducts = saleEdit.getSaleProducts().stream()
                .collect(Collectors.toMap(SaleProductVM::getProductId, Function.identity()));
        productRepository.findByIdIn(productIds).forEach(product -> {
            SaleProductVM saleProduct = mapSaleProducts.get(product.getId());
            if (ProductUtils.isProductCategoryCustomPackage(saleProduct.getProductCategoryCode())) {
                // Process package products separately
                handleSalePackageProduct(activityName, saleProduct, saleId, invoiceNumber);
                return;
            }
            SaleDetail sd = createSaleDetail(saleId, saleProduct);
            if (product.getDeletedAt() != null) {
                // The deleted products will be kept for the sale details only, changes to the
                // product details, price, stock, or expiry won't be stored as they won't be
                // seen on the product list anymore.
                return;
            }
            Long productId = product.getId();
            boolean productNeedsUpdate = false;
            if (product.getQuantity() == null) {
                ProductStock ps = new ProductStock();
                ps.setActivity(activityName);
                ps.setFinalQuantity(saleProduct.getCurrentQuantity());
                ps.setProductId(productId);
                ps.setSaleId(saleId);
                ps.setSaleInvoiceNumber(invoiceNumber);
                ps.setUserId(currentUserId);
                productStockRepository.save(ps);
                product.setQuantity(saleProduct.getCurrentQuantity());
                productNeedsUpdate = true;
            }
            if (isProductPricesUnset(product)) {
                BigDecimal sellingPrice = saleProduct.getSellingPrice();
                ProductPrice pp = new ProductPrice();
                pp.setActivity(activityName);
                pp.setGeneralSellingPrice(sellingPrice);
                pp.setProductId(productId);
                pp.setUserId(currentUserId);
                pp.setSaleId(saleId);
                pp.setSaleInvoiceNumber(invoiceNumber);
                if (SellingMode.PRESCRIPTION.equals(saleEdit.getSellingMode())) {
                    pp.setPrescriptionSellingPrice(sellingPrice);
                    product.setPrescriptionSellingPrice(sellingPrice);
                }
                product.setGeneralSellingPrice(sellingPrice);
                productPriceRepository.save(pp);
                productNeedsUpdate = true;
            }
            if (productNeedsUpdate) {
                product = productRepository.save(product);
            }
            Integer finalQuantity = createProductStock(activityName, saleId, saleProduct, invoiceNumber);
            if (saleProduct.getExpiredDate() != null) {
                createProductExpiry(activityName, sd, saleProduct, invoiceNumber);
            }
            product.setQuantity(finalQuantity);
            productRepository.save(product);
        });
    }

    /**
     * Reverts the effects of a sale on product details (stock, expiry, price).
     * <p>
     * When a sale is edited or removed, this method reverses the changes made to
     * the associated products. It iterates through each product in the sale and
     * calls methods to restore the product's stock, expiry, and price to their
     * previous state. If a product is a package, this method will also revert the
     * effects on the products contained within the package.
     * </p>
     *
     * @param saleId       The ID of the sale being reverted.
     * @param activityName The activity that triggered the reversion (e.g.,
     *                     "EDIT_SALE", "REMOVE_SALES").
     */
    private void revertLastSaleProducts(Long saleId, String activityName) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        Long currentUserId = sessionService.getCurrentSession().getUser().getId();
        productRepository.findBySaleIdAndDeletedAtIsNull(saleId).forEach(product -> {
            Long productId = product.getId();
            List<Product> products = List.of(product);
            if (ProductUtils.isProductCategoryCustomPackage(product.getCategoryCode())) {
                products = packageDetailRepository.findByProductId(productId, language).stream()
                        .map(pp -> objectConverter.convertObject(pp, Product.class)).toList();
            }
            products.forEach(p -> {
                revertProductStock(saleId, activityName, currentUserId, p);
                revertProductExpiry(saleId, activityName, currentUserId, productId);
                revertProductPrice(saleId, activityName, currentUserId, p);
                productExpiryRepository.findClosestExpiredDateAvailableByProductId(productId)
                        .ifPresentOrElse(p::setClosestExpiredDate, () -> p.setClosestExpiredDate(null));
                productRepository.save(p);
            });
        });
    }

    private void revertProductPrice(Long saleId, String activityName, Long currentUserId, Product product) {
        Long productId = product.getId();
        List<ProductPrice> pps = productPriceRepository.findFirst2ByProductIdAndDeletedAtIsNullOrderByIdDesc(productId);
        if (!pps.isEmpty()) {
            ProductPrice pp0 = pps.get(0);
            if (Objects.equals(saleId, pp0.getSaleId())) {
                ProductPrice pp = new ProductPrice();
                pp.setActivity(activityName);
                pp.setProductId(productId);
                pp.setUserId(currentUserId);
                if (pps.size() == 2) {
                    ProductPrice pp1 = pps.get(1);
                    pp.setGeneralSellingPrice(pp1.getGeneralSellingPrice());
                    pp.setPrescriptionSellingPrice(pp1.getPrescriptionSellingPrice());
                }
                pp.setRemarks("Revert price");
                productPriceRepository.save(pp);
                product.setGeneralSellingPrice(pp.getGeneralSellingPrice());
                product.setPrescriptionSellingPrice(pp.getPrescriptionSellingPrice());
            }
        }
    }

    private void revertProductExpiry(Long saleId, String activityName, Long currentUserId, Long productId) {
        productExpiryRepository.findFirstByProductIdOrderByIdDesc(productId).ifPresent(px -> {
            if (Objects.equals(saleId, px.getSaleId())) {
                ProductExpiry npx = new ProductExpiry();
                npx.setActivity(activityName);
                npx.setExpiredDate(px.getExpiredDate());
                npx.setFinalQuantity(px.getFinalQuantity() + px.getQuantityOut());
                npx.setFinalQuantityExpiredDate(px.getFinalQuantityExpiredDate() + px.getQuantityOut());
                npx.setProductId(productId);
                npx.setUserId(currentUserId);
                npx.setRemarks("Revert expiry stock");
                productExpiryRepository.save(npx);
            }
        });
    }

    private void revertProductStock(Long saleId, String activityName, Long currentUserId, Product product) {
        Long productId = product.getId();
        productStockRepository.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).ifPresent(ps -> {
            if (Objects.equals(saleId, ps.getSaleId())) {
                ProductStock nps = new ProductStock();
                nps.setActivity(activityName);
                int finalQuantity = ps.getFinalQuantity() + ps.getQuantityOut();
                nps.setFinalQuantity(finalQuantity);
                nps.setProductId(productId);
                nps.setUserId(currentUserId);
                nps.setRemarks("Revert stock");
                productStockRepository.save(nps);
                product.setQuantity(finalQuantity);
            }
        });
    }

    @TargetActivity(Activity.GET_SALE_PRODUCTS)
    public List<SaleProductVM> getSaleProducts(Long saleId) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return saleDetailRepository.findBySaleIdJoinProducts(saleId, language);
    }

    @TargetActivity(Activity.ADD_SALE_CASHIER)
    @CacheEvict(value = {
            CacheNameConstants.SALES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.RECEIVABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void createSaleCashier(SaleAddVM saleAdd) {
        createSale(saleAdd, Activity.ADD_SALE_CASHIER);
    }

    @TargetActivity(Activity.SEARCH_SALE_REPORT)
    public List<SaleReportVM> searchSalesReport(SaleReportFilterVM filter, String language) {
        return saleDetailRepository.findByFilter(filter, language);
    }

}

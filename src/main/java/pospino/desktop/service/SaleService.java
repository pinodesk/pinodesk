package pospino.desktop.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pospino.desktop.annotation.ForActivity;
import pospino.desktop.constant.Activity;
import pospino.desktop.constant.CacheNameConstants;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.DomainError;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.SellingMode;
import pospino.desktop.domain.Product;
import pospino.desktop.domain.ProductExpiry;
import pospino.desktop.domain.ProductPrice;
import pospino.desktop.domain.ProductStock;
import pospino.desktop.domain.Receivable;
import pospino.desktop.domain.Sale;
import pospino.desktop.domain.SaleDetail;
import pospino.desktop.exception.DomainException;
import pospino.desktop.repository.PackageDetailRepository;
import pospino.desktop.repository.ProductExpiryRepository;
import pospino.desktop.repository.ProductPriceRepository;
import pospino.desktop.repository.ProductRepository;
import pospino.desktop.repository.ProductStockRepository;
import pospino.desktop.repository.ReceivablePaymentRepository;
import pospino.desktop.repository.ReceivableRepository;
import pospino.desktop.repository.SaleDetailRepository;
import pospino.desktop.repository.SaleRepository;
import pospino.desktop.util.ProductUtils;
import pospino.desktop.viewmodel.SaleAddVM;
import pospino.desktop.viewmodel.SaleEditVM;
import pospino.desktop.viewmodel.SaleFilterVM;
import pospino.desktop.viewmodel.SaleProductVM;
import pospino.desktop.viewmodel.SaleReportFilterVM;
import pospino.desktop.viewmodel.SaleReportVM;
import pospino.desktop.viewmodel.SaleVM;

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

    @ForActivity(Activity.SEARCH_SALES_BY_FILTER)
    @Cacheable(CacheNameConstants.SALES_BY_FILTER)
    public List<SaleVM> searchSales(SaleFilterVM filter) {
        return saleRepository.findByFilter(filter);
    }

    @ForActivity(Activity.REMOVE_SALES)
    @CacheEvict(value = {
            CacheNameConstants.SALES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removeSales(List<Long> ids) {
        ids.forEach(id -> revertLastSaleProducts(id, Activity.REMOVE_SALES.toString()));
        saleDetailRepository.deleteUpdateBySaleIdIn(ids);
        saleRepository.deleteUpdateByIdIn(ids);
    }

    private void handleSalePackageProduct(
            String activityName,
            SaleProductVM saleProduct,
            Long saleId,
            String invoiceNumber) {
        packageDetailRepository.findByProductId(saleProduct.getProductId()).forEach(pp -> {
            Product product = productRepository.findByIdAndDeletedAtIsNull(pp.getId()).orElseThrow();
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
        createSaleDetail(saleId, saleProduct);
    }

    private boolean isProductPricesUnset(Product product) {
        return product.getGeneralSellingPrice() == null && product.getPrescriptionSellingPrice() == null;
    }

    @ForActivity(Activity.ADD_SALE)
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
        sale.setPaymentDueDate(saleAdd.getPaymentDueDate());
        sale.setPaymentStatus(saleAdd.getPaymentStatus().toString());
        sale.setSellingMode(saleAdd.getSellingMode().toString());
        sale.setTotalPayment(saleAdd.getTotalPayment());
        sale.setTotalProduct(saleAdd.getTotalProduct());
        sale.setTotalSale(saleAdd.getTotalSale());
        Sale created = saleRepository.save(sale);
        Long saleId = created.getId();
        saleAdd.getSaleProducts().forEach(saleProduct -> {
            if (ProductUtils.isProductCategoryCustomPackage(saleProduct.getProductCategoryCode())) {
                handleSalePackageProduct(activityName, saleProduct, saleId, invoiceNumber);
                return;
            }
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
            SaleDetail sd = createSaleDetail(saleId, saleProduct);
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

    @ForActivity(Activity.EDIT_SALE)
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
        sale.setPaymentDueDate(saleEdit.getPaymentDueDate());
        sale.setPaymentStatus(saleEdit.getPaymentStatus().toString());
        sale.setSellingMode(saleEdit.getSellingMode().toString());
        sale.setTotalPayment(saleEdit.getTotalPayment());
        sale.setTotalProduct(saleEdit.getTotalProduct());
        sale.setTotalSale(saleEdit.getTotalSale());
        saleRepository.save(sale);
        revertLastSaleProducts(saleId, activityName);
        saleDetailRepository.deleteBySaleId(saleId);
        saleEdit.getSaleProducts().forEach(saleProduct -> {
            if (ProductUtils.isProductCategoryCustomPackage(saleProduct.getProductCategoryCode())) {
                handleSalePackageProduct(activityName, saleProduct, saleId, invoiceNumber);
                return;
            }
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
            SaleDetail sd = createSaleDetail(saleId, saleProduct);
            if (saleProduct.getExpiredDate() != null) {
                createProductExpiry(activityName, sd, saleProduct, invoiceNumber);
            }
            product.setQuantity(finalQuantity);
            productRepository.save(product);
        });
    }

    private void revertLastSaleProducts(Long saleId, String activityName) {
        Long currentUserId = sessionService.getCurrentSession().getUser().getId();
        saleDetailRepository.findBySaleId(saleId).forEach(pd -> {
            Long productId = pd.getProductId();
            Product product = productRepository.findByIdAndDeletedAtIsNull(productId).orElseThrow();
            List<Product> products = List.of(product);
            if (ProductUtils.isProductCategoryCustomPackage(product.getCategoryCode())) {
                products = packageDetailRepository.findByProductId(productId).stream()
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

    @ForActivity(Activity.GET_SALE_PRODUCTS)
    public List<SaleProductVM> getSaleProducts(Long saleId) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return saleDetailRepository.findBySaleIdJoinProducts(saleId, language);
    }

    @ForActivity(Activity.ADD_SALE_CASHIER)
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

    @ForActivity(Activity.SEARCH_SALE_REPORT)
    public List<SaleReportVM> searchSalesReport(SaleReportFilterVM filter, String language) {
        return saleDetailRepository.findByFilter(filter, language);
    }

}

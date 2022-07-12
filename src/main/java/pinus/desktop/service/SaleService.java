package pinus.desktop.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.Activity;
import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.constant.PaymentStatus;
import pinus.desktop.constant.SellingMode;
import pinus.desktop.domain.Product;
import pinus.desktop.domain.ProductExpiry;
import pinus.desktop.domain.ProductPrice;
import pinus.desktop.domain.ProductStock;
import pinus.desktop.domain.Receivable;
import pinus.desktop.domain.Sale;
import pinus.desktop.domain.SaleDetail;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.ProductExpiryRepository;
import pinus.desktop.repository.ProductPriceRepository;
import pinus.desktop.repository.ProductRepository;
import pinus.desktop.repository.ProductStockRepository;
import pinus.desktop.repository.ReceivableRepository;
import pinus.desktop.repository.SaleDetailRepository;
import pinus.desktop.repository.SaleRepository;
import pinus.desktop.viewmodel.SaleAddVM;
import pinus.desktop.viewmodel.SaleEditVM;
import pinus.desktop.viewmodel.SaleFilterVM;
import pinus.desktop.viewmodel.SaleProductVM;
import pinus.desktop.viewmodel.SaleVM;

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
    private ConfigurationService configurationService;

    @Cacheable(CacheNameConstants.SALES_BY_FILTER)
    public List<SaleVM> searchSales(SaleFilterVM filter) {
        return saleRepository.findByFilter(filter);
    }

    @CacheEvict(value = {
            CacheNameConstants.SALES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removeSales(List<Long> ids) {
        ids.forEach(id -> revertLastSaleProducts(id, Activity.DELETE_SALE.toString()));
        saleDetailRepository.deleteUpdateBySaleIdIn(ids);
        saleRepository.deleteUpdateByIdIn(ids);
    }

    @CacheEvict(value = {
            CacheNameConstants.SALES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PAYABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void createSale(SaleAddVM saleAdd) {
        String activityName = Activity.ADD_SALE.toString();
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
                ps.setUserId(1l);
                productStockRepository.save(ps);
                product.setQuantity(saleProduct.getCurrentQuantity());
                productNeedsUpdate = true;
            }
            if (product.getGeneralSellingPrice() == null && product.getPrescriptionSellingPrice() == null) {
                BigDecimal sellingPrice = saleProduct.getSellingPrice();
                ProductPrice pp = new ProductPrice();
                pp.setActivity(activityName);
                pp.setGeneralSellingPrice(sellingPrice);
                pp.setProductId(productId);
                pp.setUserId(1l);
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
            createSaleDetail(saleId, saleProduct);
            if (saleProduct.getExpiredDate() != null) {
                createProductExpiry(activityName, saleId, saleProduct, invoiceNumber);
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
            receivable.setPaymentAmount(sale.getTotalPayment());
            receivable.setPaymentDueDate(sale.getPaymentDueDate());
            receivable.setSaleId(sale.getId());
            receivable.setCustomerId(sale.getCustomerId());
            receivableRepository.save(receivable);
        }
    }

    private void createProductExpiry(
            String activityName,
            Long saleId,
            SaleProductVM saleProduct,
            String invoiceNumber) {
        Integer saleQuantity = saleProduct.getSaleQuantity();
        ProductExpiry px = new ProductExpiry();
        px.setActivity(activityName);
        px.setExpiredDate(saleProduct.getExpiredDate());
        px.setProductId(saleProduct.getProductId());
        px.setQuantityOut(saleQuantity);
        px.setSaleId(saleId);
        px.setSaleInvoiceNumber(invoiceNumber);
        px.setUserId(1l);
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
        Long productId = saleProduct.getProductId();
        Integer saleQuantity = saleProduct.getSaleQuantity();
        Integer lastStockQuantity = productStockRepository
                .findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).stream()
                .map(ProductStock::getFinalQuantity).findAny().orElse(0);
        if (lastStockQuantity < saleQuantity) {
            throw new DomainException(DomainError.PRODUCT_QUANTITY_INSUFFICIENT_FOR_SALE, saleProduct.getProductName());
        }
        Integer finalQuantity = lastStockQuantity - saleQuantity;
        ProductStock ps = new ProductStock();
        ps.setActivity(activityName);
        ps.setFinalQuantity(finalQuantity);
        ps.setProductId(productId);
        ps.setQuantityOut(saleQuantity);
        ps.setSaleId(saleId);
        ps.setSaleInvoiceNumber(invoiceNumber);
        ps.setUserId(1l);
        productStockRepository.save(ps);
        return finalQuantity;
    }

    private void createSaleDetail(Long saleId, SaleProductVM saleProduct) {
        SaleDetail sd = new SaleDetail();
        sd.setProductId(saleProduct.getProductId());
        sd.setQuantity(saleProduct.getSaleQuantity());
        sd.setSaleId(saleId);
        sd.setSellingPrice(saleProduct.getSellingPrice());
        sd.setSubtotal(saleProduct.getSubtotal());
        saleDetailRepository.save(sd);
    }

    @CacheEvict(value = {
            CacheNameConstants.SALES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void updateSale(SaleEditVM saleEdit, Long saleId) {
        String activityName = Activity.EDIT_SALE.toString();
        String invoiceNumber = saleEdit.getInvoiceNumber();
        Sale sale = saleRepository.findByIdAndDeletedAtIsNull(saleId)
                .orElseThrow(() -> new DomainException(DomainError.SALE_NOT_FOUND_BY_ID));
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
                ps.setUserId(1l);
                productStockRepository.save(ps);
                product.setQuantity(saleProduct.getCurrentQuantity());
                productNeedsUpdate = true;
            }
            if (product.getGeneralSellingPrice() == null && product.getPrescriptionSellingPrice() == null) {
                BigDecimal sellingPrice = saleProduct.getSellingPrice();
                ProductPrice pp = new ProductPrice();
                pp.setActivity(activityName);
                pp.setGeneralSellingPrice(sellingPrice);
                pp.setProductId(productId);
                pp.setUserId(1l);
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
            createSaleDetail(saleId, saleProduct);
            if (saleProduct.getExpiredDate() != null) {
                createProductExpiry(activityName, saleId, saleProduct, invoiceNumber);
            }
            product.setQuantity(finalQuantity);
            productRepository.save(product);
        });
    }

    private void revertLastSaleProducts(Long saleId, String activityName) {
        saleDetailRepository.findBySaleId(saleId).forEach(pd -> {
            Long productId = pd.getProductId();
            Product product = productRepository.findByIdAndDeletedAtIsNull(productId).orElseThrow();
            productStockRepository.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).ifPresent(ps -> {
                if (Objects.equals(saleId, ps.getSaleId())) {
                    ProductStock nps = new ProductStock();
                    nps.setActivity(activityName);
                    int finalQuantity = ps.getFinalQuantity() + ps.getQuantityOut();
                    nps.setFinalQuantity(finalQuantity);
                    nps.setProductId(productId);
                    nps.setUserId(1l);
                    productStockRepository.save(nps);
                    product.setQuantity(finalQuantity);
                }
            });
            productExpiryRepository.findFirstByProductIdOrderByIdDesc(productId).ifPresent(px -> {
                if (Objects.equals(saleId, px.getSaleId())) {
                    ProductExpiry npx = new ProductExpiry();
                    npx.setActivity(activityName);
                    npx.setExpiredDate(px.getExpiredDate());
                    npx.setFinalQuantity(px.getFinalQuantity() + px.getQuantityOut());
                    npx.setFinalQuantityExpiredDate(px.getFinalQuantityExpiredDate() + px.getQuantityOut());
                    npx.setProductId(productId);
                    npx.setUserId(1l);
                    productExpiryRepository.save(npx);
                }
            });
            List<ProductPrice> pps = productPriceRepository
                    .findFirst2ByProductIdAndDeletedAtIsNullOrderByIdDesc(productId);
            if (!pps.isEmpty()) {
                ProductPrice pp0 = pps.get(0);
                if (Objects.equals(saleId, pp0.getSaleId())) {
                    ProductPrice pp = new ProductPrice();
                    pp.setActivity(activityName);
                    pp.setProductId(productId);
                    pp.setUserId(1l);
                    if (pps.size() == 2) {
                        ProductPrice pp1 = pps.get(1);
                        pp.setGeneralSellingPrice(pp1.getGeneralSellingPrice());
                        pp.setPrescriptionSellingPrice(pp1.getPrescriptionSellingPrice());
                    }
                    productPriceRepository.save(pp);
                    product.setGeneralSellingPrice(pp.getGeneralSellingPrice());
                    product.setPrescriptionSellingPrice(pp.getPrescriptionSellingPrice());
                }
            }
            productExpiryRepository.findClosestExpiredDateAvailableByProductId(productId)
                    .ifPresentOrElse(product::setClosestExpiredDate, () -> product.setClosestExpiredDate(null));
            productRepository.save(product);
        });
    }

    public List<SaleProductVM> getSaleProducts(Long saleId) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return saleDetailRepository.findBySaleIdJoinProducts(saleId, language);
    }

}

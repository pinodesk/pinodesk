package pinus.desktop.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.Activity;
import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.Product;
import pinus.desktop.domain.ProductExpiry;
import pinus.desktop.domain.ProductPrice;
import pinus.desktop.domain.ProductStock;
import pinus.desktop.domain.Purchase;
import pinus.desktop.domain.PurchaseDetail;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.ProductExpiryRepository;
import pinus.desktop.repository.ProductPriceRepository;
import pinus.desktop.repository.ProductRepository;
import pinus.desktop.repository.ProductStockRepository;
import pinus.desktop.repository.PurchaseDetailRepository;
import pinus.desktop.repository.PurchaseRepository;
import pinus.desktop.viewmodel.PurchaseAddVM;
import pinus.desktop.viewmodel.PurchaseEditVM;
import pinus.desktop.viewmodel.PurchaseFilterVM;
import pinus.desktop.viewmodel.PurchaseProductVM;
import pinus.desktop.viewmodel.PurchaseVM;

@Service
public class PurchaseService extends BaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseDetailRepository purchaseDetailRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private ProductExpiryRepository productExpiryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Cacheable(CacheNameConstants.PURCHASES_BY_FILTER)
    public List<PurchaseVM> searchPurchases(PurchaseFilterVM filter) {
        return purchaseRepository.findByFilter(filter);
    }

    @CacheEvict(value = {
            CacheNameConstants.PURCHASES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void createPurchase(PurchaseAddVM purchaseAdd) {
        String activityName = Activity.ADD_PURCHASE.name();
        String invoiceNumber = purchaseAdd.getInvoiceNumber();
        if (purchaseRepository.existsByInvoiceNumberIgnoreCaseAndSupplierIdAndDeletedAtIsNull(
                invoiceNumber,
                purchaseAdd.getSupplierId())) {
            throw new DomainException(DomainError.PURCHASE_EXISTS_BY_INVOICE_NUMBER_AND_SUPPLIER_ID);
        }
        Purchase purchase = new Purchase();
        purchase.setDiscount(purchaseAdd.getDiscount());
        purchase.setInvoiceDate(purchaseAdd.getInvoiceDate());
        purchase.setInvoiceNumber(invoiceNumber);
        purchase.setPaymentDueDate(purchaseAdd.getPaymentDueDate());
        purchase.setPaymentStatus(purchaseAdd.getPaymentStatus().name());
        purchase.setSupplierId(purchaseAdd.getSupplierId());
        purchase.setTax(purchaseAdd.getTax());
        purchase.setTotalPayment(purchaseAdd.getTotalPayment());
        purchase.setTotalProduct(purchaseAdd.getTotalProduct());
        purchase.setTotalPurchase(purchaseAdd.getTotalPurchase());
        Purchase created = purchaseRepository.save(purchase);
        Long purchaseId = created.getId();
        purchaseAdd.getPurchaseProducts().stream().forEach(purchaseProduct -> {
            Long productId = purchaseProduct.getProductId();
            Integer purchaseQuantity = purchaseProduct.getQuantity();
            Product product = productRepository.findByIdAndDeletedAtIsNull(productId).orElseThrow();
            createPurchaseDetail(purchaseId, purchaseProduct, productId, purchaseQuantity);
            if (purchaseProduct.getExpiredDate() != null) {
                createProductExpiry(
                        activityName,
                        invoiceNumber,
                        purchaseId,
                        purchaseProduct,
                        productId,
                        purchaseQuantity);
            }
            Integer lastStockQuantity = productStockRepository
                    .findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).stream()
                    .map(ProductStock::getFinalQuantity).findAny().orElse(0);
            Integer nextStockQuantity = lastStockQuantity + purchaseQuantity;
            createProductStock(activityName, invoiceNumber, purchaseId, productId, purchaseQuantity, nextStockQuantity);
            createProductPrice(activityName, invoiceNumber, purchaseId, purchaseProduct, productId);
            updateProduct(purchaseProduct, productId, product, nextStockQuantity);
        });
    }

    @CacheEvict(value = {
            CacheNameConstants.PURCHASES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void updatePurchase(PurchaseEditVM purchaseEdit, Long purchaseId) {
        String activityName = Activity.EDIT_PURCHASE.name();
        String invoiceNumber = purchaseEdit.getInvoiceNumber();
        Long supplierId = purchaseEdit.getSupplierId();
        Purchase purchase = purchaseRepository.findByIdAndDeletedAtIsNull(purchaseId)
                .orElseThrow(() -> new DomainException(DomainError.PURCHASE_NOT_FOUND_BY_ID));
        if (isDifferentInvoiceNumberOrSupplierId(purchase, purchaseEdit) && purchaseRepository
                .existsByInvoiceNumberIgnoreCaseAndSupplierIdAndDeletedAtIsNull(invoiceNumber, supplierId)) {
            throw new DomainException(DomainError.PURCHASE_OTHER_EXISTS_BY_INVOICE_NUMBER_AND_SUPPLIER_ID);
        }
        purchase.setDiscount(purchaseEdit.getDiscount());
        purchase.setInvoiceDate(purchaseEdit.getInvoiceDate());
        purchase.setInvoiceNumber(invoiceNumber);
        purchase.setPaymentDueDate(purchaseEdit.getPaymentDueDate());
        purchase.setPaymentStatus(purchaseEdit.getPaymentStatus().name());
        purchase.setSupplierId(supplierId);
        purchase.setTax(purchaseEdit.getTax());
        purchase.setTotalPayment(purchaseEdit.getTotalPayment());
        purchase.setTotalProduct(purchaseEdit.getTotalProduct());
        purchase.setTotalPurchase(purchaseEdit.getTotalPurchase());
        purchaseRepository.save(purchase);
        revertLastPurchasedProducts(purchaseId, activityName);
        purchaseDetailRepository.deleteByPurchaseId(purchaseId);
        purchaseEdit.getPurchaseProducts().stream().forEach(purchaseProduct -> {
            Long productId = purchaseProduct.getProductId();
            Integer purchaseQuantity = purchaseProduct.getQuantity();
            Product product = productRepository.findByIdAndDeletedAtIsNull(productId).orElseThrow();
            createPurchaseDetail(purchaseId, purchaseProduct, productId, purchaseQuantity);
            if (purchaseProduct.getExpiredDate() != null) {
                createProductExpiry(
                        activityName,
                        invoiceNumber,
                        purchaseId,
                        purchaseProduct,
                        productId,
                        purchaseQuantity);
            }
            Integer lastStockQuantity = productStockRepository
                    .findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).stream()
                    .map(ProductStock::getFinalQuantity).findAny().orElse(0);
            Integer nextStockQuantity = lastStockQuantity + purchaseQuantity;
            createProductStock(activityName, invoiceNumber, purchaseId, productId, purchaseQuantity, nextStockQuantity);
            createProductPrice(activityName, invoiceNumber, purchaseId, purchaseProduct, productId);
            updateProduct(purchaseProduct, productId, product, nextStockQuantity);
        });
    }

    private void revertLastPurchasedProducts(Long purchaseId, String activityName) {
        purchaseDetailRepository.findByPurchaseId(purchaseId).forEach(pd -> {
            Long productId = pd.getProductId();
            Product product = productRepository.findByIdAndDeletedAtIsNull(productId).orElseThrow();
            productStockRepository.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).ifPresent(ps -> {
                if (Objects.equals(purchaseId, ps.getPurchaseId())) {
                    ProductStock nps = new ProductStock();
                    nps.setActivity(activityName);
                    int finalQuantity = ps.getFinalQuantity() - ps.getQuantityIn();
                    nps.setFinalQuantity(finalQuantity);
                    nps.setProductId(productId);
                    nps.setUserId(1l);
                    productStockRepository.save(nps);
                    product.setQuantity(finalQuantity);
                }
            });
            productExpiryRepository.findFirstByProductIdOrderByIdDesc(productId).ifPresent(px -> {
                if (Objects.equals(purchaseId, px.getPurchaseId())) {
                    ProductExpiry npx = new ProductExpiry();
                    npx.setActivity(activityName);
                    npx.setExpiredDate(px.getExpiredDate());
                    npx.setFinalQuantity(px.getFinalQuantity() - px.getQuantityIn());
                    npx.setFinalQuantityExpiredDate(px.getFinalQuantityExpiredDate() - px.getQuantityIn());
                    npx.setProductId(productId);
                    npx.setUserId(1l);
                    productExpiryRepository.save(npx);
                }
            });
            List<ProductPrice> pps = productPriceRepository
                    .findFirst2ByProductIdAndDeletedAtIsNullOrderByIdDesc(productId);
            if (!pps.isEmpty()) {
                ProductPrice pp0 = pps.get(0);
                if (Objects.equals(purchaseId, pp0.getPurchaseId())) {
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
            List<PurchaseDetail> purchaseDetails = purchaseDetailRepository
                    .findByProductIdAndDeletedAtIsNull(productId);
            BigDecimal averageBuyingPrice = purchaseDetails.stream()
                    .filter(pd1 -> Objects.equals(pd1.getPurchaseId(), purchaseId)).map(PurchaseDetail::getBuyingPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(purchaseDetails.size()));
            product.setAverageBuyingPrice(averageBuyingPrice);
            productRepository.save(product);
        });
    }

    private boolean isDifferentInvoiceNumberOrSupplierId(Purchase purchase, PurchaseEditVM purchaseEdit) {
        return ObjectUtils.notEqual(purchase.getSupplierId(), purchaseEdit.getSupplierId())
                || ObjectUtils.notEqual(purchase.getInvoiceNumber(), purchaseEdit.getInvoiceNumber());
    }

    private void createPurchaseDetail(
            Long purchaseId,
            PurchaseProductVM purchaseProduct,
            Long productId,
            Integer purchaseQuantity) {
        PurchaseDetail pd = new PurchaseDetail();
        pd.setBuyingPrice(purchaseProduct.getBuyingPrice());
        pd.setProductId(productId);
        pd.setPurchaseId(purchaseId);
        pd.setQuantity(purchaseQuantity);
        pd.setSubtotal(purchaseProduct.getSubtotal());
        purchaseDetailRepository.save(pd);
    }

    private void updateProduct(
            PurchaseProductVM purchaseProduct,
            Long productId,
            Product product,
            Integer nextStockQuantity) {
        productExpiryRepository.findClosestExpiredDateAvailableByProductId(productId)
                .ifPresentOrElse(product::setClosestExpiredDate, () -> product.setClosestExpiredDate(null));
        BigDecimal averageBuyingPrice = calculateProductAverageBuyingPrice(productId);
        product.setAverageBuyingPrice(averageBuyingPrice);
        product.setGeneralSellingPrice(purchaseProduct.getGeneralSellingPrice());
        product.setPrescriptionSellingPrice(purchaseProduct.getPrescriptionSellingPrice());
        product.setQuantity(nextStockQuantity);
        productRepository.save(product);
    }

    private BigDecimal calculateProductAverageBuyingPrice(Long productId) {
        List<PurchaseDetail> purchaseDetails = purchaseDetailRepository.findByProductIdAndDeletedAtIsNull(productId);
        return purchaseDetails.stream().map(PurchaseDetail::getBuyingPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(purchaseDetails.size()));
    }

    private void createProductExpiry(
            String activityName,
            String invoiceNumber,
            Long purchaseId,
            PurchaseProductVM purchaseProduct,
            Long productId,
            Integer purchaseQuantity) {
        Integer lastExpiryQuantity = productExpiryRepository.findFirstByProductIdOrderByIdDesc(productId).stream()
                .map(ProductExpiry::getFinalQuantity).findAny().orElse(0);
        Integer finalQuantityExpiredDate = productExpiryRepository
                .findFirstByProductIdAndExpiredDateOrderByIdDesc(productId, purchaseProduct.getExpiredDate())
                .map(ProductExpiry::getFinalQuantityExpiredDate).orElse(0);
        ProductExpiry px = new ProductExpiry();
        px.setActivity(activityName);
        px.setBatchNumber(purchaseProduct.getBatchNumber());
        px.setExpiredDate(purchaseProduct.getExpiredDate());
        px.setFinalQuantity(lastExpiryQuantity + purchaseQuantity);
        px.setFinalQuantityExpiredDate(finalQuantityExpiredDate + purchaseQuantity);
        px.setProductId(productId);
        px.setPurchaseId(purchaseId);
        px.setPurchaseInvoiceNumber(invoiceNumber);
        px.setQuantityIn(purchaseQuantity);
        px.setUserId(1l);
        productExpiryRepository.save(px);
    }

    private void createProductStock(
            String activityName,
            String invoiceNumber,
            Long purchaseId,
            Long productId,
            Integer purchaseQuantity,
            Integer nextStockQuantity) {
        ProductStock ps = new ProductStock();
        ps.setActivity(activityName);
        ps.setProductId(productId);
        ps.setPurchaseId(purchaseId);
        ps.setPurchaseInvoiceNumber(invoiceNumber);
        ps.setQuantityIn(purchaseQuantity);
        ps.setFinalQuantity(nextStockQuantity);
        ps.setUserId(1l);
        productStockRepository.save(ps);
    }

    private void createProductPrice(
            String activityName,
            String invoiceNumber,
            Long purchaseId,
            PurchaseProductVM purchaseProduct,
            Long productId) {
        ProductPrice pp = new ProductPrice();
        pp.setActivity(activityName);
        pp.setGeneralSellingPrice(purchaseProduct.getGeneralSellingPrice());
        pp.setPrescriptionSellingPrice(purchaseProduct.getPrescriptionSellingPrice());
        pp.setProductId(productId);
        pp.setPurchaseId(purchaseId);
        pp.setPurchaseInvoiceNumber(invoiceNumber);
        pp.setUserId(1l);
        productPriceRepository.save(pp);
    }

    @CacheEvict(value = { CacheNameConstants.PURCHASES_BY_FILTER }, allEntries = true)
    @Transactional
    public void removePurchases(List<Long> ids) {
        purchaseDetailRepository.deleteUpdateByPurchaseIdIn(ids);
        purchaseRepository.deleteUpdateByIdIn(ids);
    }

    public List<PurchaseProductVM> getPurchaseProducts(Long purchaseId) {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return purchaseDetailRepository.findByPurchaseIdJoinProducts(purchaseId, languageCode);
    }

}

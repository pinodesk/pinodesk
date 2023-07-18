package pospino.desktop.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
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
import pospino.desktop.domain.Payable;
import pospino.desktop.domain.Product;
import pospino.desktop.domain.ProductExpiry;
import pospino.desktop.domain.ProductPrice;
import pospino.desktop.domain.ProductStock;
import pospino.desktop.domain.Purchase;
import pospino.desktop.domain.PurchaseDetail;
import pospino.desktop.exception.DomainException;
import pospino.desktop.repository.PayablePaymentRepository;
import pospino.desktop.repository.PayableRepository;
import pospino.desktop.repository.ProductExpiryRepository;
import pospino.desktop.repository.ProductPriceRepository;
import pospino.desktop.repository.ProductRepository;
import pospino.desktop.repository.ProductStockRepository;
import pospino.desktop.repository.PurchaseDetailRepository;
import pospino.desktop.repository.PurchaseRepository;
import pospino.desktop.viewmodel.PurchaseAddVM;
import pospino.desktop.viewmodel.PurchaseEditVM;
import pospino.desktop.viewmodel.PurchaseFilterVM;
import pospino.desktop.viewmodel.PurchaseProductVM;
import pospino.desktop.viewmodel.PurchaseReportFilterVM;
import pospino.desktop.viewmodel.PurchaseReportVM;
import pospino.desktop.viewmodel.PurchaseVM;

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
    private PayableRepository payableRepository;

    @Autowired
    private PayablePaymentRepository payablePaymentRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SessionService sessionService;

    @ForActivity(Activity.SEARCH_PURCHASES_BY_FILTER)
    @Cacheable(CacheNameConstants.PURCHASES_BY_FILTER)
    public List<PurchaseVM> searchPurchases(PurchaseFilterVM filter) {
        return purchaseRepository.findByFilter(filter);
    }

    @ForActivity(Activity.ADD_PURCHASE)
    @CacheEvict(value = {
            CacheNameConstants.PURCHASES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PAYABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void createPurchase(PurchaseAddVM purchaseAdd) {
        String activityName = Activity.ADD_PURCHASE.toString();
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
        purchase.setPaymentStatus(purchaseAdd.getPaymentStatus().toString());
        purchase.setSupplierId(purchaseAdd.getSupplierId());
        purchase.setTax(purchaseAdd.getTax());
        purchase.setTotalPayment(purchaseAdd.getTotalPayment());
        purchase.setTotalProduct(purchaseAdd.getTotalProduct());
        purchase.setTotalPurchase(purchaseAdd.getTotalPurchase());
        purchase.setUserId(sessionService.getCurrentSession().getUser().getId());
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
        if (PaymentStatus.UNPAID.equals(purchaseAdd.getPaymentStatus())) {
            Payable payable = new Payable();
            payable.setInvoiceDate(purchaseAdd.getInvoiceDate());
            payable.setInvoiceNumber(purchaseAdd.getInvoiceNumber());
            payable.setAmount(purchaseAdd.getTotalPayment());
            payable.setDueDate(purchaseAdd.getPaymentDueDate());
            payable.setPurchaseId(purchaseId);
            payable.setSupplierId(purchaseAdd.getSupplierId());
            payableRepository.save(payable);
        }
    }

    private void processPaymentStatusChange(PurchaseEditVM purchaseEdit, Purchase purchase) {
        boolean isChangedToPaid = !purchaseEdit.getPaymentStatus().toString().equals(purchase.getPaymentStatus())
                && purchaseEdit.getPaymentStatus().equals(PaymentStatus.PAID);
        boolean isChangedToUnpaid = !purchaseEdit.getPaymentStatus().toString().equals(purchase.getPaymentStatus())
                && purchaseEdit.getPaymentStatus().equals(PaymentStatus.UNPAID);
        Optional<Payable> opayable = payableRepository.findByPurchaseId(purchase.getId());
        if (isChangedToPaid) {
            if (payablePaymentRepository.existsByPurchaseId(purchase.getId())) {
                throw new DomainException(DomainError.PAYABLE_PAYMENT_EXISTS_BY_SALE_ID);
            }
            opayable.ifPresent(payable -> {
                payablePaymentRepository.deleteByPayableId(payable.getId());
                payableRepository.delete(payable);
            });
            return;
        }
        if (isChangedToUnpaid) {
            if (opayable.isPresent()) {
                throw new DomainException(DomainError.PAYABLE_ALREADY_COMPLETED_BY_SALE_ID);
            }
            Payable payable = new Payable();
            payable.setInvoiceDate(purchaseEdit.getInvoiceDate());
            payable.setInvoiceNumber(purchaseEdit.getInvoiceNumber());
            payable.setAmount(purchaseEdit.getTotalPayment());
            payable.setDueDate(purchaseEdit.getPaymentDueDate());
            payable.setPurchaseId(purchase.getId());
            payable.setSupplierId(purchaseEdit.getSupplierId());
            payableRepository.save(payable);
            return;
        }
        // No changes on payment status, update payable if present
        opayable.ifPresent(payable -> {
            payable.setInvoiceDate(purchaseEdit.getInvoiceDate());
            payable.setInvoiceNumber(purchaseEdit.getInvoiceNumber());
            payable.setAmount(purchaseEdit.getTotalPayment());
            payable.setDueDate(purchaseEdit.getPaymentDueDate());
            payable.setPurchaseId(purchase.getId());
            payable.setSupplierId(purchaseEdit.getSupplierId());
            payableRepository.save(payable);
        });
    }

    @ForActivity(Activity.EDIT_PURCHASE)
    @CacheEvict(value = {
            CacheNameConstants.PURCHASES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PAYABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void updatePurchase(PurchaseEditVM purchaseEdit, Long purchaseId) {
        String activityName = Activity.EDIT_PURCHASE.toString();
        String invoiceNumber = purchaseEdit.getInvoiceNumber();
        Long supplierId = purchaseEdit.getSupplierId();
        Purchase purchase = purchaseRepository.findByIdAndDeletedAtIsNull(purchaseId)
                .orElseThrow(() -> new DomainException(DomainError.PURCHASE_NOT_FOUND_BY_ID));
        processPaymentStatusChange(purchaseEdit, purchase);
        if (isDifferentInvoiceNumberOrSupplierId(purchase, purchaseEdit) && purchaseRepository
                .existsByInvoiceNumberIgnoreCaseAndSupplierIdAndDeletedAtIsNull(invoiceNumber, supplierId)) {
            throw new DomainException(DomainError.PURCHASE_OTHER_EXISTS_BY_INVOICE_NUMBER_AND_SUPPLIER_ID);
        }
        purchase.setDiscount(purchaseEdit.getDiscount());
        purchase.setInvoiceDate(purchaseEdit.getInvoiceDate());
        purchase.setInvoiceNumber(invoiceNumber);
        purchase.setPaymentDueDate(purchaseEdit.getPaymentDueDate());
        purchase.setPaymentStatus(purchaseEdit.getPaymentStatus().toString());
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
        Long currentUserId = sessionService.getCurrentSession().getUser().getId();
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
                    nps.setUserId(currentUserId);
                    nps.setRemarks("Revert stock");
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
                    npx.setUserId(currentUserId);
                    npx.setRemarks("Revert expiry stock");
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
            productExpiryRepository.findClosestExpiredDateAvailableByProductId(productId)
                    .ifPresentOrElse(product::setClosestExpiredDate, () -> product.setClosestExpiredDate(null));
            List<PurchaseDetail> purchaseDetails = purchaseDetailRepository
                    .findByProductIdAndDeletedAtIsNull(productId);
            BigDecimal averageBuyingPrice = purchaseDetails.stream()
                    .filter(pd1 -> Objects.equals(pd1.getPurchaseId(), purchaseId)).map(PurchaseDetail::getBuyingPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(purchaseDetails.size()), 4, RoundingMode.HALF_UP);
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
                .divide(BigDecimal.valueOf(purchaseDetails.size()), 4, RoundingMode.HALF_UP);
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
        px.setUserId(sessionService.getCurrentSession().getUser().getId());
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
        ps.setUserId(sessionService.getCurrentSession().getUser().getId());
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
        pp.setUserId(sessionService.getCurrentSession().getUser().getId());
        productPriceRepository.save(pp);
    }

    @ForActivity(Activity.REMOVE_PURCHASES)
    @CacheEvict(value = {
            CacheNameConstants.PURCHASES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removePurchases(List<Long> ids) {
        ids.forEach(id -> revertLastPurchasedProducts(id, Activity.REMOVE_PURCHASES.toString()));
        purchaseDetailRepository.deleteUpdateByPurchaseIdIn(ids);
        purchaseRepository.deleteUpdateByIdIn(ids);
    }

    @ForActivity(Activity.GET_PURCHASE_PRODUCTS)
    public List<PurchaseProductVM> getPurchaseProducts(Long purchaseId) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return purchaseDetailRepository.findByPurchaseIdJoinProducts(purchaseId, language);
    }

    @ForActivity(Activity.SEARCH_PURCHASE_REPORT)
    public List<PurchaseReportVM> searchPurchaseReport(PurchaseReportFilterVM filter, String language) {
        return purchaseDetailRepository.findByFilter(filter, language);
    }

}

package com.pinodesk.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
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
import com.pinodesk.entity.Payable;
import com.pinodesk.entity.Product;
import com.pinodesk.entity.ProductExpiry;
import com.pinodesk.entity.ProductPrice;
import com.pinodesk.entity.ProductStock;
import com.pinodesk.entity.Purchase;
import com.pinodesk.entity.PurchaseDetail;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.PayablePaymentRepository;
import com.pinodesk.repository.PayableRepository;
import com.pinodesk.repository.ProductExpiryRepository;
import com.pinodesk.repository.ProductPriceRepository;
import com.pinodesk.repository.ProductRepository;
import com.pinodesk.repository.ProductStockRepository;
import com.pinodesk.repository.PurchaseDetailRepository;
import com.pinodesk.repository.PurchaseRepository;
import com.pinodesk.viewmodel.PurchaseAddVM;
import com.pinodesk.viewmodel.PurchaseEditVM;
import com.pinodesk.viewmodel.PurchaseFilterVM;
import com.pinodesk.viewmodel.PurchaseProductVM;
import com.pinodesk.viewmodel.PurchaseReportFilterVM;
import com.pinodesk.viewmodel.PurchaseReportVM;
import com.pinodesk.viewmodel.PurchaseVM;

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

    @TargetActivity(Activity.SEARCH_PURCHASES_BY_FILTER)
    @Cacheable(CacheNameConstants.PURCHASES_BY_FILTER)
    public List<PurchaseVM> searchPurchases(PurchaseFilterVM filter) {
        return purchaseRepository.findByFilter(filter);
    }

    @TargetActivity(Activity.ADD_PURCHASE)
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
        purchase.setAdditionalDiscount(purchaseAdd.getAdditionalDiscount());
        purchase.setTotalDiscount(purchaseAdd.getTotalDiscount());
        purchase.setInvoiceDate(purchaseAdd.getInvoiceDate());
        purchase.setInvoiceNumber(invoiceNumber);
        purchase.setPaymentDueDate(purchaseAdd.getPaymentDueDate());
        purchase.setPaymentStatus(purchaseAdd.getPaymentStatus().toString());
        purchase.setSupplierId(purchaseAdd.getSupplierId());
        purchase.setTax(purchaseAdd.getTax());
        purchase.setTotalPayment(purchaseAdd.getTotalPayment());
        purchase.setTotalProduct(purchaseAdd.getTotalProduct());
        purchase.setTotalPrice(purchaseAdd.getTotalPrice());
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
            updateProduct(purchaseProduct, product, nextStockQuantity);
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

    /**
     * Processes the change in payment status for a purchase.
     * <p>
     * This method handles the logic for updating payables based on the change in
     * payment status of a purchase. If the status changes to PAID, it deletes the
     * corresponding payable. If the status changes to UNPAID, it creates a new
     * payable. If the status remains unchanged, it updates the existing payable
     * information.
     * </p>
     *
     * @param purchaseEdit The PurchaseEditVM containing the updated purchase
     *                     information.
     * @param purchase     The existing Purchase entity.
     * 
     * @throws DomainException If a payable payment already exists for the purchase
     *                         when the status is changed to PAID, or if a payable
     *                         is already completed when the status is changed to
     *                         UNPAID.
     */
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

    /**
     * Updates the purchase data and the details. The purchase might contain some
     * deleted products, which will maintain the purchase product history as long as
     * the deleted products are not removed from the purchased products.
     * <p>
     * This method handles updating the purchase information, reverting previous
     * purchase product details (stock, expiry, price), and then applying the new
     * purchase product details. It also checks for invoice number and supplier ID
     * uniqueness and handles payment status changes which may affect payables.
     * </p>
     * 
     * @param purchaseEdit The PurchaseEditVM model containing the updated purchase
     *                     data and products.
     * @param purchaseId   The ID of the purchase to be updated.
     * 
     * @throws DomainException If the purchase is not found, if another purchase
     *                         with the same invoice number and supplier ID exists,
     *                         or if there are issues with payable payments.
     */
    @TargetActivity(Activity.EDIT_PURCHASE)
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
        purchase.setAdditionalDiscount(purchaseEdit.getAdditionalDiscount());
        purchase.setTotalDiscount(purchaseEdit.getTotalDiscount());
        purchase.setInvoiceDate(purchaseEdit.getInvoiceDate());
        purchase.setInvoiceNumber(invoiceNumber);
        purchase.setPaymentDueDate(purchaseEdit.getPaymentDueDate());
        purchase.setPaymentStatus(purchaseEdit.getPaymentStatus().toString());
        purchase.setSupplierId(supplierId);
        purchase.setTax(purchaseEdit.getTax());
        purchase.setTotalPayment(purchaseEdit.getTotalPayment());
        purchase.setTotalProduct(purchaseEdit.getTotalProduct());
        purchase.setTotalPrice(purchaseEdit.getTotalPrice());
        purchaseRepository.save(purchase);
        revertLastPurchasedProducts(purchaseId, activityName);
        purchaseDetailRepository.deleteByPurchaseId(purchaseId);
        // Process the products for this purchase as the new order details
        List<Long> productIds = purchaseEdit.getPurchaseProducts().stream().map(p -> p.getProductId()).toList();
        Map<Long, PurchaseProductVM> mapPurchaseProducts = purchaseEdit.getPurchaseProducts().stream()
                .collect(Collectors.toMap(PurchaseProductVM::getProductId, Function.identity()));
        productRepository.findByIdIn(productIds).forEach(product -> {
            Long productId = product.getId();
            PurchaseProductVM purchaseProduct = mapPurchaseProducts.get(productId);
            Integer purchaseQuantity = purchaseProduct.getQuantity();
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
            updateProduct(purchaseProduct, product, nextStockQuantity);
        });
    }

    /**
     * Reverts the old product details of the purchase, including stocks, expiries,
     * and prices to the state before the edit or removal.
     * <p>
     * This method iterates through each product associated with the given purchase
     * ID. For each product, it reverts the product stock, expiry, and price to
     * their previous states. It also updates the product's closest expiry date and
     * recalculates the average buying price.
     * </p>
     *
     * @param purchaseId   The ID of the purchase being reverted.
     * @param activityName The name of the activity causing the reversion (e.g.,
     *                     "EDIT_PURCHASE", "REMOVE_PURCHASES"). This is used for
     *                     audit purposes.
     */
    private void revertLastPurchasedProducts(Long purchaseId, String activityName) {
        Long currentUserId = sessionService.getCurrentSession().getUser().getId();
        productRepository.findByPurchaseIdAndDeletedAtIsNull(purchaseId).forEach(product -> {
            Long productId = product.getId();
            revertProductStock(purchaseId, activityName, currentUserId, product);
            revertProductExpiry(purchaseId, activityName, currentUserId, productId);
            revertProductPrice(purchaseId, activityName, currentUserId, product);
            productExpiryRepository.findClosestExpiredDateAvailableByProductId(productId)
                    .ifPresentOrElse(product::setClosestExpiredDate, () -> product.setClosestExpiredDate(null));
            List<PurchaseDetail> purchaseDetails = purchaseDetailRepository
                    .findByProductIdAndDeletedAtIsNull(productId);
            BigDecimal averageBuyingPrice = purchaseDetails.stream()
                    .filter(pd1 -> Objects.equals(pd1.getPurchaseId(), purchaseId)).map(PurchaseDetail::getBuyingPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(purchaseDetails.size()), 4, RoundingMode.HALF_EVEN);
            product.setAverageBuyingPrice(averageBuyingPrice);
            productRepository.save(product);
        });
    }

    private void revertProductPrice(Long purchaseId, String activityName, Long currentUserId, Product product) {
        Long productId = product.getId();
        List<ProductPrice> pps = productPriceRepository.findFirst2ByProductIdAndDeletedAtIsNullOrderByIdDesc(productId);
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
    }

    private void revertProductExpiry(Long purchaseId, String activityName, Long currentUserId, Long productId) {
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
    }

    private void revertProductStock(Long purchaseId, String activityName, Long currentUserId, Product product) {
        Long productId = product.getId();
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
        pd.setSubtotalPrice(purchaseProduct.getSubtotalPrice());
        pd.setDiscountType(Objects.toString(purchaseProduct.getDiscountType(), null));
        pd.setDiscountAmount(purchaseProduct.getDiscountAmount());
        pd.setSubtotalDiscount(purchaseProduct.getSubtotalDiscount());
        pd.setBuyingPriceDiscount(purchaseProduct.getBuyingPriceDiscount());
        purchaseDetailRepository.save(pd);
    }

    private void updateProduct(PurchaseProductVM purchaseProduct, Product product, Integer nextStockQuantity) {
        Long productId = product.getId();
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
                .divide(BigDecimal.valueOf(purchaseDetails.size()), 4, RoundingMode.HALF_EVEN);
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

    @TargetActivity(Activity.REMOVE_PURCHASES)
    @CacheEvict(value = {
            CacheNameConstants.PURCHASES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PAYABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void removePurchases(List<Long> ids) {
        ids.forEach(id -> revertLastPurchasedProducts(id, Activity.REMOVE_PURCHASES.toString()));
        payableRepository.deleteByPurchaseIdIn(ids); // Cascade delete to payable_payment
        purchaseRepository.deleteByIdIn(ids); // Cascade delete to purchase_detail
    }

    @TargetActivity(Activity.GET_PURCHASE_PRODUCTS)
    public List<PurchaseProductVM> getPurchaseProducts(Long purchaseId) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return purchaseDetailRepository.findByPurchaseIdJoinProducts(purchaseId, language);
    }

    @TargetActivity(Activity.SEARCH_PURCHASE_REPORT)
    public List<PurchaseReportVM> searchPurchaseReport(PurchaseReportFilterVM filter, String language) {
        return purchaseDetailRepository.findByFilter(filter, language);
    }

}

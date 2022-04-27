package pinus.desktop.service;

import java.math.BigDecimal;
import java.util.List;

import com.gitlab.muhammadkholidb.sequel.sql.Where;

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
        return purchaseRepository.filter(filter);
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
        if (purchaseRepository.existsByInvoiceNumberAndSupplierId(invoiceNumber, purchaseAdd.getSupplierId())) {
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
        Long purchaseId = purchaseRepository.create(purchase);
        purchaseAdd.getPurchaseProducts().stream().forEach(purchaseProduct -> {
            Long productId = purchaseProduct.getProductId();
            Integer purchaseQuantity = purchaseProduct.getQuantity();
            Product product = productRepository.readOne(productId).orElseThrow();
            createPurchaseDetail(purchaseId, purchaseProduct, productId, purchaseQuantity);
            if (purchaseProduct.getExpiredDate() != null) {
                Integer lastExpiryQuantity = productExpiryRepository.findTopByProductId(productId).stream()
                        .map(ProductExpiry::getFinalQuantity).findAny().orElse(0);
                createProductExpiry(
                        activityName,
                        invoiceNumber,
                        purchaseId,
                        purchaseProduct,
                        productId,
                        purchaseQuantity,
                        lastExpiryQuantity);
            }
            Integer lastStockQuantity = productStockRepository.findTopByProductId(productId).stream()
                    .map(ProductStock::getFinalQuantity).findAny().orElse(0);
            Integer nextStockQuantity = lastStockQuantity + purchaseQuantity;
            createProductStock(activityName, invoiceNumber, purchaseId, productId, purchaseQuantity, nextStockQuantity);
            createProductPrice(activityName, invoiceNumber, purchaseId, purchaseProduct, productId);
            updateProduct(purchaseProduct, productId, product, nextStockQuantity);
        });
    }

    @CacheEvict(value = { CacheNameConstants.PURCHASES_BY_FILTER }, allEntries = true)
    @Transactional
    public void updatePurchase(PurchaseEditVM purchaseEdit, Long purchaseId) {
        String invoiceNumber = purchaseEdit.getInvoiceNumber();
        Purchase purchase = purchaseRepository.readOne(purchaseId)
                .orElseThrow(() -> new DomainException(DomainError.PURCHASE_NOT_FOUND_BY_ID));
        if (purchaseRepository
                .existsByInvoiceNumberAndSupplierId(invoiceNumber, purchaseEdit.getSupplierId(), purchase.getId())) {
            throw new DomainException(DomainError.PURCHASE_OTHER_EXISTS_BY_INVOICE_NUMBER_AND_SUPPLIER_ID);
        }
        purchase.setDiscount(purchaseEdit.getDiscount());
        purchase.setInvoiceDate(purchaseEdit.getInvoiceDate());
        purchase.setInvoiceNumber(invoiceNumber);
        purchase.setPaymentDueDate(purchaseEdit.getPaymentDueDate());
        purchase.setPaymentStatus(purchaseEdit.getPaymentStatus().name());
        purchase.setSupplierId(purchaseEdit.getSupplierId());
        purchase.setTax(purchaseEdit.getTax());
        purchase.setTotalPayment(purchaseEdit.getTotalPayment());
        purchase.setTotalProduct(purchaseEdit.getTotalProduct());
        purchase.setTotalPurchase(purchaseEdit.getTotalPurchase());
        purchaseRepository.update(purchase);
        purchaseDetailRepository.deleteByPurchaseId(purchaseId);
        purchaseEdit.getPurchaseProducts().stream().forEach(purchaseProduct -> {
            Long productId = purchaseProduct.getProductId();
            Integer purchaseQuantity = purchaseProduct.getQuantity();
            createPurchaseDetail(purchaseId, purchaseProduct, productId, purchaseQuantity);
        });
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
        purchaseDetailRepository.create(pd);
    }

    private void updateProduct(
            PurchaseProductVM purchaseProduct,
            Long productId,
            Product product,
            Integer nextStockQuantity) {
        productExpiryRepository.findTopByProductIdOrderByExpiredDate(productId)
                .ifPresent(px -> product.setClosestExpiredDate(px.getExpiredDate()));
        BigDecimal averageBuyingPrice = calculateProductAverageBuyingPrice(productId);
        product.setAverageBuyingPrice(averageBuyingPrice);
        product.setGeneralSellingPrice(purchaseProduct.getGeneralSellingPrice());
        product.setPrescriptionSellingPrice(purchaseProduct.getPrescriptionSellingPrice());
        product.setQuantity(nextStockQuantity);
        productRepository.update(product);
    }

    private BigDecimal calculateProductAverageBuyingPrice(Long productId) {
        List<PurchaseDetail> purchaseDetails = purchaseDetailRepository.findByProductId(productId);
        return purchaseDetails.stream().map(PurchaseDetail::getBuyingPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(purchaseDetails.size()));
    }

    private void createProductExpiry(
            String activityName,
            String invoiceNumber,
            Long purchaseId,
            PurchaseProductVM purchaseProduct,
            Long productId,
            Integer purchaseQuantity,
            Integer lastExpiryQuantity) {
        ProductExpiry px = new ProductExpiry();
        px.setActivity(activityName);
        px.setBatchNumber(purchaseProduct.getBatchNumber());
        px.setExpiredDate(purchaseProduct.getExpiredDate());
        px.setFinalQuantity(lastExpiryQuantity + purchaseQuantity);
        px.setProductId(productId);
        px.setPurchaseId(purchaseId);
        px.setPurchaseInvoiceNumber(invoiceNumber);
        px.setQuantityIn(purchaseQuantity);
        px.setUserId(1l);
        productExpiryRepository.create(px);
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
        productStockRepository.create(ps);
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
        productPriceRepository.create(pp);
    }

    @CacheEvict(value = { CacheNameConstants.PURCHASES_BY_FILTER }, allEntries = true)
    @Transactional
    public void removePurchases(List<Long> ids) {
        purchaseDetailRepository.delete(new Where().in(PurchaseDetail.C_PURCHASE_ID, ids));
        purchaseRepository.delete(ids);
    }

    public List<PurchaseProductVM> getPurchaseProducts(Long purchaseId) {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return purchaseDetailRepository.findByPurchaseIdJoinProducts(purchaseId, languageCode);
    }

}

package pinus.desktop.service;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.constant.PaymentMethod;
import pinus.desktop.constant.PaymentStatus;
import pinus.desktop.domain.Product;
import pinus.desktop.domain.Purchase;
import pinus.desktop.domain.PurchaseDetail;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.ProductRepository;
import pinus.desktop.repository.PurchaseDetailRepository;
import pinus.desktop.repository.PurchaseRepository;
import pinus.desktop.viewmodel.PurchaseFilterVM;
import pinus.desktop.viewmodel.PurchaseOrderVM;
import pinus.desktop.viewmodel.PurchaseOrderVM.PurchaseProductVM;
import pinus.desktop.viewmodel.PurchaseVM;

@Service
public class PurchaseService extends BaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseDetailRepository purchaseDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Cacheable(CacheNameConstants.PURCHASES_BY_FILTER)
    public List<PurchaseVM> searchPurchases(PurchaseFilterVM filter) {
        return purchaseRepository.filter(filter);
    }

    @CacheEvict(value = { CacheNameConstants.PURCHASES_BY_FILTER }, allEntries = true)
    @Transactional
    public Long createPurchase(PurchaseOrderVM po) {
        if (purchaseRepository.existsByOrderNumberAndSupplierId(po.getOrderNumber(), po.getSupplierId())) {
            throw new DomainException(DomainError.PURCHASE_EXISTS_BY_ORDER_NUMBER_AND_SUPPLIER_ID);
        }
        Purchase purchase = new Purchase();
        purchase.setOrderDate(po.getOrderDate());
        purchase.setOrderNumber(po.getOrderNumber());
        purchase.setTotalProduct(po.getTotalProduct());
        purchase.setTotalPayment(po.getTotalPayment());
        purchase.setPaymentMethod(po.getPaymentMethod().name());
        purchase.setPaymentPeriodCount(po.getPaymentPeriodCount());
        purchase.setPaymentPeriodUnit(po.getPaymentPeriodUnit() == null ? null : po.getPaymentPeriodUnit().name());
        purchase.setPaymentDueDate(po.getDueDate());
        purchase.setPaymentStatus(
                po.getPaymentMethod().equals(PaymentMethod.CASH) ?
                        PaymentStatus.PAID.name() : PaymentStatus.UNPAID.name());
        purchase.setSupplierId(po.getSupplierId());
        purchase.setTax(po.getTax());
        purchase.setDiscount(po.getDiscount());
        purchase.setTotalPurchase(po.getTotalPurchase());
        Long purchaseId = purchaseRepository.create(purchase);
        for (PurchaseProductVM purchaseProduct : po.getPurchaseProducts()) {
            PurchaseDetail pd = new PurchaseDetail();
            pd.setPurchaseId(purchaseId);
            pd.setProductId(purchaseProduct.getProduct().getId());
            pd.setProductPrice(purchaseProduct.getPurchasePrice());
            pd.setQuantity(purchaseProduct.getPurchaseQuantity());
            pd.setSubtotal(purchaseProduct.getSubtotalPurchase());
            purchaseDetailRepository.create(pd);
            Product product = productRepository.readOne(purchaseProduct.getProduct().getId()).orElseThrow();
            Integer lastQuantity = product.getQuantity();
            product.setPurchasePrice(purchaseProduct.getPurchasePrice());
            product.setSellingPrice(purchaseProduct.getSellingPrice());
            product.setQuantity(
                    lastQuantity == null ?
                            purchaseProduct.getPurchaseQuantity() :
                            lastQuantity + purchaseProduct.getPurchaseQuantity());
            productRepository.update(product);
        }
        return purchaseId;
    }

    @CacheEvict(value = { CacheNameConstants.PURCHASES_BY_FILTER }, allEntries = true)
    @Transactional
    public void removePurchases(List<Long> ids) {
        purchaseRepository.delete(ids);
        purchaseDetailRepository.delete(new Where().in(PurchaseDetail.C_PURCHASE_ID, ids));
    }

}

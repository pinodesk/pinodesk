package toscabox.desktop.service;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import toscabox.desktop.constant.CacheNameConstants;
import toscabox.desktop.constant.PaymentMethod;
import toscabox.desktop.constant.PaymentStatus;
import toscabox.desktop.domain.Product;
import toscabox.desktop.domain.Purchase;
import toscabox.desktop.domain.PurchaseDetail;
import toscabox.desktop.repository.ProductRepository;
import toscabox.desktop.repository.PurchaseDetailRepository;
import toscabox.desktop.repository.PurchaseRepository;
import toscabox.desktop.viewmodel.PurchaseFilterVM;
import toscabox.desktop.viewmodel.PurchaseOrderVM;
import toscabox.desktop.viewmodel.PurchaseOrderVM.PurchaseProductVM;
import toscabox.desktop.viewmodel.PurchaseVM;

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
        Purchase purchase = new Purchase();
        purchase.setOrderDate(po.getOrderDate());
        purchase.setOrderNumber(po.getOrderNumber());
        purchase.setTotalProduct(po.getTotalProduct());
        purchase.setTotalPayment(po.getTotalPayment());
        purchase.setPaymentMethod(po.getPaymentMethod().name());
        purchase.setPaymentPeriodCount(po.getPaymentPeriodCount());
        purchase.setPaymentPeriodUnit(po.getPaymentPeriodUnit() == null ? null : po.getPaymentPeriodUnit().name());
        purchase.setPaymentDueDate(po.getDueDate());
        purchase.setPaymentStatus(po.getPaymentMethod().equals(PaymentMethod.CASH) ? PaymentStatus.PAID.name()
                : PaymentStatus.UNPAID.name());
        purchase.setSupplierId(po.getSupplierId());
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
            product.setQuantity(lastQuantity == null ? purchaseProduct.getPurchaseQuantity()
                    : lastQuantity + purchaseProduct.getPurchaseQuantity());
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

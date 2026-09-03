package com.pinodesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pinodesk.annotation.TargetActivity;
import com.pinodesk.constant.Activity;
import com.pinodesk.constant.CacheNameConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.Consignment;
import com.pinodesk.entity.ConsignmentDetail;
import com.pinodesk.entity.Product;
import com.pinodesk.entity.ProductExpiry;
import com.pinodesk.entity.ProductPrice;
import com.pinodesk.entity.ProductStock;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.ConsignmentDetailRepository;
import com.pinodesk.repository.ConsignmentRepository;
import com.pinodesk.repository.ProductExpiryRepository;
import com.pinodesk.repository.ProductPriceRepository;
import com.pinodesk.repository.ProductRepository;
import com.pinodesk.repository.ProductStockRepository;
import com.pinodesk.viewmodel.ConsignmentAddVM;
import com.pinodesk.viewmodel.ConsignmentFilterVM;
import com.pinodesk.viewmodel.ConsignmentProductVM;
import com.pinodesk.viewmodel.ConsignmentVM;

@Service
public class ConsignmentService extends BaseService {

    @Autowired
    private ConsignmentRepository consignmentRepository;

    @Autowired
    private ConsignmentDetailRepository consignmentDetailRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private ProductExpiryRepository productExpiryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SessionService sessionService;

    @TargetActivity(Activity.SEARCH_CONSIGNMENTS_BY_FILTER)
    @Cacheable(CacheNameConstants.CONSIGNMENTS_BY_FILTER)
    public List<ConsignmentVM> searchConsignments(ConsignmentFilterVM filter) {
        return objectConverter.convertList(consignmentRepository.findByFilter(filter), ConsignmentVM.class);
    }

    @TargetActivity(Activity.REMOVE_CONSIGNMENTS)
    @CacheEvict(value = {
            CacheNameConstants.CONSIGNMENTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PAYABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void removeConsignments(List<Long> consignmentIds) {

    }

    @TargetActivity(Activity.ADD_CONSIGNMENTS)
    @CacheEvict(value = {
            CacheNameConstants.CONSIGNMENTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PAYABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void createConsignment(ConsignmentAddVM consignmentAdd) {
        String activityName = Activity.ADD_CONSIGNMENTS.toString();
        String invoiceNumber = consignmentAdd.getInvoiceNumber();
        if (consignmentRepository.existsByInvoiceNumberIgnoreCaseAndSupplierIdAndDeletedAtIsNull(
                invoiceNumber,
                consignmentAdd.getSupplierId())) {
            throw new DomainException(DomainError.CONSIGNMENT_EXISTS_BY_INVOICE_NUMBER_AND_SUPPLIER_ID);
        }
        Consignment consignment = new Consignment();
        consignment.setInvoiceDate(consignmentAdd.getInvoiceDate());
        consignment.setInvoiceNumber(invoiceNumber);
        consignment.setSupplierId(consignmentAdd.getSupplierId());
        consignment.setTotalProduct(consignmentAdd.getTotalProduct());
        consignment.setUserId(sessionService.getCurrentSession().getUser().getId());
        Consignment created = consignmentRepository.save(consignment);
        Long consignmentId = created.getId();
        consignmentAdd.getConsignmentProducts().stream().forEach(consignmentProduct -> {
            Long productId = consignmentProduct.getProductId();
            Integer quantity = consignmentProduct.getQuantity();
            Product product = productRepository.findByIdAndDeletedAtIsNull(productId).orElseThrow();
            createConsignmentDetail(consignmentId, consignmentProduct, productId, quantity);
            if (consignmentProduct.getExpiredDate() != null) {
                createProductExpiry(
                        activityName,
                        invoiceNumber,
                        consignmentId,
                        consignmentProduct,
                        productId,
                        quantity);
            }
            Integer lastStockQuantity = productStockRepository
                    .findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).stream()
                    .map(ProductStock::getFinalQuantity).findAny().orElse(0);
            Integer nextStockQuantity = lastStockQuantity + quantity;
            createProductStock(activityName, invoiceNumber, consignmentId, productId, quantity, nextStockQuantity);
            createProductPrice(activityName, invoiceNumber, consignmentId, consignmentProduct, productId);
            updateProduct(consignmentProduct, product, nextStockQuantity);
        });
    }

    private void createConsignmentDetail(
            Long consignmentId,
            ConsignmentProductVM consignmentProduct,
            Long productId,
            Integer qty) {
        ConsignmentDetail cd = new ConsignmentDetail();
        cd.setPrice(consignmentProduct.getSupplierPrice());
        cd.setProductId(productId);
        cd.setConsignmentId(consignmentId);
        cd.setQuantity(qty);
        consignmentDetailRepository.save(cd);
    }

    private void updateProduct(ConsignmentProductVM consignmentProduct, Product product, Integer nextStockQuantity) {
        Long productId = product.getId();
        productExpiryRepository.findClosestExpiredDateAvailableByProductId(productId)
                .ifPresentOrElse(product::setClosestExpiredDate, () -> product.setClosestExpiredDate(null));
        // Set the average buying price with null since we never buy from the
        // supplier for consignment products
        product.setAverageBuyingPrice(null);
        product.setGeneralSellingPrice(consignmentProduct.getGeneralSellingPrice());
        product.setPrescriptionSellingPrice(consignmentProduct.getPrescriptionSellingPrice());
        product.setQuantity(nextStockQuantity);
        productRepository.save(product);
    }

    private void createProductExpiry(
            String activityName,
            String invoiceNumber,
            Long consignmentId,
            ConsignmentProductVM consignmentProduct,
            Long productId,
            Integer purchaseQuantity) {
        Integer lastExpiryQuantity = productExpiryRepository.findFirstByProductIdOrderByIdDesc(productId).stream()
                .map(ProductExpiry::getFinalQuantity).findAny().orElse(0);
        Integer finalQuantityExpiredDate = productExpiryRepository
                .findFirstByProductIdAndExpiredDateOrderByIdDesc(productId, consignmentProduct.getExpiredDate())
                .map(ProductExpiry::getFinalQuantityExpiredDate).orElse(0);
        ProductExpiry px = new ProductExpiry();
        px.setActivity(activityName);
        px.setBatchNumber(consignmentProduct.getBatchNumber());
        px.setExpiredDate(consignmentProduct.getExpiredDate());
        px.setFinalQuantity(lastExpiryQuantity + purchaseQuantity);
        px.setFinalQuantityExpiredDate(finalQuantityExpiredDate + purchaseQuantity);
        px.setProductId(productId);
        px.setConsignmentId(consignmentId);
        px.setConsignmentInvoiceNumber(invoiceNumber);
        px.setQuantityIn(purchaseQuantity);
        px.setUserId(sessionService.getCurrentSession().getUser().getId());
        productExpiryRepository.save(px);
    }

    private void createProductStock(
            String activityName,
            String invoiceNumber,
            Long consignmentId,
            Long productId,
            Integer purchaseQuantity,
            Integer nextStockQuantity) {
        ProductStock ps = new ProductStock();
        ps.setActivity(activityName);
        ps.setProductId(productId);
        ps.setConsignmentId(consignmentId);
        ps.setConsignmentInvoiceNumber(invoiceNumber);
        ps.setQuantityIn(purchaseQuantity);
        ps.setFinalQuantity(nextStockQuantity);
        ps.setUserId(sessionService.getCurrentSession().getUser().getId());
        productStockRepository.save(ps);
    }

    private void createProductPrice(
            String activityName,
            String invoiceNumber,
            Long consignmentId,
            ConsignmentProductVM consignmentProduct,
            Long productId) {
        ProductPrice pp = new ProductPrice();
        pp.setActivity(activityName);
        pp.setGeneralSellingPrice(consignmentProduct.getGeneralSellingPrice());
        pp.setPrescriptionSellingPrice(consignmentProduct.getPrescriptionSellingPrice());
        pp.setProductId(productId);
        pp.setConsignmentId(consignmentId);
        pp.setConsignmentInvoiceNumber(invoiceNumber);
        pp.setUserId(sessionService.getCurrentSession().getUser().getId());
        productPriceRepository.save(pp);
    }

}
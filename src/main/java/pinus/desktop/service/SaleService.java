package pinus.desktop.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.Activity;
import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.constant.SellingMode;
import pinus.desktop.domain.Product;
import pinus.desktop.domain.ProductExpiry;
import pinus.desktop.domain.ProductPrice;
import pinus.desktop.domain.ProductStock;
import pinus.desktop.domain.Sale;
import pinus.desktop.domain.SaleDetail;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.ProductExpiryRepository;
import pinus.desktop.repository.ProductPriceRepository;
import pinus.desktop.repository.ProductRepository;
import pinus.desktop.repository.ProductStockRepository;
import pinus.desktop.repository.SaleDetailRepository;
import pinus.desktop.repository.SaleRepository;
import pinus.desktop.viewmodel.SaleAddVM;
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

    @Cacheable(CacheNameConstants.SALES_BY_FILTER)
    public List<SaleVM> searchSales(SaleFilterVM filter) {
        return saleRepository.findByFilter(filter);
    }

    @CacheEvict(value = { CacheNameConstants.SALES_BY_FILTER }, allEntries = true)
    @Transactional
    public void removeSales(List<Long> ids) {
        saleDetailRepository.deleteUpdateBySaleIdIn(ids);
        saleRepository.deleteUpdateByIdIn(ids);
    }

    @CacheEvict(value = {
            CacheNameConstants.SALES_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void createSale(SaleAddVM saleAdd) {
        String activityName = Activity.ADD_SALE.name();
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
        sale.setPaymentStatus(saleAdd.getPaymentStatus().name());
        sale.setSellingMode(saleAdd.getSellingMode().name());
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
            Integer finalQuantity = createProductStock(activityName, saleId, saleProduct, saleAdd);
            createSaleDetail(saleId, saleProduct);
            if (saleProduct.getExpiredDate() != null) {
                createProductExpiry(activityName, saleId, saleProduct, saleAdd);
            }
            product.setQuantity(finalQuantity);
            productRepository.save(product);
        });
    }

    private void createProductExpiry(String activityName, Long saleId, SaleProductVM saleProduct, SaleAddVM saleAdd) {
        ProductExpiry pxByProductId = productExpiryRepository
                .findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(saleProduct.getProductId()).orElseThrow();
        Integer saleQuantity = saleProduct.getSaleQuantity();
        ProductExpiry px = new ProductExpiry();
        px.setActivity(activityName);
        px.setExpiredDate(saleProduct.getExpiredDate());
        px.setFinalQuantity(pxByProductId.getFinalQuantity() - saleQuantity);
        px.setProductId(saleProduct.getProductId());
        px.setQuantityOut(saleQuantity);
        px.setSaleId(saleId);
        px.setSaleInvoiceNumber(saleAdd.getInvoiceNumber());
        px.setUserId(1l);
        productExpiryRepository.save(px);
    }

    private Integer createProductStock(String activityName, Long saleId, SaleProductVM saleProduct, SaleAddVM saleAdd) {
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
        ps.setSaleInvoiceNumber(saleAdd.getInvoiceNumber());
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

}

package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.annotation.Transactional;

import com.pinodesk.constant.Activity;
import com.pinodesk.constant.DomainError;
import com.pinodesk.constant.PaymentStatus;
import com.pinodesk.constant.SellingMode;
import com.pinodesk.entity.Product;
import com.pinodesk.entity.ProductExpiry;
import com.pinodesk.entity.ProductPrice;
import com.pinodesk.entity.ProductStock;
import com.pinodesk.entity.Receivable;
import com.pinodesk.entity.Sale;
import com.pinodesk.entity.SaleDetail;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.PackageDetailRepository;
import com.pinodesk.repository.ProductExpiryRepository;
import com.pinodesk.repository.ProductPriceRepository;
import com.pinodesk.repository.ProductRepository;
import com.pinodesk.repository.ProductStockRepository;
import com.pinodesk.repository.ReceivablePaymentRepository;
import com.pinodesk.repository.ReceivableRepository;
import com.pinodesk.repository.SaleDetailRepository;
import com.pinodesk.repository.SaleRepository;
import com.pinodesk.viewmodel.CurrentSessionVM;
import com.pinodesk.viewmodel.SaleAddVM;
import com.pinodesk.viewmodel.SaleProductVM;
import com.pinodesk.viewmodel.UserVM;

class SaleServiceTest extends BaseServiceTest {
    @Mock
    private PaymentMethodService paymentMethodService;
    @Mock
    private SaleRepository sales;
    @Mock
    private SaleDetailRepository details;
    @Mock
    private ProductRepository products;
    @Mock
    private ProductExpiryRepository expiries;
    @Mock
    private ProductStockRepository stocks;
    @Mock
    private ProductPriceRepository prices;
    @Mock
    private ReceivableRepository receivables;
    @Mock
    private ReceivablePaymentRepository receivablePayments;
    @Mock
    private PackageDetailRepository packageDetails;
    @Mock
    private ConfigurationService configuration;
    @Mock
    private SessionService sessions;
    @InjectMocks
    private SaleService service;

    @BeforeEach
    void setUpSession() {
        UserVM user = new UserVM();
        user.setId(8L);
        CurrentSessionVM currentSession = new CurrentSessionVM();
        currentSession.setUser(user);
        when(sessions.getCurrentSession()).thenReturn(currentSession);
    }

    @Test
    void createSalePersistsStockExpiryPriceAndUnpaidReceivable() {
        SaleAddVM addition = saleAddition(3);
        Product product = productWithQuantity(10);
        when(sales.existsByInvoiceNumberIgnoreCaseAndDeletedAtIsNull("SALE-001")).thenReturn(false);
        when(sales.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale sale = invocation.getArgument(0);
            sale.setId(5L);
            sale.setCreatedAt(LocalDateTime.of(2026, 11, 9, 10, 0));
            return sale;
        });
        when(details.save(any(SaleDetail.class))).thenAnswer(invocation -> {
            SaleDetail detail = invocation.getArgument(0);
            detail.setId(11L);
            return detail;
        });
        when(products.findByIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(product));
        when(products.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stocks.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(7L)).thenReturn(Optional.of(stockBalance(10)));
        ProductExpiry priorExpiry = new ProductExpiry();
        priorExpiry.setExpiredDate(LocalDate.of(2027, 3, 31));
        priorExpiry.setFinalQuantity(10);
        priorExpiry.setFinalQuantityExpiredDate(6);
        when(expiries.findFirstByProductIdOrderByIdDesc(7L)).thenReturn(Optional.of(priorExpiry));

        service.createSale(addition, Activity.ADD_SALE);

        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(sales).save(saleCaptor.capture());
        assertEquals("SALE-001", saleCaptor.getValue().getInvoiceNumber());
        assertEquals("unpaid", saleCaptor.getValue().getPaymentStatus());
        assertEquals(8L, saleCaptor.getValue().getUserId());

        ArgumentCaptor<SaleDetail> detailCaptor = ArgumentCaptor.forClass(SaleDetail.class);
        verify(details).save(detailCaptor.capture());
        assertEquals(5L, detailCaptor.getValue().getSaleId());
        assertEquals(7L, detailCaptor.getValue().getProductId());
        assertEquals(3, detailCaptor.getValue().getQuantity());

        ArgumentCaptor<ProductStock> stockCaptor = ArgumentCaptor.forClass(ProductStock.class);
        verify(stocks).save(stockCaptor.capture());
        assertEquals(7, stockCaptor.getValue().getFinalQuantity());
        assertEquals(3, stockCaptor.getValue().getQuantityOut());
        assertEquals(5L, stockCaptor.getValue().getSaleId());
        assertEquals(8L, stockCaptor.getValue().getUserId());
        assertEquals("SALE-001", stockCaptor.getValue().getSaleInvoiceNumber());

        ArgumentCaptor<ProductPrice> priceCaptor = ArgumentCaptor.forClass(ProductPrice.class);
        verify(prices).save(priceCaptor.capture());
        assertEquals(new BigDecimal("12.50"), priceCaptor.getValue().getGeneralSellingPrice());
        assertEquals(5L, priceCaptor.getValue().getSaleId());
        assertEquals(new BigDecimal("12.50"), product.getGeneralSellingPrice());
        assertEquals(7, product.getQuantity());

        ArgumentCaptor<ProductExpiry> expiryCaptor = ArgumentCaptor.forClass(ProductExpiry.class);
        verify(expiries).save(expiryCaptor.capture());
        assertEquals(11L, expiryCaptor.getValue().getSaleDetailId());
        assertEquals(7, expiryCaptor.getValue().getFinalQuantity());
        assertEquals(3, expiryCaptor.getValue().getFinalQuantityExpiredDate());

        ArgumentCaptor<Receivable> receivableCaptor = ArgumentCaptor.forClass(Receivable.class);
        verify(receivables).save(receivableCaptor.capture());
        assertEquals(5L, receivableCaptor.getValue().getSaleId());
        assertEquals(4L, receivableCaptor.getValue().getCustomerId());
        assertEquals(new BigDecimal("37.50"), receivableCaptor.getValue().getAmount());
        assertEquals(LocalDate.of(2026, 11, 9), receivableCaptor.getValue().getInvoiceDate());
        verify(products, times(2)).save(product);
    }

    @Test
    void insufficientStockDoesNotPersistInventoryOrReceivableChanges() throws NoSuchMethodException {
        SaleAddVM addition = saleAddition(11);
        Product product = productWithQuantity(10);
        product.setGeneralSellingPrice(new BigDecimal("12.50"));
        when(sales.existsByInvoiceNumberIgnoreCaseAndDeletedAtIsNull("SALE-001")).thenReturn(false);
        when(sales.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale sale = invocation.getArgument(0);
            sale.setId(5L);
            return sale;
        });
        when(products.findByIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(product));
        when(stocks.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(7L)).thenReturn(Optional.of(stockBalance(10)));

        DomainException error = assertThrows(
                DomainException.class,
                () -> service.createSale(addition, Activity.ADD_SALE));

        assertEquals(DomainError.PRODUCT_QUANTITY_INSUFFICIENT_FOR_SALE, error.getError());
        verify(stocks, never()).save(any(ProductStock.class));
        verify(expiries, never()).save(any(ProductExpiry.class));
        verify(receivables, never()).save(any(Receivable.class));
        verify(products, never()).save(any(Product.class));
        verifyNoInteractions(prices);
        assertEquals(10, product.getQuantity());
        assertTrue(
                SaleService.class.getMethod("createSale", SaleAddVM.class, Activity.class)
                        .isAnnotationPresent(Transactional.class));
    }

    @Test
    void duplicateInvoiceIsRejectedBeforeSaleOrInventoryWrites() {
        when(sales.existsByInvoiceNumberIgnoreCaseAndDeletedAtIsNull("SALE-001")).thenReturn(true);

        DomainException error = assertThrows(
                DomainException.class,
                () -> service.createSale(saleAddition(3), Activity.ADD_SALE));

        assertEquals(DomainError.SALE_EXISTS_BY_INVOICE_NUMBER, error.getError());
        verify(sales, never()).save(any(Sale.class));
        verifyNoInteractions(
                details,
                products,
                expiries,
                stocks,
                prices,
                receivables,
                receivablePayments,
                packageDetails);
    }

    private SaleAddVM saleAddition(int quantity) {
        SaleProductVM item = new SaleProductVM();
        item.setProductId(7L);
        item.setProductName("Medication");
        item.setProductCategoryCode("standard");
        item.setCurrentQuantity(10 - quantity);
        item.setSaleQuantity(quantity);
        item.setSellingPrice(new BigDecimal("12.50"));
        item.setSubtotal(new BigDecimal("12.50").multiply(BigDecimal.valueOf(quantity)));
        item.setExpiredDate(LocalDate.of(2027, 3, 31));

        SaleAddVM addition = new SaleAddVM();
        addition.setCustomerId(4L);
        addition.setSellingMode(SellingMode.GENERAL);
        addition.setInvoiceNumber("SALE-001");
        addition.setInvoiceDate(LocalDate.of(2026, 11, 9));
        addition.setPaymentDueDate(LocalDate.of(2026, 12, 9));
        addition.setPaymentStatus(PaymentStatus.UNPAID);
        addition.setTotalProduct(1);
        addition.setTotalSale(item.getSubtotal());
        addition.setTotalPayment(item.getSubtotal());
        addition.setSaleProducts(List.of(item));
        return addition;
    }

    private Product productWithQuantity(int quantity) {
        Product product = new Product();
        product.setId(7L);
        product.setName("Medication");
        product.setQuantity(quantity);
        return product;
    }

    private ProductStock stockBalance(int quantity) {
        ProductStock stock = new ProductStock();
        stock.setFinalQuantity(quantity);
        return stock;
    }
}

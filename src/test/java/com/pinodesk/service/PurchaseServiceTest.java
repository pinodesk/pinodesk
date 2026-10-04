package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.pinodesk.constant.*;
import com.pinodesk.entity.*;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.*;
import com.pinodesk.viewmodel.*;

class PurchaseServiceTest extends BaseServiceTest {
    @Mock
    private PayableRepository payables;
    @Mock
    private PayablePaymentRepository payments;
    @Mock
    private ConfigurationService configuration;
    @Mock
    private PurchaseRepository purchases;
    @Mock
    private PurchaseDetailRepository details;
    @Mock
    private ProductStockRepository stocks;
    @Mock
    private ProductPriceRepository prices;
    @Mock
    private ProductExpiryRepository expiries;
    @Mock
    private ProductRepository products;
    @Mock
    private SessionService sessions;
    @InjectMocks
    private PurchaseService service;

    @BeforeEach
    void session() {
        CurrentSessionVM session = new CurrentSessionVM();
        UserVM user = new UserVM();
        user.setId(8L);
        session.setUser(user);
        when(sessions.getCurrentSession()).thenReturn(session);
    }

    private PurchaseAddVM addition(boolean expiring) {
        PurchaseProductVM item = new PurchaseProductVM();
        item.setProductId(7L);
        item.setQuantity(4);
        item.setBuyingPrice(new BigDecimal("5.1234"));
        item.setGeneralSellingPrice(new BigDecimal("8.4567"));
        item.setPrescriptionSellingPrice(new BigDecimal("7"));
        item.setBatchNumber("LOT-A");
        if (expiring)
            item.setExpiredDate(LocalDate.of(2027, 3, 31));
        PurchaseAddVM add = new PurchaseAddVM();
        add.setPaymentStatus(PaymentStatus.PAID);
        add.setTotalPayment(new BigDecimal("20.4936"));
        add.setSupplierId(2L);
        add.setInvoiceNumber("INV-001");
        add.setInvoiceDate(LocalDate.of(2026, 10, 3));
        add.setTotalProduct(1);
        add.setPurchaseProducts(List.of(item));
        return add;
    }

    private Product savedProduct() {
        when(purchases.save(any(Purchase.class))).thenAnswer(inv -> {
            Purchase c = inv.getArgument(0);
            c.setId(3L);
            return c;
        });
        PurchaseDetail prior = new PurchaseDetail();
        prior.setBuyingPrice(new BigDecimal("5.1234"));
        when(details.findByProductIdAndDeletedAtIsNull(7L)).thenReturn(List.of(prior));
        Product p = new Product();
        p.setId(7L);
        p.setAverageBuyingPrice(BigDecimal.TEN);
        p.setClosestExpiredDate(LocalDate.of(2027, 1, 1));
        when(products.findByIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(p));
        return p;
    }

    @Test
    void duplicateInvoiceForSupplierIsRejectedBeforeWrites() {
        when(purchases.existsByInvoiceNumberIgnoreCaseAndSupplierIdAndDeletedAtIsNull("INV-001", 2L)).thenReturn(true);
        assertEquals(
                DomainError.PURCHASE_EXISTS_BY_INVOICE_NUMBER_AND_SUPPLIER_ID,
                assertThrows(DomainException.class, () -> service.createPurchase(addition(false))).getError());
        verify(purchases, never()).save(any());
        verifyNoInteractions(details, stocks, prices, expiries, products);
    }

    @Test
    void firstPurchaseRecordsStockAndAverageBuyingCost() {
        Product product = savedProduct();
        service.createPurchase(addition(false));
        ArgumentCaptor<Purchase> c = ArgumentCaptor.forClass(Purchase.class);
        verify(purchases).save(c.capture());
        assertEquals(2L, c.getValue().getSupplierId());
        assertEquals(8L, c.getValue().getUserId());
        assertEquals("INV-001", c.getValue().getInvoiceNumber());
        ArgumentCaptor<PurchaseDetail> d = ArgumentCaptor.forClass(PurchaseDetail.class);
        verify(details).save(d.capture());
        assertEquals(3L, d.getValue().getPurchaseId());
        assertEquals(7L, d.getValue().getProductId());
        assertEquals(4, d.getValue().getQuantity());
        assertEquals(new BigDecimal("5.1234"), d.getValue().getBuyingPrice());
        ArgumentCaptor<ProductStock> s = ArgumentCaptor.forClass(ProductStock.class);
        verify(stocks).save(s.capture());
        assertEquals(4, s.getValue().getQuantityIn());
        assertEquals(4, s.getValue().getFinalQuantity());
        assertEquals(8L, s.getValue().getUserId());
        assertEquals(3L, s.getValue().getPurchaseId());
        assertEquals("INV-001", s.getValue().getPurchaseInvoiceNumber());
        ArgumentCaptor<ProductPrice> p = ArgumentCaptor.forClass(ProductPrice.class);
        verify(prices).save(p.capture());
        assertEquals(new BigDecimal("8.4567"), p.getValue().getGeneralSellingPrice());
        assertEquals(new BigDecimal("7"), p.getValue().getPrescriptionSellingPrice());
        assertEquals(Activity.ADD_PURCHASE.toString(), p.getValue().getActivity());
        assertEquals(8L, p.getValue().getUserId());
        verify(expiries, never()).save(any());
        assertEquals(new BigDecimal("5.1234"), product.getAverageBuyingPrice());
        assertNull(product.getClosestExpiredDate());
        assertEquals(4, product.getQuantity());
        assertEquals(new BigDecimal("8.4567"), product.getGeneralSellingPrice());
        verify(products).save(product);
    }

    @Test
    void repeatPurchaseAddsToGlobalAndDatedBalances() {
        Product product = savedProduct();
        PurchaseAddVM add = addition(true);
        LocalDate date = add.getPurchaseProducts().get(0).getExpiredDate();
        ProductStock previousStock = new ProductStock();
        previousStock.setFinalQuantity(10);
        when(stocks.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(7L)).thenReturn(Optional.of(previousStock));
        ProductExpiry previous = new ProductExpiry();
        previous.setFinalQuantity(20);
        previous.setFinalQuantityExpiredDate(6);
        when(expiries.findFirstByProductIdOrderByIdDesc(7L)).thenReturn(Optional.of(previous));
        when(expiries.findFirstByProductIdAndExpiredDateOrderByIdDesc(7L, date)).thenReturn(Optional.of(previous));
        when(expiries.findClosestExpiredDateAvailableByProductId(7L)).thenReturn(Optional.of(date));
        service.createPurchase(add);
        ArgumentCaptor<ProductExpiry> e = ArgumentCaptor.forClass(ProductExpiry.class);
        verify(expiries).save(e.capture());
        assertEquals(24, e.getValue().getFinalQuantity());
        assertEquals(10, e.getValue().getFinalQuantityExpiredDate());
        assertEquals(4, e.getValue().getQuantityIn());
        assertEquals("LOT-A", e.getValue().getBatchNumber());
        assertEquals(3L, e.getValue().getPurchaseId());
        assertEquals(8L, e.getValue().getUserId());
        assertEquals(date, e.getValue().getExpiredDate());
        assertEquals(14, product.getQuantity());
        assertEquals(date, product.getClosestExpiredDate());
    }

    @Test
    void firstExpiryUsesZeroForBothBalances() {
        savedProduct();
        service.createPurchase(addition(true));
        ArgumentCaptor<ProductExpiry> e = ArgumentCaptor.forClass(ProductExpiry.class);
        verify(expiries).save(e.capture());
        assertEquals(4, e.getValue().getFinalQuantity());
        assertEquals(4, e.getValue().getFinalQuantityExpiredDate());
    }

    @Test
    void missingProductPreventsDetailAndHistoryWrites() {
        when(purchases.save(any(Purchase.class))).thenAnswer(inv -> {
            Purchase c = inv.getArgument(0);
            c.setId(3L);
            return c;
        });
        assertThrows(java.util.NoSuchElementException.class, () -> service.createPurchase(addition(false)));
        verifyNoInteractions(details, stocks, prices, expiries);
        verify(products, never()).save(any());
    }

    private Purchase existingPurchase(String status) {
        Purchase p = new Purchase();
        p.setId(3L);
        p.setInvoiceNumber("INV-001");
        p.setSupplierId(2L);
        p.setPaymentStatus(status);
        when(purchases.findByIdAndDeletedAtIsNull(3L)).thenReturn(Optional.of(p));
        return p;
    }

    private PurchaseEditVM emptyEdit(PaymentStatus status) {
        PurchaseEditVM e = new PurchaseEditVM();
        e.setInvoiceNumber("INV-001");
        e.setSupplierId(2L);
        e.setPaymentStatus(status);
        e.setPurchaseProducts(List.of());
        e.setTotalPayment(new BigDecimal("123.4567"));
        e.setPaymentDueDate(LocalDate.of(2026, 11, 1));
        return e;
    }

    @Test
    void unpaidPurchaseCreatesPayableWithInvoiceAndExactAmount() {
        savedProduct();
        PurchaseAddVM a = addition(false);
        a.setPaymentStatus(PaymentStatus.UNPAID);
        a.setPaymentDueDate(LocalDate.of(2026, 11, 1));
        service.createPurchase(a);
        ArgumentCaptor<Payable> c = ArgumentCaptor.forClass(Payable.class);
        verify(payables).save(c.capture());
        assertEquals(3L, c.getValue().getPurchaseId());
        assertEquals(2L, c.getValue().getSupplierId());
        assertEquals(a.getTotalPayment(), c.getValue().getAmount());
        assertEquals(a.getPaymentDueDate(), c.getValue().getDueDate());
    }

    @Test
    void markingUnpaidPurchasePaidDeletesExistingBalanceWithoutPayments() {
        Purchase p = existingPurchase("unpaid");
        Payable b = new Payable();
        b.setId(9L);
        when(payables.findByPurchaseId(3L)).thenReturn(Optional.of(b));
        service.updatePurchase(emptyEdit(PaymentStatus.PAID), 3L);
        verify(payments).deleteByPayableId(9L);
        verify(payables).delete(b);
        assertEquals("paid", p.getPaymentStatus());
        verify(details).deleteByPurchaseId(3L);
    }

    @Test
    void cannotMarkPaidWhileRecordedPaymentsExist() {
        existingPurchase("unpaid");
        when(payments.existsByPurchaseId(3L)).thenReturn(true);
        assertEquals(
                DomainError.PAYABLE_PAYMENT_EXISTS_BY_SALE_ID,
                assertThrows(DomainException.class, () -> service.updatePurchase(emptyEdit(PaymentStatus.PAID), 3L))
                        .getError());
        verify(purchases, never()).save(any());
        verify(details, never()).deleteByPurchaseId(anyLong());
    }

    @Test
    void reopeningPaidPurchaseCreatesBalanceUnlessAlreadyCompleted() {
        Purchase p = existingPurchase("paid");
        service.updatePurchase(emptyEdit(PaymentStatus.UNPAID), 3L);
        ArgumentCaptor<Payable> c = ArgumentCaptor.forClass(Payable.class);
        verify(payables).save(c.capture());
        assertEquals(new BigDecimal("123.4567"), c.getValue().getAmount());
        assertEquals("unpaid", p.getPaymentStatus());
        p.setPaymentStatus("paid");
        when(payables.findByPurchaseId(3L)).thenReturn(Optional.of(c.getValue()));
        assertEquals(
                DomainError.PAYABLE_ALREADY_COMPLETED_BY_SALE_ID,
                assertThrows(DomainException.class, () -> service.updatePurchase(emptyEdit(PaymentStatus.UNPAID), 3L))
                        .getError());
    }

    @Test
    void unchangedPaymentStatusSynchronizesBalanceAmountAndDueDate() {
        existingPurchase("unpaid");
        Payable b = new Payable();
        when(payables.findByPurchaseId(3L)).thenReturn(Optional.of(b));
        PurchaseEditVM e = emptyEdit(PaymentStatus.UNPAID);
        service.updatePurchase(e, 3L);
        verify(payables).save(b);
        assertEquals(e.getTotalPayment(), b.getAmount());
        assertEquals(e.getPaymentDueDate(), b.getDueDate());
    }

    @Test
    void unknownPurchaseAndDuplicateInvoiceChangesAreRejected() {
        PurchaseEditVM e = emptyEdit(PaymentStatus.PAID);
        assertEquals(
                DomainError.PURCHASE_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.updatePurchase(e, 3L)).getError());
        existingPurchase("paid");
        e.setInvoiceNumber("OTHER");
        when(purchases.existsByInvoiceNumberIgnoreCaseAndSupplierIdAndDeletedAtIsNull("OTHER", 2L)).thenReturn(true);
        assertEquals(
                DomainError.PURCHASE_OTHER_EXISTS_BY_INVOICE_NUMBER_AND_SUPPLIER_ID,
                assertThrows(DomainException.class, () -> service.updatePurchase(e, 3L)).getError());
        verify(purchases, never()).save(any());
    }

    @Test
    void searchConvertsInvoiceDataAndRetainsFilter() {
        PurchaseFilterVM filter = new PurchaseFilterVM();
        PurchaseVM c = new PurchaseVM();
        c.setId(3L);
        c.setInvoiceNumber("INV-001");
        when(purchases.findByFilter(filter)).thenReturn(List.of(c));
        List<PurchaseVM> result = service.searchPurchases(filter);
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals("INV-001", result.get(0).getInvoiceNumber());
    }
}

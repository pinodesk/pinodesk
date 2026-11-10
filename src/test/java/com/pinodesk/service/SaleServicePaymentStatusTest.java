package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

import com.pinodesk.constant.DomainError;
import com.pinodesk.constant.PaymentStatus;
import com.pinodesk.constant.SellingMode;
import com.pinodesk.entity.Receivable;
import com.pinodesk.entity.Sale;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.ProductRepository;
import com.pinodesk.repository.ReceivablePaymentRepository;
import com.pinodesk.repository.ReceivableRepository;
import com.pinodesk.repository.SaleDetailRepository;
import com.pinodesk.repository.SaleRepository;
import com.pinodesk.viewmodel.CurrentSessionVM;
import com.pinodesk.viewmodel.SaleEditVM;
import com.pinodesk.viewmodel.UserVM;

class SaleServicePaymentStatusTest extends BaseServiceTest {
    @Mock
    private SaleRepository sales;
    @Mock
    private SaleDetailRepository details;
    @Mock
    private ProductRepository products;
    @Mock
    private ReceivableRepository receivables;
    @Mock
    private ReceivablePaymentRepository receivablePayments;
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
    void updateSaleToPaidRemovesReceivableWhenNoPaymentsExist() {
        Sale sale = existingSale("unpaid");
        Receivable receivable = new Receivable();
        receivable.setId(9L);
        when(receivables.findBySaleId(5L)).thenReturn(Optional.of(receivable));
        when(receivablePayments.existsBySaleId(5L)).thenReturn(false);

        SaleEditVM edit = saleEdit(PaymentStatus.PAID);
        service.updateSale(edit, 5L);

        verify(receivablePayments).deleteByReceivableId(9L);
        verify(receivables).delete(receivable);
        assertEquals("paid", sale.getPaymentStatus());
    }

    @Test
    void updateSaleToPaidRejectsSaleWithReceivablePayments() {
        existingSale("unpaid");
        Receivable receivable = new Receivable();
        when(receivables.findBySaleId(5L)).thenReturn(Optional.of(receivable));
        when(receivablePayments.existsBySaleId(5L)).thenReturn(true);

        SaleEditVM edit = saleEdit(PaymentStatus.PAID);
        DomainException error = assertThrows(DomainException.class, () -> service.updateSale(edit, 5L));

        assertEquals(DomainError.RECEIVABLE_PAYMENT_EXISTS_BY_SALE_ID, error.getError());
        verify(sales, never()).save(any(Sale.class));
        verify(receivables, never()).delete(any(Receivable.class));
    }

    @Test
    void updateSaleToUnpaidCreatesReceivableForCustomer() {
        existingSale("paid");
        SaleEditVM edit = saleEdit(PaymentStatus.UNPAID);

        service.updateSale(edit, 5L);

        ArgumentCaptor<Receivable> receivableCaptor = ArgumentCaptor.forClass(Receivable.class);
        verify(receivables).save(receivableCaptor.capture());
        Receivable receivable = receivableCaptor.getValue();
        assertEquals(5L, receivable.getSaleId());
        assertEquals(4L, receivable.getCustomerId());
        assertEquals("SALE-001", receivable.getInvoiceNumber());
        assertEquals(LocalDate.of(2026, 11, 9), receivable.getInvoiceDate());
        assertEquals(LocalDate.of(2026, 12, 9), receivable.getDueDate());
        assertEquals(new BigDecimal("42.00"), receivable.getAmount());
    }

    private Sale existingSale(String paymentStatus) {
        Sale sale = new Sale();
        sale.setId(5L);
        sale.setCreatedAt(LocalDateTime.of(2026, 11, 9, 10, 0));
        sale.setInvoiceNumber("SALE-001");
        sale.setPaymentStatus(paymentStatus);
        when(sales.findByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(sale));
        return sale;
    }

    private SaleEditVM saleEdit(PaymentStatus paymentStatus) {
        SaleEditVM edit = new SaleEditVM();
        edit.setCustomerId(4L);
        edit.setSellingMode(SellingMode.GENERAL);
        edit.setInvoiceNumber("SALE-001");
        edit.setInvoiceDate(LocalDate.of(2026, 11, 9));
        edit.setPaymentDueDate(LocalDate.of(2026, 12, 9));
        edit.setPaymentStatus(paymentStatus);
        edit.setTotalProduct(0);
        edit.setTotalSale(new BigDecimal("42.00"));
        edit.setTotalPayment(new BigDecimal("42.00"));
        edit.setSaleProducts(List.of());
        return edit;
    }
}

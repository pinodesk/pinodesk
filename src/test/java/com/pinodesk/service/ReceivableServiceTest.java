package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.*;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.*;
import com.pinodesk.viewmodel.*;

class ReceivableServiceTest extends BaseServiceTest {
    @Mock
    private ReceivableRepository balances;
    @Mock
    private ReceivablePaymentRepository payments;
    @Mock
    private SaleRepository transactions;
    @InjectMocks
    private ReceivableService service;
    private final LocalDate due = LocalDate.of(2026, 11, 1);

    private Receivable existing(Sale sale) {
        Receivable balance = new Receivable();
        balance.setId(1L);
        balance.setSaleId(2L);
        balance.setAmount(new BigDecimal("100.1234"));
        balance.setDueDate(due);
        when(balances.findById(1L)).thenReturn(Optional.of(balance));
        when(transactions.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(sale));
        return balance;
    }

    private ReceivablePaymentVM payment(String amount, LocalDate date) {
        ReceivablePaymentVM p = new ReceivablePaymentVM();
        p.setAmount(new BigDecimal(amount));
        p.setPaymentDate(date);
        return p;
    }

    private ReceivableEditVM edit(ReceivablePaymentVM... entries) {
        ReceivableEditVM edit = new ReceivableEditVM();
        edit.setPayments(List.of(entries));
        return edit;
    }

    @Test
    void fullPaymentUsesLatestDateDespiteInputOrderAndClearsDueDate() {
        Sale sale = new Sale();
        sale.setPaymentDueDate(due);
        Receivable balance = existing(sale);
        LocalDate latest = due.minusDays(1);
        service.updateReceivable(
                edit(
                        payment("20", latest.minusDays(3)),
                        payment("30", latest),
                        payment("50.1234", latest.minusDays(1))),
                1L);
        assertEquals(latest, balance.getCompletionDate());
        assertEquals("paid", sale.getPaymentStatus());
        assertNull(sale.getPaymentDueDate());
        ArgumentCaptor<List<ReceivablePayment>> cap = ArgumentCaptor.forClass(List.class);
        InOrder order = inOrder(payments);
        order.verify(payments).deleteByReceivableId(1L);
        order.verify(payments).saveAll(cap.capture());
        assertEquals(3, cap.getValue().size());
        assertEquals(
                new BigDecimal("100.1234"),
                cap.getValue().stream().map(ReceivablePayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        assertTrue(cap.getValue().stream().allMatch(p -> Long.valueOf(1).equals(p.getReceivableId())));
        verify(transactions).save(sale);
        verify(balances).save(balance);
    }

    @Test
    void partialPaymentReopensBalanceAndRestoresDueDate() {
        Sale sale = new Sale();
        sale.setPaymentStatus("paid");
        Receivable balance = existing(sale);
        balance.setCompletionDate(due.minusDays(1));
        service.updateReceivable(edit(payment("0.0001", due.minusDays(2))), 1L);
        assertNull(balance.getCompletionDate());
        assertEquals("unpaid", sale.getPaymentStatus());
        assertEquals(due, sale.getPaymentDueDate());
    }

    @Test
    void clearingPaymentsReopensBalanceAndPersistsEmptyReplacement() {
        Sale sale = new Sale();
        Receivable balance = existing(sale);
        balance.setCompletionDate(due);
        service.updateReceivable(edit(), 1L);
        assertNull(balance.getCompletionDate());
        assertEquals("unpaid", sale.getPaymentStatus());
        verify(payments).saveAll(List.of());
        verify(balances).save(balance);
    }

    @Test
    void cumulativeOverpaymentIsRejectedBeforeReplacementIsSaved() {
        existing(new Sale());
        assertEquals(
                DomainError.PAYMENT_AMOUNT_GREATER_THAN_RECEIVABLE_AMOUNT,
                assertThrows(
                        DomainException.class,
                        () -> service.updateReceivable(edit(payment("100", due), payment("0.1235", due)), 1L))
                        .getError());
        verify(payments, never()).saveAll(any());
        verify(balances, never()).save(any());
        verify(transactions, never()).save(any());
    }

    @Test
    void unknownBalanceDoesNotDeletePayments() {
        assertEquals(
                DomainError.RECEIVABLE_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.updateReceivable(edit(), 9L)).getError());
        verifyNoInteractions(payments, transactions);
    }

    @Test
    void missingSalePreventsReplacementWrites() {
        existing(new Sale());
        when(transactions.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.empty());
        assertThrows(java.util.NoSuchElementException.class, () -> service.updateReceivable(edit(), 1L));
        verify(payments, never()).saveAll(any());
        verify(balances, never()).save(any());
    }

    @Test
    void queriesPreserveFilterAndMapPaymentsWithoutLosingPrecision() {
        ReceivableFilterVM filter = new ReceivableFilterVM();
        List<ReceivableVM> rows = List.of(new ReceivableVM());
        when(balances.findByFilter(filter)).thenReturn(rows);
        assertSame(rows, service.searchReceivables(filter));
        ReceivablePayment p = new ReceivablePayment();
        p.setAmount(new BigDecimal("0.1234"));
        p.setPaymentDate(due);
        when(payments.findByReceivableIdAndDeletedAtIsNull(1L)).thenReturn(List.of(p));
        List<ReceivablePaymentVM> mapped = service.getReceivablePayments(1L);
        assertEquals(1, mapped.size());
        assertEquals(p.getAmount(), mapped.get(0).getAmount());
        assertEquals(due, mapped.get(0).getPaymentDate());
        when(balances.findByIdJoinCustomer(1L)).thenReturn(Optional.of(rows.get(0)));
        assertSame(rows.get(0), service.getReceivableById(1L));
        assertEquals(
                DomainError.RECEIVABLE_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.getReceivableById(9L)).getError());
    }
}

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

class PayableServiceTest extends BaseServiceTest {
    @Mock
    private PayableRepository balances;
    @Mock
    private PayablePaymentRepository payments;
    @Mock
    private PurchaseRepository transactions;
    @InjectMocks
    private PayableService service;
    private final LocalDate due = LocalDate.of(2026, 11, 1);

    private Payable existing(Purchase purchase) {
        Payable balance = new Payable();
        balance.setId(1L);
        balance.setPurchaseId(2L);
        balance.setAmount(new BigDecimal("100.1234"));
        balance.setDueDate(due);
        when(balances.findById(1L)).thenReturn(Optional.of(balance));
        when(transactions.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(purchase));
        return balance;
    }

    private PayablePaymentVM payment(String amount, LocalDate date) {
        PayablePaymentVM p = new PayablePaymentVM();
        p.setAmount(new BigDecimal(amount));
        p.setPaymentDate(date);
        return p;
    }

    private PayableEditVM edit(PayablePaymentVM... entries) {
        PayableEditVM edit = new PayableEditVM();
        edit.setPayments(List.of(entries));
        return edit;
    }

    @Test
    void fullPaymentUsesLatestDateDespiteInputOrderAndClearsDueDate() {
        Purchase purchase = new Purchase();
        purchase.setPaymentDueDate(due);
        Payable balance = existing(purchase);
        LocalDate latest = due.minusDays(1);
        service.updatePayable(
                edit(
                        payment("20", latest.minusDays(3)),
                        payment("30", latest),
                        payment("50.1234", latest.minusDays(1))),
                1L);
        assertEquals(latest, balance.getCompletionDate());
        assertEquals("paid", purchase.getPaymentStatus());
        assertNull(purchase.getPaymentDueDate());
        ArgumentCaptor<List<PayablePayment>> cap = ArgumentCaptor.forClass(List.class);
        InOrder order = inOrder(payments);
        order.verify(payments).deleteByPayableId(1L);
        order.verify(payments).saveAll(cap.capture());
        assertEquals(3, cap.getValue().size());
        assertEquals(
                new BigDecimal("100.1234"),
                cap.getValue().stream().map(PayablePayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        assertTrue(cap.getValue().stream().allMatch(p -> Long.valueOf(1).equals(p.getPayableId())));
        verify(transactions).save(purchase);
        verify(balances).save(balance);
    }

    @Test
    void partialPaymentReopensBalanceAndRestoresDueDate() {
        Purchase purchase = new Purchase();
        purchase.setPaymentStatus("paid");
        Payable balance = existing(purchase);
        balance.setCompletionDate(due.minusDays(1));
        service.updatePayable(edit(payment("0.0001", due.minusDays(2))), 1L);
        assertNull(balance.getCompletionDate());
        assertEquals("unpaid", purchase.getPaymentStatus());
        assertEquals(due, purchase.getPaymentDueDate());
    }

    @Test
    void clearingPaymentsReopensBalanceAndPersistsEmptyReplacement() {
        Purchase purchase = new Purchase();
        Payable balance = existing(purchase);
        balance.setCompletionDate(due);
        service.updatePayable(edit(), 1L);
        assertNull(balance.getCompletionDate());
        assertEquals("unpaid", purchase.getPaymentStatus());
        verify(payments).saveAll(List.of());
        verify(balances).save(balance);
    }

    @Test
    void cumulativeOverpaymentIsRejectedBeforeReplacementIsSaved() {
        existing(new Purchase());
        assertEquals(
                DomainError.PAYMENT_AMOUNT_GREATER_THAN_PAYABLE_AMOUNT,
                assertThrows(
                        DomainException.class,
                        () -> service.updatePayable(edit(payment("100", due), payment("0.1235", due)), 1L)).getError());
        verify(payments, never()).saveAll(any());
        verify(balances, never()).save(any());
        verify(transactions, never()).save(any());
    }

    @Test
    void unknownBalanceDoesNotDeletePayments() {
        assertEquals(
                DomainError.PAYABLE_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.updatePayable(edit(), 9L)).getError());
        verifyNoInteractions(payments, transactions);
    }

    @Test
    void missingPurchasePreventsReplacementWrites() {
        existing(new Purchase());
        when(transactions.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.empty());
        assertThrows(java.util.NoSuchElementException.class, () -> service.updatePayable(edit(), 1L));
        verify(payments, never()).saveAll(any());
        verify(balances, never()).save(any());
    }

    @Test
    void queriesPreserveFilterAndMapPaymentsWithoutLosingPrecision() {
        PayableFilterVM filter = new PayableFilterVM();
        List<PayableVM> rows = List.of(new PayableVM());
        when(balances.findByFilter(filter)).thenReturn(rows);
        assertSame(rows, service.searchPayables(filter));
        PayablePayment p = new PayablePayment();
        p.setAmount(new BigDecimal("0.1234"));
        p.setPaymentDate(due);
        when(payments.findByPayableIdAndDeletedAtIsNull(1L)).thenReturn(List.of(p));
        List<PayablePaymentVM> mapped = service.getPayablePayments(1L);
        assertEquals(1, mapped.size());
        assertEquals(p.getAmount(), mapped.get(0).getAmount());
        assertEquals(due, mapped.get(0).getPaymentDate());
        when(balances.findByIdJoinSupplier(1L)).thenReturn(Optional.of(rows.get(0)));
        assertSame(rows.get(0), service.getPayableById(1L));
        assertEquals(
                DomainError.PAYABLE_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.getPayableById(9L)).getError());
    }
}

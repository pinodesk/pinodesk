package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.Expense;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.ExpenseRepository;
import com.pinodesk.viewmodel.ExpenseAddVM;
import com.pinodesk.viewmodel.ExpenseFilterVM;
import com.pinodesk.viewmodel.ExpenseVM;

class ExpenseServiceTest {
    private ExpenseRepository repository;
    private SessionService sessionService;
    private ExpenseService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExpenseRepository.class);
        sessionService = mock(SessionService.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);
        service = new ExpenseService(repository, sessionService);
    }

    @Test
    void searchExpensesDelegatesFilterToRepository() {
        ExpenseFilterVM filter = new ExpenseFilterVM();
        List<ExpenseVM> expected = List.of(new ExpenseVM());
        when(repository.findByFilter(filter)).thenReturn(expected);

        assertEquals(expected, service.searchExpenses(filter));
        verify(repository).findByFilter(filter);
    }

    @Test
    void createExpenseMapsInputAndCurrentUserToEntity() {
        when(sessionService.getCurrentSession().getUser().getId()).thenReturn(17L);
        ExpenseAddVM input = expenseInput();

        service.createExpense(input);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(repository).save(captor.capture());
        Expense saved = captor.getValue();
        assertEquals("Office supplies", saved.getTitle());
        assertEquals(2L, saved.getExpenseCategoryId());
        assertEquals(new BigDecimal("125.50"), saved.getNominal());
        assertEquals(LocalDate.of(2026, 11, 7), saved.getExpenseDate());
        assertEquals(3L, saved.getPaymentMethodId());
        assertEquals("Printer paper", saved.getNotes());
        assertEquals(17L, saved.getUserId());
    }

    @Test
    void updateExpenseMapsInputToExistingEntity() {
        Expense existing = new Expense();
        existing.setId(8L);
        when(repository.findById(8L)).thenReturn(Optional.of(existing));
        ExpenseAddVM input = expenseInput();

        service.updateExpense(input, 8L);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(repository).save(captor.capture());
        Expense saved = captor.getValue();
        assertEquals(8L, saved.getId());
        assertEquals("Office supplies", saved.getTitle());
        assertEquals(2L, saved.getExpenseCategoryId());
        assertEquals(new BigDecimal("125.50"), saved.getNominal());
        assertEquals(LocalDate.of(2026, 11, 7), saved.getExpenseDate());
        assertEquals(3L, saved.getPaymentMethodId());
        assertEquals("Printer paper", saved.getNotes());
    }

    @Test
    void updateExpenseRejectsMissingExpense() {
        when(repository.findById(404L)).thenReturn(Optional.empty());
        ExpenseAddVM input = expenseInput();

        DomainException exception = assertThrows(DomainException.class, () -> service.updateExpense(input, 404L));

        assertEquals(DomainError.EXPENSE_NOT_FOUND_BY_ID, exception.getError());
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any(Expense.class));
    }

    @Test
    void removeExpensesDelegatesIdsToRepository() {
        List<Long> ids = List.of(4L, 5L);

        service.removeExpenses(ids);

        verify(repository).deleteUpdateByIdIn(ids);
    }

    private ExpenseAddVM expenseInput() {
        ExpenseAddVM input = new ExpenseAddVM();
        input.setTitle("Office supplies");
        input.setExpenseCategoryId(2L);
        input.setNominal(new BigDecimal("125.50"));
        input.setExpenseDate(LocalDate.of(2026, 11, 7));
        input.setPaymentMethodId(3L);
        input.setNotes("Printer paper");
        return input;
    }
}

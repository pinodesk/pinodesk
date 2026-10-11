package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pinodesk.constant.UserStatus;
import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.ExpenseCategoryRepository;
import com.pinodesk.repository.ExpenseRepository;

class ExpenseCategoryServiceTest {
    private ExpenseCategoryRepository repository;
    private ExpenseRepository expenseRepository;
    private ExpenseCategoryService service;
    private ExpenseCategory existingCategory;

    @BeforeEach
    void setup() {
        repository = mock(ExpenseCategoryRepository.class);
        expenseRepository = mock(ExpenseRepository.class);
        service = new ExpenseCategoryService(repository, expenseRepository);
        existingCategory = new ExpenseCategory();
        existingCategory.setId(1L);
        existingCategory.setName("Office Supplies");
        existingCategory.setStatus(UserStatus.ACTIVE.toString());
    }

    @Test
    void findAllReturnsAllCategoriesOrdered() {
        ExpenseCategory category1 = new ExpenseCategory();
        category1.setName("A Category");
        ExpenseCategory category2 = new ExpenseCategory();
        category2.setName("B Category");

        when(repository.findAllByOrderByNameAsc()).thenReturn(List.of(category1, category2));

        List<ExpenseCategory> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("A Category", result.get(0).getName());
        assertEquals("B Category", result.get(1).getName());
        verify(repository).findAllByOrderByNameAsc();
    }

    @Test
    void saveCreatesNewCategory() {
        when(repository.findByNameIgnoreCase("New Category")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ExpenseCategory result = service.save(null, "New Category", UserStatus.ACTIVE);

        assertEquals("New Category", result.getName());
        assertEquals(UserStatus.ACTIVE.toString(), result.getStatus());
        verify(repository).findByNameIgnoreCase("New Category");
        verify(repository).save(any(ExpenseCategory.class));
    }

    @Test
    void saveUpdatesExistingCategory() {
        when(repository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(repository.findByNameIgnoreCase("Updated Name")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ExpenseCategory result = service.save(1L, "Updated Name", UserStatus.INACTIVE);

        assertEquals("Updated Name", result.getName());
        assertEquals(UserStatus.INACTIVE.toString(), result.getStatus());
        verify(repository).findById(1L);
        verify(repository).save(any(ExpenseCategory.class));
    }

    @Test
    void saveThrowsExceptionForNullName() {
        DomainException ex = assertThrows(DomainException.class, () -> service.save(null, null, UserStatus.ACTIVE));
        assertEquals(DomainError.EXPENSE_CATEGORY_INVALID, ex.getError());
    }

    @Test
    void saveThrowsExceptionForBlankName() {
        DomainException ex = assertThrows(DomainException.class, () -> service.save(null, "   ", UserStatus.ACTIVE));
        assertEquals(DomainError.EXPENSE_CATEGORY_INVALID, ex.getError());
    }

    @Test
    void saveThrowsExceptionForTooLongName() {
        DomainException ex = assertThrows(
                DomainException.class,
                () -> service.save(null, "x".repeat(101), UserStatus.ACTIVE));
        assertEquals(DomainError.EXPENSE_CATEGORY_INVALID, ex.getError());
    }

    @Test
    void saveThrowsExceptionForNullStatus() {
        DomainException ex = assertThrows(DomainException.class, () -> service.save(null, "Valid Name", null));
        assertEquals(DomainError.EXPENSE_CATEGORY_INVALID, ex.getError());
    }

    @Test
    void saveThrowsExceptionForDuplicateName() {
        when(repository.findByNameIgnoreCase("Duplicate")).thenReturn(Optional.of(existingCategory));

        DomainException ex = assertThrows(
                DomainException.class,
                () -> service.save(null, "Duplicate", UserStatus.ACTIVE));
        assertEquals(DomainError.EXPENSE_CATEGORY_DUPLICATE, ex.getError());
    }

    @Test
    void saveAllowsSameNameForSameCategory() {
        when(repository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(repository.findByNameIgnoreCase("Office Supplies")).thenReturn(Optional.of(existingCategory));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ExpenseCategory result = service.save(1L, "Office Supplies", UserStatus.ACTIVE);

        assertEquals("Office Supplies", result.getName());
        verify(repository).save(any(ExpenseCategory.class));
    }

    @Test
    void saveTrimsWhitespaceFromName() {
        when(repository.findByNameIgnoreCase("Trimmed")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ExpenseCategory result = service.save(null, "  Trimmed  ", UserStatus.ACTIVE);

        assertEquals("Trimmed", result.getName());
    }

    @Test
    void removeDeletesCategories() {
        ExpenseCategory category1 = new ExpenseCategory();
        category1.setId(1L);
        ExpenseCategory category2 = new ExpenseCategory();
        category2.setId(2L);

        when(repository.findById(1L)).thenReturn(Optional.of(category1));
        when(repository.findById(2L)).thenReturn(Optional.of(category2));
        when(expenseRepository.existsByExpenseCategoryIdAndDeletedAtIsNull(1L)).thenReturn(false);
        when(expenseRepository.existsByExpenseCategoryIdAndDeletedAtIsNull(2L)).thenReturn(false);

        service.remove(List.of(1L, 2L));

        verify(repository).findById(1L);
        verify(repository).findById(2L);
        verify(expenseRepository).existsByExpenseCategoryIdAndDeletedAtIsNull(1L);
        verify(expenseRepository).existsByExpenseCategoryIdAndDeletedAtIsNull(2L);
        verify(repository).deleteAll(anyList());
    }

    @Test
    void removeThrowsExceptionForNonExistentCategory() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> service.remove(List.of(999L)));
        assertEquals(DomainError.EXPENSE_CATEGORY_NOT_FOUND, ex.getError());
    }

    @Test
    void removeThrowsExceptionForCategoryInUse() {
        ExpenseCategory category = new ExpenseCategory();
        category.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(expenseRepository.existsByExpenseCategoryIdAndDeletedAtIsNull(1L)).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class, () -> service.remove(List.of(1L)));
        assertEquals(DomainError.EXPENSE_CATEGORY_IN_USE, exception.getError());
    }

    @Test
    void removeHandlesDuplicateIds() {
        ExpenseCategory category = new ExpenseCategory();
        category.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(expenseRepository.existsByExpenseCategoryIdAndDeletedAtIsNull(1L)).thenReturn(false);

        service.remove(List.of(1L, 1L));

        verify(repository, times(1)).findById(1L);
        verify(expenseRepository, times(1)).existsByExpenseCategoryIdAndDeletedAtIsNull(1L);
        verify(repository).deleteAll(anyList());
    }
}

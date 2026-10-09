package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.repository.ExpenseCategoryRepository;

class ExpenseCategoryServiceTest {
    private ExpenseCategoryRepository repository;
    private ExpenseCategoryService service;
    private ExpenseCategory existingCategory;

    @BeforeEach
    void setup() {
        repository = mock(ExpenseCategoryRepository.class);
        service = new ExpenseCategoryService(repository);
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
        assertThrows(IllegalArgumentException.class, () -> service.save(null, null, UserStatus.ACTIVE));
    }

    @Test
    void saveThrowsExceptionForBlankName() {
        assertThrows(IllegalArgumentException.class, () -> service.save(null, "   ", UserStatus.ACTIVE));
    }

    @Test
    void saveThrowsExceptionForTooLongName() {
        assertThrows(IllegalArgumentException.class, () -> service.save(null, "x".repeat(101), UserStatus.ACTIVE));
    }

    @Test
    void saveThrowsExceptionForNullStatus() {
        assertThrows(IllegalArgumentException.class, () -> service.save(null, "Valid Name", null));
    }

    @Test
    void saveThrowsExceptionForDuplicateName() {
        when(repository.findByNameIgnoreCase("Duplicate")).thenReturn(Optional.of(existingCategory));

        assertThrows(IllegalArgumentException.class, () -> service.save(null, "Duplicate", UserStatus.ACTIVE));
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

        service.remove(List.of(1L, 2L));

        verify(repository).findById(1L);
        verify(repository).findById(2L);
        verify(repository).deleteAll(anyList());
    }

    @Test
    void removeThrowsExceptionForNonExistentCategory() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.remove(List.of(999L)));
    }

    @Test
    void removeHandlesDuplicateIds() {
        ExpenseCategory category = new ExpenseCategory();
        category.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(category));

        service.remove(List.of(1L, 1L));

        verify(repository, times(1)).findById(1L);
        verify(repository).deleteAll(anyList());
    }
}

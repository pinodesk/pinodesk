package com.pinodesk.service;

import java.util.List;
import java.util.Objects;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pinodesk.constant.CacheNameConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.ExpenseCategoryRepository;
import com.pinodesk.repository.ExpenseRepository;

@Service
public class ExpenseCategoryService {
    private final ExpenseCategoryRepository repository;
    private final ExpenseRepository expenseRepository;

    public ExpenseCategoryService(ExpenseCategoryRepository repository, ExpenseRepository expenseRepository) {
        this.repository = repository;
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseCategory> findAll() {
        return repository.findAllByOrderByNameAsc();
    }

    @Transactional
    @CacheEvict(value = CacheNameConstants.EXPENSES_BY_FILTER, allEntries = true)
    public ExpenseCategory save(Long id, String name, UserStatus status) {
        if (name == null || name.isBlank() || name.trim().length() > 100 || status == null) {
            throw new DomainException(DomainError.EXPENSE_CATEGORY_INVALID);
        }
        String cleanedName = name.trim();
        repository.findByNameIgnoreCase(cleanedName).ifPresent(existing -> {
            if (!Objects.equals(existing.getId(), id)) {
                throw new DomainException(DomainError.EXPENSE_CATEGORY_DUPLICATE);
            }
        });
        ExpenseCategory category = id == null ?
                new ExpenseCategory() :
                repository.findById(id).orElseThrow(() -> new DomainException(DomainError.EXPENSE_CATEGORY_NOT_FOUND));
        category.setName(cleanedName);
        category.setStatus(status.toString());
        try {
            return repository.save(category);
        } catch (DataIntegrityViolationException exception) {
            throw new DomainException(DomainError.EXPENSE_CATEGORY_DUPLICATE);
        }
    }

    @Transactional
    @CacheEvict(value = CacheNameConstants.EXPENSES_BY_FILTER, allEntries = true)
    public void remove(List<Long> ids) {
        List<ExpenseCategory> categories = ids.stream().distinct()
                .map(
                        id -> repository.findById(id)
                                .orElseThrow(() -> new DomainException(DomainError.EXPENSE_CATEGORY_NOT_FOUND)))
                .toList();
        for (ExpenseCategory category : categories) {
            if (expenseRepository.existsByExpenseCategoryIdAndDeletedAtIsNull(category.getId())) {
                throw new DomainException(DomainError.EXPENSE_CATEGORY_IN_USE);
            }
        }
        repository.deleteAll(categories);
    }
}

package com.pinodesk.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pinodesk.annotation.TargetActivity;
import com.pinodesk.constant.Activity;
import com.pinodesk.constant.CacheNameConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.Expense;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.ExpenseRepository;
import com.pinodesk.viewmodel.ExpenseAddVM;
import com.pinodesk.viewmodel.ExpenseFilterVM;
import com.pinodesk.viewmodel.ExpenseVM;

@Service
public class ExpenseService extends BaseService {

    private final ExpenseRepository expenseRepository;
    private final SessionService sessionService;

    public ExpenseService(ExpenseRepository expenseRepository, SessionService sessionService) {
        this.expenseRepository = expenseRepository;
        this.sessionService = sessionService;
    }

    @TargetActivity(Activity.SEARCH_EXPENSES_BY_FILTER)
    @Cacheable(CacheNameConstants.EXPENSES_BY_FILTER)
    public List<ExpenseVM> searchExpenses(ExpenseFilterVM filter) {
        return expenseRepository.findByFilter(filter);
    }

    @TargetActivity(Activity.ADD_EXPENSE)
    @CacheEvict(value = { CacheNameConstants.EXPENSES_BY_FILTER }, allEntries = true)
    @Transactional
    public void createExpense(ExpenseAddVM expenseAdd) {
        Expense expense = new Expense();
        expense.setTitle(expenseAdd.getTitle());
        expense.setExpenseCategoryId(expenseAdd.getExpenseCategoryId());
        expense.setNominal(expenseAdd.getNominal());
        expense.setExpenseDate(expenseAdd.getExpenseDate());
        expense.setPaymentMethodId(expenseAdd.getPaymentMethodId());
        expense.setNotes(expenseAdd.getNotes());
        expense.setUserId(sessionService.getCurrentSession().getUser().getId());
        expenseRepository.save(expense);
    }

    @TargetActivity(Activity.EDIT_EXPENSE)
    @CacheEvict(value = { CacheNameConstants.EXPENSES_BY_FILTER }, allEntries = true)
    @Transactional
    public void updateExpense(ExpenseAddVM expenseEdit, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new DomainException(DomainError.EXPENSE_NOT_FOUND_BY_ID));
        expense.setTitle(expenseEdit.getTitle());
        expense.setExpenseCategoryId(expenseEdit.getExpenseCategoryId());
        expense.setNominal(expenseEdit.getNominal());
        expense.setExpenseDate(expenseEdit.getExpenseDate());
        expense.setPaymentMethodId(expenseEdit.getPaymentMethodId());
        expense.setNotes(expenseEdit.getNotes());
        expenseRepository.save(expense);
    }

    @TargetActivity(Activity.REMOVE_EXPENSES)
    @CacheEvict(value = { CacheNameConstants.EXPENSES_BY_FILTER }, allEntries = true)
    @Transactional
    public void removeExpenses(List<Long> ids) {
        expenseRepository.deleteUpdateByIdIn(ids);
    }
}

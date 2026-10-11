package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.entity.Expense;
import com.pinodesk.sequel.repository.AbstractRepository;
import com.pinodesk.sequel.sql.Where;
import com.pinodesk.viewmodel.ExpenseFilterVM;
import com.pinodesk.viewmodel.ExpenseVM;

import org.apache.commons.lang3.StringUtils;

public class ExpenseRepositoryImpl extends AbstractRepository<Expense> implements ExpenseRepositoryCustom {

    @Override
    public List<ExpenseVM> findByFilter(ExpenseFilterVM filter) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select a.*,
                    b.name as expense_category_name,
                    c.name as payment_method_name,
                    d.full_name as user_full_name
                from expense a
                inner join expense_category b on b.id = a.expense_category_id
                inner join payment_method c on c.id = a.payment_method_id
                inner join `user` d on d.id = a.user_id
                """);
        Where where = new Where().isNull("a.deleted_at");
        if (StringUtils.isNotBlank(filter.getTitle())) {
            where.andContainsIgnoreCase("a.title", filter.getTitle().trim());
        }
        if (filter.getExpenseCategoryId() != null) {
            where.andEquals("a.expense_category_id", filter.getExpenseCategoryId());
        }
        if (filter.getNominalMin() != null) {
            where.andGreaterThanOrEqual("a.nominal", filter.getNominalMin());
        }
        if (filter.getNominalMax() != null) {
            where.andLowerThanOrEqual("a.nominal", filter.getNominalMax());
        }
        if (filter.getExpenseDateMin() != null) {
            where.andGreaterThanOrEqual("a.expense_date", filter.getExpenseDateMin());
        }
        if (filter.getExpenseDateMax() != null) {
            where.andLowerThanOrEqual("a.expense_date", filter.getExpenseDateMax());
        }
        if (filter.getPaymentMethodId() != null) {
            where.andEquals("a.payment_method_id", filter.getPaymentMethodId());
        }
        sb.append(where.getClause());
        return performSelect(sb.toString(), where.getValues(), ExpenseVM.class);
    }

}

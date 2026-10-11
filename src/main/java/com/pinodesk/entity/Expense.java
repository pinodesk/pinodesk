package com.pinodesk.entity;

import com.pinodesk.sequel.model.DataModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
public class Expense extends DataModel {
    public static final String C_TITLE = "title";
    public static final String C_EXPENSE_CATEGORY_ID = "expense_category_id";
    public static final String C_NOMINAL = "nominal";
    public static final String C_EXPENSE_DATE = "expense_date";
    public static final String C_PAYMENT_METHOD_ID = "payment_method_id";
    public static final String C_NOTES = "notes";
    public static final String C_USER_ID = "user_id";

    private String title;
    private Long expenseCategoryId;
    private BigDecimal nominal;
    private LocalDate expenseDate;
    private Long paymentMethodId;
    private String notes;
    private Long userId;
}

package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ExpenseAddVM {
    private String title;
    private Long expenseCategoryId;
    private BigDecimal nominal;
    private LocalDate expenseDate;
    private Long paymentMethodId;
    private String notes;
}

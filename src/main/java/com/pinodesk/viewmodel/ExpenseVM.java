package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ExpenseVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String title;
    private Long expenseCategoryId;
    private String expenseCategoryName;
    private BigDecimal nominal;
    private LocalDate expenseDate;
    private Long paymentMethodId;
    private String paymentMethodName;
    private String notes;
    private Long userId;
    private String userFullName;
}

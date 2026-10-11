package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ExpenseFilterVM {
    private String title;
    private Long expenseCategoryId;
    private String expenseCategoryName;
    private BigDecimal nominalMin;
    private BigDecimal nominalMax;
    private LocalDate expenseDateMin;
    private LocalDate expenseDateMax;
    private Long paymentMethodId;
    private String paymentMethodName;
}

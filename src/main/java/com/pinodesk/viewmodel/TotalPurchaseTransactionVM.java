package com.pinodesk.viewmodel;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TotalPurchaseTransactionVM {
    private Integer totalTransaction;
    private BigDecimal totalPayment;
}

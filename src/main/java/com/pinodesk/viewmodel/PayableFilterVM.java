package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pinodesk.constant.PaymentStatus;

import lombok.Data;

@Data
public class PayableFilterVM {
    private Long supplierId;
    private String supplierName;
    private String invoiceNumber;
    private LocalDate invoiceDateMin;
    private LocalDate invoiceDateMax;
    private BigDecimal amountMin;
    private BigDecimal amountMax;
    private LocalDate dueDateMin;
    private LocalDate dueDateMax;
    private LocalDate completionDateMin;
    private LocalDate completionDateMax;
    private String remarks;
    private PaymentStatus paymentStatus;
}

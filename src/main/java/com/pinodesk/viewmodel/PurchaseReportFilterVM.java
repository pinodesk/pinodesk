package com.pinodesk.viewmodel;

import java.time.LocalDate;

import com.pinodesk.constant.PaymentStatus;

import lombok.Data;

@Data
public class PurchaseReportFilterVM {
    private String invoiceNumber;
    private LocalDate invoiceDateMin;
    private LocalDate invoiceDateMax;
    private PaymentStatus paymentStatus;
    private String productName;
    private String supplierName;
}

package com.pinodesk.viewmodel;

import java.time.LocalDate;

import com.pinodesk.constant.PaymentStatus;
import com.pinodesk.constant.SellingMode;

import lombok.Data;

@Data
public class SaleReportFilterVM {
    private String invoiceNumber;
    private LocalDate invoiceDateMin;
    private LocalDate invoiceDateMax;
    private SellingMode sellingMode;
    private PaymentStatus paymentStatus;
    private String productName;
    private String customerName;
}

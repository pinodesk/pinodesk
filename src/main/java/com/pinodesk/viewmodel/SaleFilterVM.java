package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pinodesk.constant.PaymentStatus;

import lombok.Data;

@Data
public class SaleFilterVM {
    private String invoiceNumber;
    private LocalDate createdDateMin;
    private LocalDate createdDateMax;
    private LocalDate dueDateMin;
    private LocalDate dueDateMax;
    private Long customerId;
    private String customerName;
    private Long doctorId;
    private String doctorName;
    private BigDecimal totalPaymentMin;
    private BigDecimal totalPaymentMax;
    private Integer totalProductMin;
    private Integer totalProductMax;
    private PaymentStatus paymentStatus;
}

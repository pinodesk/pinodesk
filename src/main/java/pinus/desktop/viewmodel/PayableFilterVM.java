package pinus.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import pinus.desktop.constant.PaymentStatus;

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

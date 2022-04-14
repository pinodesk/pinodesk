package pinus.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import pinus.desktop.constant.PaymentStatus;

@Data
public class PurchaseFilterVM {
    private String invoiceNumber;
    private LocalDate invoiceDateMin;
    private LocalDate invoiceDateMax;
    private LocalDate dueDateMin;
    private LocalDate dueDateMax;
    private Long supplierId;
    private String supplierName;
    private BigDecimal totalPaymentMin;
    private BigDecimal totalPaymentMax;
    private Integer totalProductMin;
    private Integer totalProductMax;
    private PaymentStatus paymentStatus;
}

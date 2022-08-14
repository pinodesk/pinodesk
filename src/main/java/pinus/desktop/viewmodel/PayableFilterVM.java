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
    private BigDecimal paymentAmountMin;
    private BigDecimal paymentAmountMax;
    private LocalDate paymentDueDateMin;
    private LocalDate paymentDueDateMax;
    private LocalDate paymentDateMin;
    private LocalDate paymentDateMax;
    private String remarks;
    private PaymentStatus paymentStatus;
}

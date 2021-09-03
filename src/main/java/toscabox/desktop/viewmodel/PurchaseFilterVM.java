package toscabox.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import toscabox.desktop.constant.PaymentMethod;
import toscabox.desktop.constant.PaymentPeriodUnit;
import toscabox.desktop.constant.PaymentStatus;

@Data
public class PurchaseFilterVM {
    private String orderNumber;
    private LocalDate orderDateMin;
    private LocalDate orderDateMax;
    private PaymentMethod paymentMethod;
    private PaymentPeriodUnit paymentPeriodUnit;
    private LocalDate dueDateMin;
    private LocalDate dueDateMax;
    private Long supplierId;
    private BigDecimal totalPaymentMin;
    private BigDecimal totalPaymentMax;
    private Integer totalProductMin;
    private Integer totalProductMax;
    private PaymentStatus paymentStatus;
}

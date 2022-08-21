package stoready.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import stoready.desktop.constant.PaymentStatus;

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

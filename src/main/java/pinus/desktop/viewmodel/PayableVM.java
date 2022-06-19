package pinus.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PayableVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long supplierId;
    private String supplierName;
    private Long purchaseId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal paymentAmount;
    private LocalDate paymentDueDate;
    private LocalDate paymentDate;
    private String remarks;
}

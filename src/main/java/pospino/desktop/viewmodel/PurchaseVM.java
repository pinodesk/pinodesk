package pospino.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PurchaseVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long supplierId;
    private String supplierName;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private LocalDate paymentDueDate;
    private String paymentStatus;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal totalPurchase;
    private Long userId;
    private String userFullName;
}

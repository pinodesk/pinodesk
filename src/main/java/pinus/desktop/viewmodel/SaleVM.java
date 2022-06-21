package pinus.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SaleVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long customerId;
    private String customerName;
    private Long doctorId;
    private String doctorName;
    private String invoiceNumber;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private BigDecimal totalSale;
    private LocalDate paymentDueDate;
    private String paymentStatus;
    private String sellingMode;
}

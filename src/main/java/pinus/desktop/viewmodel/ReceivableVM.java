package pinus.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReceivableVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long customerId;
    private String customerName;
    private Long saleId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate completionDate;
    private String remarks;
}

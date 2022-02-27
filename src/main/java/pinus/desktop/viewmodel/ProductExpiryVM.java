package pinus.desktop.viewmodel;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProductExpiryVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long productId;
    private LocalDate expiredDate;
    private String batchNumber;
    private Long purchaseId;
    private String purchaseInvoiceNumber;
    private Long saleId;
    private String saleInvoiceNumber;
    private Integer quantityIn;
    private Integer quantityOut;
    private Integer finalQuantity;
    private Long userId;
    private String activity;
    private String remarks;
}

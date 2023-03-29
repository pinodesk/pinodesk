package pospino.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProductStockVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long productId;
    private Integer quantityIn;
    private Integer quantityOut;
    private Integer finalQuantity;
    private Long purchaseId;
    private String purchaseInvoiceNumber;
    private Long saleId;
    private String saleInvoiceNumber;
    private Long userId;
    private String userFullName;
    private String userUsername;
    private String activity;
    private String remarks;
}

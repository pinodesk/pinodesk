package stoready.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProductPriceVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long productId;
    private BigDecimal generalSellingPrice;
    private BigDecimal prescriptionSellingPrice;
    private Long purchaseId;
    private String purchaseInvoiceNumber;
    private Long saleId;
    private String saleInvoiceNumber;
    private Long userId;
    private String activity;
    private String remarks;
}

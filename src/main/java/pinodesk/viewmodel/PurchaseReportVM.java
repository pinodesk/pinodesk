package pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PurchaseReportVM {
    private Long purchaseId;
    private LocalDate invoiceDate;
    private String invoiceNumber;
    private String supplierName;
    private String productName;
    private Integer quantity;
    private String unit;
    private BigDecimal buyingPrice;
    private String discountType;
    private BigDecimal discountAmount;
    private BigDecimal buyingPriceDiscount;
    private BigDecimal subtotalPrice;
    private BigDecimal subtotalDiscount;
    private LocalDateTime createdAt;
    private String paymentStatus;
    private Integer totalProduct;
    private BigDecimal totalPayment;
}

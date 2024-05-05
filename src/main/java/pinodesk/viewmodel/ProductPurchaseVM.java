package pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ProductPurchaseVM {
    // Purchase
    private Long supplierId;
    private String supplierName;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate paymentDueDate;
    private String paymentStatus;

    // Purchase detail
    private Long purchaseId;
    private Integer quantity;
    private BigDecimal buyingPrice;
    private String discountType;
    private BigDecimal discountAmount;
    private BigDecimal buyingPriceDiscount;
    private BigDecimal subtotalDiscount;
    private BigDecimal subtotalPrice;
}

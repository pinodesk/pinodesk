package pospino.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class PurchaseProductVM {
    private Long productId;
    private String productName;
    private String productCategoryName;
    private String productCategoryCode;
    private String productUnitLabel;
    private Integer quantity;
    private BigDecimal buyingPrice;
    private String discountType;
    private BigDecimal discountAmount;
    private BigDecimal buyingPriceDiscount;
    private BigDecimal subtotalDiscount;
    private BigDecimal subtotalPrice;
    private BigDecimal generalSellingPrice;
    private BigDecimal prescriptionSellingPrice;
    private String batchNumber;
    private LocalDate expiredDate;
}

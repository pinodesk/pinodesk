package pospino.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class SaleProductVM {
    private Long productId;
    private String productName;
    private String productCode;
    private String productBarcode;
    private String productCategoryName;
    private String productCategoryCode;
    private String productUnitLabel;
    private Integer currentQuantity;
    private Integer saleQuantity;
    private BigDecimal subtotal;
    private BigDecimal sellingPrice;
    private BigDecimal generalSellingPrice;
    private BigDecimal prescriptionSellingPrice;
    private LocalDate expiredDate;
}

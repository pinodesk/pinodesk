package pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import pinodesk.constant.ProductStatus;

@Data
public class ProductFilterVM {
    private String name;
    private String description;
    private String code;
    private String barcode;
    private ProductCategoryVM category;
    private UnitVM unit;
    private ProductStatus status;
    private BigDecimal generalSellingPriceMin;
    private BigDecimal generalSellingPriceMax;
    private BigDecimal prescriptionSellingPriceMin;
    private BigDecimal prescriptionSellingPriceMax;
    private Integer stockQuantityMin;
    private Integer stockQuantityMax;
    private LocalDate expiredDateMin;
    private LocalDate expiredDateMax;
    private String batchNumber;
}

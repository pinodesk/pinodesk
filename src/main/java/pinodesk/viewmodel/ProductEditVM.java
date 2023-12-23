package pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import pinodesk.constant.ProductStatus;

@Data
public class ProductEditVM {

    @NotBlank
    @Size(max = 256)
    private String name;

    @NotBlank
    @Size(max = 64)
    private String code;

    @Size(max = 24)
    private String barcode;

    @Size(max = 512)
    private String description;

    @NotNull
    private ProductCategoryVM productCategory;

    @NotNull
    private UnitVM unit;

    @NotNull
    private ProductStatus status;

    private DrugClassificationVM drugClassification;

    @Size(max = 512)
    private String indication;

    @Size(max = 512)
    private String contraindication;

    @Min(0)
    private BigDecimal generalSellingPrice;

    @Min(0)
    private BigDecimal prescriptionSellingPrice;

    @Size(max = 128)
    private String priceRemarks;

    @Min(0)
    private Integer stockQuantity;

    @Size(max = 128)
    private String stockRemarks;

    private LocalDate expiredDate;

    @Size(max = 64)
    private String batchNumber;

    @Min(0)
    private Integer expiryQuantity;

    @Size(max = 128)
    private String expiryRemarks;
}

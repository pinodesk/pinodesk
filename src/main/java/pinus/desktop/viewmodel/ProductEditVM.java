package pinus.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;
import pinus.desktop.constant.ProductStatus;

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

    private DrugCategoryVM drugCategory;

    @Size(max = 512)
    private String indication;

    @Size(max = 512)
    private String contraindication;

    @Positive
    private BigDecimal generalSellingPrice;

    @Positive
    private BigDecimal prescriptionSellingPrice;

    @Size(max = 128)
    private String priceRemarks;

    @Positive
    private Integer stockQuantity;

    @Size(max = 128)
    private String stockRemarks;

    private LocalDate expiredDate;

    @Size(max = 64)
    private String batchNumber;

    @Positive
    private Integer expiryQuantity;

    @Size(max = 128)
    private String expiryRemarks;
}

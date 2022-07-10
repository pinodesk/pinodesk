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
public class ProductImportVM {

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

    @NotBlank
    private String productCategoryCode;

    @NotNull
    private Long unitId;

    @NotNull
    private ProductStatus status;

    private String drugClassificationCode;

    @Size(max = 512)
    private String indication;

    @Size(max = 512)
    private String contraindication;

    @Positive
    private BigDecimal generalSellingPrice;

    @Positive
    private BigDecimal prescriptionSellingPrice;

    @Positive
    private Integer quantity;

    private LocalDate expiredDate;

}

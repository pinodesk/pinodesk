package pospino.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PackageProductVM {
    private Long id;
    private String code;
    private String barcode;
    private String name;
    private String description;
    private Long unitId;
    private String unitLabel;
    private Long categoryId;
    private String categoryName;
    private String categoryCode;
    private Integer quantity;
    private BigDecimal generalSellingPrice;
    private BigDecimal prescriptionSellingPrice;
    private BigDecimal averageBuyingPrice;
    private LocalDate closestExpiredDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer quantityInPackage;
}

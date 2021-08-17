package tosca.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProductVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String code;
    private String barcode;
    private String name;
    private String description;
    private Integer quantity;
    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private String vatIncluded;
    private Long unitId;
    private String unitLabel;
    private Long rackId;
    private String rackCode;
    private String rackName;
    private LocalDate expiredDate;
}

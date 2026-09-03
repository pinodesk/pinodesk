package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConsignmentProductVM {
    private Long productId;
    private String productName;
    private LocalDateTime productDeletedAt;
    private String productCategoryName;
    private String productCategoryCode;
    private String productUnitLabel;
    private Integer quantity;
    private BigDecimal supplierPrice;
    private BigDecimal subtotalPrice;
    private BigDecimal generalSellingPrice;
    private BigDecimal prescriptionSellingPrice;
    private String batchNumber;
    private LocalDate expiredDate;
}

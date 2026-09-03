package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SaleProductVM {
    private Long productId;
    private String productName;
    private LocalDateTime productDeletedAt;
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

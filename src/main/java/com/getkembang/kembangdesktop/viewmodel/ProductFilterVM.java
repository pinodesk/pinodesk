package com.getkembang.kembangdesktop.viewmodel;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ProductFilterVM {
    private String name;
    private String code;
    private String barcode;
    private Long categoryId;
    private String categoryCode;
    private BigDecimal purchasePriceMin;
    private BigDecimal purchasePriceMax;
    private BigDecimal sellingPriceMin;
    private BigDecimal sellingPriceMax;
    private Integer quantityMin;
    private Integer quantityMax;
    private Long rackId;
    private Long unitId;
    private Date expiredDateMin;
    private Date expiredDateMax;
    private String includesVat;
}

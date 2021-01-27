package com.getkembang.kembangdesktop.viewmodel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ProductAddVM {
    private String code;
    private String barcode;
    private String name;
    private String description;
    private Integer quantity;
    private ProductCategoryVM productCategory;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private String vatIncluded;
    private UnitVM unit;
    private RackVM rack;
    private Date expiredDate;
    private DrugVM drug;
    private List<WholesaleVM> wholesales;
}

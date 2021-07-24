package com.getkembang.kembangdesktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ProductEditVM {
    private Long id;
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
    private LocalDate expiredDate;
    private DrugVM drug;
    private List<WholesaleVM> wholesales;
}

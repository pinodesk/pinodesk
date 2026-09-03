package com.pinodesk.entity;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PackageDetail extends DataModel {

    public static final String C_PACKAGE_PRODUCT_ID = "package_product_id";
    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_QUANTITY = "quantity";

    // Package content
    private Long packageProductId;
    // Product parent
    private Long productId;
    private Integer quantity;
}

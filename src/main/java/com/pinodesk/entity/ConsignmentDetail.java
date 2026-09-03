package com.pinodesk.entity;

import java.math.BigDecimal;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConsignmentDetail extends DataModel {

    public static final String C_CONSIGNMENT_ID = "consignment_id";
    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_QUANTITY = "quantity";
    public static final String C_PRICE = "price";
    public static final String C_SUBTOTAL = "subtotal";

    private Long consignmentId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}

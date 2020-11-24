package com.gitlab.muhammadkholidb.bianglala.data.model;

import java.math.BigDecimal;

import com.gitlab.muhammadkholidb.jdbctemplatehelper.annotation.DataColumn;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductPrice extends DataModel {

    public static final String TABLE_NAME = "t_product_price";

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_PURCHASE_QUANTITY = "purchase_quantity";
    public static final String C_UNIT_ID = "unit_id";
    public static final String C_UNIT_LABEL = "unit_label";
    public static final String C_SELLING_PRICE = "selling_price";
    public static final String C_SELLING_PRICE_BEFORE_TAX = "selling_price_before_tax";
    public static final String C_VAT_INCLUDED = "vat_included";

    @DataColumn(C_PRODUCT_ID)
    private Long productId;

    @DataColumn(C_PURCHASE_QUANTITY)
    private Integer purchaseQuantity;

    @DataColumn(C_UNIT_ID)
    private Long unitId;

    @DataColumn(C_UNIT_LABEL)
    private String unitLabel;

    @DataColumn(C_SELLING_PRICE)
    private BigDecimal sellingPrice;

    @DataColumn(C_SELLING_PRICE_BEFORE_TAX)
    private BigDecimal sellingPriceBeforeTax;

    @DataColumn(C_VAT_INCLUDED)
    private String vatIncluded;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

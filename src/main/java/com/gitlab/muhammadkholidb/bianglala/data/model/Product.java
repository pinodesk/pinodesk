package com.gitlab.muhammadkholidb.bianglala.data.model;

import java.math.BigDecimal;

import com.gitlab.muhammadkholidb.jdbctemplatehelper.annotation.DataColumn;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.model.DataModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author muhammad
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Product extends DataModel {

    public static final String TABLE_NAME = "t_product";

    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";
    public static final String C_QUANTITY = "quantity";
    public static final String C_UNIT_ID = "unit_id";
    public static final String C_UNIT_LABEL = "unit_label";
    public static final String C_CATEGORY_CODE = "category_code";
    public static final String C_PURCHASE_PRICE = "purchase_price";
    public static final String C_SELLING_PRICE = "selling_price";

    @DataColumn(C_CODE)
    private String code;

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_DESCRIPTION)
    private String description;

    @DataColumn(C_QUANTITY)
    private Integer quantity;

    @DataColumn(C_UNIT_ID)
    private Long unitId;

    @DataColumn(C_UNIT_LABEL)
    private String unitLabel;

    @DataColumn(C_CATEGORY_CODE)
    private String categoryCode;

    @DataColumn(C_PURCHASE_PRICE)
    private BigDecimal purchasePrice;

    @DataColumn(C_SELLING_PRICE)
    private BigDecimal sellingPrice;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

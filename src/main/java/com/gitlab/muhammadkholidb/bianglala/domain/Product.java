package com.gitlab.muhammadkholidb.bianglala.domain;

import java.math.BigDecimal;
import java.sql.Date;

import com.gitlab.muhammadkholidb.jdbctemplatehelper.annotation.DataColumn;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author muhammad
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Product extends DataModel {

    public static final String TABLE_NAME = "t_product";

    public static final String C_CODE = "code";
    public static final String C_BARCODE = "barcode";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";
    public static final String C_QUANTITY = "quantity";
    public static final String C_UNIT_ID = "unit_id";
    public static final String C_UNIT_LABEL = "unit_label";
    public static final String C_CATEGORY_CODE = "category_code";
    public static final String C_PURCHASE_PRICE = "purchase_price";
    public static final String C_SELLING_PRICE = "selling_price";
    public static final String C_SELLING_PRICE_BEFORE_TAX = "selling_price_before_tax";
    public static final String C_VAT_PERCENTAGE = "vat_percentage";
    public static final String C_RACK_ID = "rack_id";
    public static final String C_RACK_CODE = "rack_code";
    public static final String C_RACK_NAME = "rack_name";
    public static final String C_EXPIRED_DATE = "expired_date";

    @DataColumn(C_CODE)
    private String code;

    @DataColumn(C_BARCODE)
    private String barcode;

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_DESCRIPTION)
    private String description;

    @DataColumn(C_QUANTITY)
    private Integer quantity;

    @DataColumn(C_CATEGORY_CODE)
    private String categoryCode;

    @DataColumn(C_UNIT_ID)
    private Long unitId;

    @DataColumn(C_UNIT_LABEL)
    private String unitLabel;

    @DataColumn(C_PURCHASE_PRICE)
    private BigDecimal purchasePrice;

    @DataColumn(C_RACK_ID)
    private Long rackId;

    @DataColumn(C_RACK_CODE)
    private String rackCode;

    @DataColumn(C_RACK_NAME)
    private String rackName;

    @DataColumn(C_EXPIRED_DATE)
    private Date expiredDate;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

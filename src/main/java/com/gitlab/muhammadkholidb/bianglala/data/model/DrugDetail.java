package com.gitlab.muhammadkholidb.bianglala.data.model;

import java.sql.Date;

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
public class DrugDetail extends DataModel {

    public static final String TABLE_NAME = "t_drug_detail";

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_CATEGORY_ID = "category_id";
    public static final String C_CATEGORY_CODE = "category_code";
    public static final String C_CATEGORY_NAME = "category_name";
    public static final String C_RACK_ID = "rack_id";
    public static final String C_RACK_CODE = "rack_code";
    public static final String C_RACK_NAME = "rack_name";
    public static final String C_EXPIRED_DATE = "expired_date";
    public static final String C_INDICATION = "indication";
    public static final String C_CONTRAINDICATION = "contraindication";
    public static final String C_PRESCRIPTION_PRICE = "prescription_price";

    @DataColumn(C_PRODUCT_ID)
    public Long productId;

    @DataColumn(C_CATEGORY_ID)
    public Long categoryId;

    @DataColumn(C_CATEGORY_CODE)
    public String categoryCode;

    @DataColumn(C_CATEGORY_NAME)
    private String categoryName;

    @DataColumn(C_RACK_ID)
    public Long rackId;

    @DataColumn(C_RACK_CODE)
    public String rackCode;

    @DataColumn(C_RACK_NAME)
    private String rackName;

    @DataColumn(C_EXPIRED_DATE)
    private Date expiredDate;

    @DataColumn(C_INDICATION)
    private String indication;

    @DataColumn(C_CONTRAINDICATION)
    private String contraindication;

    @DataColumn(C_PRESCRIPTION_PRICE)
    private String prescriptionPrice;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

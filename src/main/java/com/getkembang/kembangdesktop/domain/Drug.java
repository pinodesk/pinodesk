package com.getkembang.kembangdesktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Drug extends DataModel {

    public static final String TABLE_NAME = "t_drug";

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_DRUG_CATEGORY_ID = "drug_category_id";
    public static final String C_DRUG_CATEGORY_CODE = "drug_category_code";
    public static final String C_DRUG_CATEGORY_NAME = "drug_category_name";
    public static final String C_INDICATION = "indication";
    public static final String C_CONTRAINDICATION = "contraindication";
    public static final String C_PRESCRIPTION_PRICE = "prescription_price";

    @DataColumn(C_PRODUCT_ID)
    public Long productId;

    @DataColumn(C_DRUG_CATEGORY_ID)
    public Long drugCategoryId;

    @DataColumn(C_DRUG_CATEGORY_CODE)
    public String drugCategoryCode;

    @DataColumn(C_DRUG_CATEGORY_NAME)
    private String drugCategoryName;

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

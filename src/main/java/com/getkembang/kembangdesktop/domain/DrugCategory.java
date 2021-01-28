package com.getkembang.kembangdesktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DrugCategory extends DataModel {

    public static final String TABLE_NAME = "t_drug_category";

    public static final String C_CATEGORY_BASE_ID = "drug_category_base_id";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";

    @DataColumn(C_CATEGORY_BASE_ID)
    public Long drugCategoryBaseId;

    @DataColumn(C_CODE)
    public String code;

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_DESCRIPTION)
    private String description;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

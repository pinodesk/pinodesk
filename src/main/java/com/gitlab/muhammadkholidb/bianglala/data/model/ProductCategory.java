package com.gitlab.muhammadkholidb.bianglala.data.model;

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
public class ProductCategory extends DataModel {

    public static final String TABLE_NAME = "t_product_category";

    public static final String C_PARENT_CATEGORY_ID = "parent_category_id";
    public static final String C_LANGUAGE_ID = "language_id";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";

    @DataColumn(C_PARENT_CATEGORY_ID)
    public Long parentCategoryId;

    @DataColumn(C_LANGUAGE_ID)
    public Long languageId;

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

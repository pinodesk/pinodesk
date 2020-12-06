package com.gitlab.muhammadkholidb.bianglala.domain;

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
public class Configuration extends DataModel {

    public static final String TABLE_NAME = "t_configuration";

    public static final String C_CODE = "code";
    public static final String C_VALUE = "value";

    @DataColumn(C_CODE)
    public String code;

    @DataColumn(C_VALUE)
    private String value;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

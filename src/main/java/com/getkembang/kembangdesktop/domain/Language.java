package com.getkembang.kembangdesktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Language extends DataModel {

    public static final String TABLE_NAME = "t_language";

    public static final String C_CODE = "code";
    public static final String C_NAME = "name";

    @DataColumn(C_CODE)
    public String code;

    @DataColumn(C_NAME)
    private String name;
    
    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

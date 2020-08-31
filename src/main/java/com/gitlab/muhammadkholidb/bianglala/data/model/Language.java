package com.gitlab.muhammadkholidb.bianglala.data.model;

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

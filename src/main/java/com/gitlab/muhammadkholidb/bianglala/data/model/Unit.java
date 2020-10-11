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
public class Unit extends DataModel {

    public static final String TABLE_NAME = "t_unit";

    public static final String C_NAME = "name";
    public static final String C_LABEL = "label";

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_LABEL)
    private String label;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

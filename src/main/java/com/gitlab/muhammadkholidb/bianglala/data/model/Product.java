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

    public static final String C_NAME = "name";
    public static final String C_CODE = "code";
    public static final String C_PRICE = "price";
    public static final String C_QUANTITY = "quantity";

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_CODE)
    private String code;

    @DataColumn(C_PRICE)
    private BigDecimal price;

    @DataColumn(C_QUANTITY)
    private Integer quantity;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

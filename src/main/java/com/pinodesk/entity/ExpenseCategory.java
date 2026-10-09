package com.pinodesk.entity;

import com.pinodesk.sequel.model.DataModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExpenseCategory extends DataModel {
    public static final String C_NAME = "name";
    public static final String C_STATUS = "status";

    private String name;
    private String status;
}

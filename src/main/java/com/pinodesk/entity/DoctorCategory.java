package com.pinodesk.entity;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DoctorCategory extends DataModel {

    public static final String C_LANGUAGE = "language";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";

    private String language;
    private String code;
    private String name;
}

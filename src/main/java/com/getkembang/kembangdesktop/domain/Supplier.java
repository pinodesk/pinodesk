package com.getkembang.kembangdesktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Supplier extends DataModel {

    public static final String TABLE_NAME = "t_supplier";

    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_PHONE = "phone";
    public static final String C_FAX = "fax";
    public static final String C_EMAIL = "email";
    public static final String C_WEBSITE = "website";
    public static final String C_ADDRESS = "address";

    @DataColumn(C_CODE)
    private String code;

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_PHONE)
    private String phone;

    @DataColumn(C_FAX)
    private String fax;

    @DataColumn(C_EMAIL)
    private String email;

    @DataColumn(C_WEBSITE)
    private String website;

    @DataColumn(C_ADDRESS)
    private String address;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

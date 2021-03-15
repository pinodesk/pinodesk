package com.getkembang.kembangdesktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Customer extends DataModel {

    public static final String TABLE_NAME = "t_customer";

    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_PHONE = "phone";
    public static final String C_EMAIL = "email";
    public static final String C_ADDRESS = "address";

    @DataColumn(C_CODE)
    public String code;

    @DataColumn(C_NAME)
    public String name;

    @DataColumn(C_PHONE)
    public String phone;

    @DataColumn(C_EMAIL)
    private String email;

    @DataColumn(C_ADDRESS)
    private String address;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

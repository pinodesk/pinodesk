package com.getkembang.kembangdesktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Contact extends DataModel {

    public static final String TABLE_NAME = "t_contact";

    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_PHONE = "phone";
    public static final String C_EMAIL = "email";
    public static final String C_ADDRESS = "address";
    public static final String C_COMPANY_NAME = "company_name";
    public static final String C_CONTACT_TYPE = "contact_type";

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

    @DataColumn(C_COMPANY_NAME)
    private String companyName;

    @DataColumn(C_CONTACT_TYPE)
    private String contactType;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Supplier extends DataModel {

    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_PHONE = "phone";
    public static final String C_EMAIL = "email";
    public static final String C_WEBSITE = "website";
    public static final String C_ADDRESS = "address";

    private String code;
    private String name;
    private String phone;
    private String email;
    private String website;
    private String address;
}

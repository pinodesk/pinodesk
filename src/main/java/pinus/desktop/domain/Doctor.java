package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Doctor extends DataModel {

    public static final String C_CATEGORY_CODE = "category_code";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_REGISTRATION_NUMBER = "registration_number";
    public static final String C_MEDICAL_LICENSE_NUMBER = "medical_license_number";

    private String categoryCode;
    private String code;
    private String name;
    private String registrationNumber;
    private String medicalLicenseNumber;
}

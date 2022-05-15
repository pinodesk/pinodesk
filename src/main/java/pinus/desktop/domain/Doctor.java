package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Doctor extends DataModel {

    public static final String TABLE_NAME = "doctor";

    public static final String C_CATEGORY_CODE = "category_code";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_REGISTRATION_NUMBER = "registration_number";
    public static final String C_MEDICAL_LICENSE_NUMBER = "medical_license_number";

    @DataColumn(C_CATEGORY_CODE)
    private String categoryCode;

    @DataColumn(C_CODE)
    private String code;

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_REGISTRATION_NUMBER)
    private String registrationNumber;

    @DataColumn(C_MEDICAL_LICENSE_NUMBER)
    private String medicalLicenseNumber;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

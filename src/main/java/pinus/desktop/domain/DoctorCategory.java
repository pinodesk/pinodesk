package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DoctorCategory extends DataModel {

    public static final String TABLE_NAME = "doctor_category";

    public static final String C_LANGUAGE_CODE = "language_code";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";

    @DataColumn(C_LANGUAGE_CODE)
    private String languageCode;

    @DataColumn(C_CODE)
    private String code;

    @DataColumn(C_NAME)
    private String name;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

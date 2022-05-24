package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DoctorCategory extends DataModel {

    public static final String C_LANGUAGE_CODE = "language_code";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";

    private String languageCode;
    private String code;
    private String name;
}

package pinodesk.domain;

import com.gitlab.mudiasoft.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DrugClassification extends DataModel {

    public static final String C_LANGUAGE = "language";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";

    private String language;
    private String code;
    private String name;
    private String description;
}

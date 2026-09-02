package pinodesk.entity;

import com.pinodesk.sequel.model.DataModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Unit extends DataModel {

    public static final String C_NAME = "name";
    public static final String C_LABEL = "label";
    public static final String C_CODE = "code";
    public static final String C_LANGUAGE = "language";

    private String name;
    private String label;
    private String code;
    private String language;
}

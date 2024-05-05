package pinodesk.entity;

import com.mudiatech.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Configuration extends DataModel {

    public static final String C_CODE = "code";
    public static final String C_VALUE = "value";

    private String code;
    private String value;
}

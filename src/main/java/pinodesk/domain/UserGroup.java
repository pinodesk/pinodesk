package pinodesk.domain;

import com.gitlab.mudiasoft.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserGroup extends DataModel {

    public static final String C_DESCRIPTION = "description";
    public static final String C_NAME = "name";
    public static final String C_STATUS = "status";

    private String description;
    private String name;
    private String status;
}

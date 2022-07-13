package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class User extends DataModel {

    public static final String C_FULL_NAME = "full_name";
    public static final String C_USERNAME = "username";
    public static final String C_USER_GROUP_ID = "user_group_id";
    public static final String C_STATUS = "status";

    private String fullName;
    private String username;
    private Long userGroupId;
    private String status;
}

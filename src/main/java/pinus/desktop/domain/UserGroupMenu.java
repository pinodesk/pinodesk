package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserGroupMenu extends DataModel {

    public static final String C_WRITE = "write";
    public static final String C_READ = "read";
    public static final String C_MENU_CODE = "menu_code";
    public static final String C_USER_GROUP_ID = "user_group_id";

    private String write;
    private String read;
    private String menuCode;
    private Long userGroupId;
}

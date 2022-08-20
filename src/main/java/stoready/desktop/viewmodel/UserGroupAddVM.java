package stoready.desktop.viewmodel;

import java.util.List;

import lombok.Data;
import stoready.desktop.constant.UserGroupStatus;

@Data
public class UserGroupAddVM {
    private String name;
    private String description;
    private UserGroupStatus status;
    private List<UserGroupMenuVM> userGroupMenus;
}

package pinodesk.viewmodel;

import java.util.List;

import lombok.Data;
import pinodesk.constant.UserGroupStatus;

@Data
public class UserGroupEditVM {
    private String name;
    private String description;
    private UserGroupStatus status;
    private List<UserGroupMenuVM> userGroupMenus;
}

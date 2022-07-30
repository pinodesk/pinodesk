package pinus.desktop.viewmodel;

import java.util.List;

import lombok.Data;
import pinus.desktop.constant.UserGroupStatus;

@Data
public class UserGroupEditVM {
    private String name;
    private String description;
    private UserGroupStatus status;
    private List<UserGroupMenuVM> userGroupMenus;
}

package pinus.desktop.viewmodel;

import lombok.Data;
import pinus.desktop.constant.UserGroupStatus;

@Data
public class UserGroupFilterVM {
    private String name;
    private String description;
    private UserGroupStatus status;
}

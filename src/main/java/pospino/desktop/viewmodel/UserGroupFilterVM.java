package pospino.desktop.viewmodel;

import lombok.Data;
import pospino.desktop.constant.UserGroupStatus;

@Data
public class UserGroupFilterVM {
    private String name;
    private String description;
    private UserGroupStatus status;
}

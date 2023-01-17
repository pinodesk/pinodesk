package pospino.desktop.viewmodel;

import lombok.Data;
import pospino.desktop.constant.UserStatus;

@Data
public class UserFilterVM {
    private String fullName;
    private String username;
    private UserGroupVM userGroup;
    private UserStatus status;
}

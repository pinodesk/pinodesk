package pinodesk.viewmodel;

import lombok.Data;
import pinodesk.constant.UserStatus;

@Data
public class UserFilterVM {
    private String fullName;
    private String username;
    private UserGroupVM userGroup;
    private UserStatus status;
}

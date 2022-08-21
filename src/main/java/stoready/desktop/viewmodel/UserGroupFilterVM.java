package stoready.desktop.viewmodel;

import lombok.Data;
import stoready.desktop.constant.UserGroupStatus;

@Data
public class UserGroupFilterVM {
    private String name;
    private String description;
    private UserGroupStatus status;
}

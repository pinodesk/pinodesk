package pinodesk.viewmodel;

import lombok.Data;
import pinodesk.constant.UserGroupStatus;

@Data
public class UserGroupFilterVM {
    private String name;
    private String description;
    private UserGroupStatus status;
}

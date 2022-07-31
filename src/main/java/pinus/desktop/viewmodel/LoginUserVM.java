package pinus.desktop.viewmodel;

import java.util.List;

import lombok.Data;

@Data
public class LoginUserVM {
    private Long id;
    private String fullName;
    private String username;
    private String status;
    private Long userGroupId;
    private String userGroupName;
    private String userGroupStatus;
    private List<LoginMenu> menus;
    private List<String> menuCodes;

    @Data
    public static class LoginMenu {
        private Long id;
        private String name;
        private String code;
        private String read;
        private String write;
    }
}

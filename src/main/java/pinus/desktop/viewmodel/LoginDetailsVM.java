package pinus.desktop.viewmodel;

import java.util.List;

import lombok.Data;

@Data
public class LoginDetailsVM {
    private LoginVM login;
    private UserVM user;
    private UserGroupVM userGroup;
    private List<UserGroupMenuVM> userGroupMenus;
}

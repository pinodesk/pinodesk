package pospino.desktop.viewmodel;

import java.util.List;

import lombok.Data;

@Data
public class CurrentSessionVM {
    private SessionVM session;
    private UserVM user;
    private UserGroupVM userGroup;
    private List<UserGroupMenuVM> userGroupMenus;
}

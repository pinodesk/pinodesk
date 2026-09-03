package com.pinodesk.viewmodel;

import java.util.List;

import com.pinodesk.constant.UserGroupStatus;

import lombok.Data;

@Data
public class UserGroupEditVM {
    private String name;
    private String description;
    private UserGroupStatus status;
    private List<UserGroupMenuVM> userGroupMenus;
}

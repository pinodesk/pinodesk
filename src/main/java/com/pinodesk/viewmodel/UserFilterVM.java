package com.pinodesk.viewmodel;

import com.pinodesk.constant.UserStatus;

import lombok.Data;

@Data
public class UserFilterVM {
    private String fullName;
    private String username;
    private UserGroupVM userGroup;
    private UserStatus status;
}

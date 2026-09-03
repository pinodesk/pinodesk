package com.pinodesk.viewmodel;

import com.pinodesk.constant.UserGroupStatus;

import lombok.Data;

@Data
public class UserGroupFilterVM {
    private String name;
    private String description;
    private UserGroupStatus status;
}

package com.pinodesk.entity;

import java.time.LocalDateTime;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Session extends DataModel {

    public static final String C_USER_ID = "user_id";
    public static final String C_LOGIN_AT = "login_at";
    public static final String C_LOGOUT_AT = "logout_at";
    public static final String C_LAST_ACTIVITY = "last_activity";
    public static final String C_LAST_ACTIVITY_AT = "last_activity_at";

    private LocalDateTime loginAt;
    private LocalDateTime logoutAt;
    private Long userId;
    private String lastActivity;
    private LocalDateTime lastActivityAt;
}

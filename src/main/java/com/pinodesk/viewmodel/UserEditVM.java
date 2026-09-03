package com.pinodesk.viewmodel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.pinodesk.constant.UserStatus;

import lombok.Data;

@Data
public class UserEditVM {

    @NotBlank
    @Size(max = 128)
    private String fullName;

    @NotBlank
    @Size(max = 64)
    private String username;

    @NotNull
    private Long userGroupId;

    @Size(min = 6)
    private String password;

    @NotNull
    private UserStatus status;

}

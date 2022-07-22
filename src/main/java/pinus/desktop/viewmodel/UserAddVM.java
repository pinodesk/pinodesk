package pinus.desktop.viewmodel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import pinus.desktop.constant.UserStatus;

@Data
public class UserAddVM {

    @NotBlank
    @Size(max = 128)
    private String fullName;

    @NotBlank
    @Size(max = 64)
    private String username;

    @NotNull
    private Long userGroupId;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotNull
    private UserStatus status;

}

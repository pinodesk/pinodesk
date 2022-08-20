package stoready.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String fullName;
    private String username;
    private Long userGroupId;
    private String userGroupName;
    private String status;
}

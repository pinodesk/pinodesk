package pinus.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoginVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime loginAt;
    private LocalDateTime logoutAt;
    private Long userId;
    private String lastActivity;
    private LocalDateTime lastActivityAt;
}

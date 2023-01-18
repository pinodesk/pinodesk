package pospino.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserGroupVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String name;
    private String description;
    private String status;
}

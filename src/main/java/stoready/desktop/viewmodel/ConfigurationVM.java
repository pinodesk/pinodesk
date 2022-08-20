package stoready.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConfigurationVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String code;
    private String value;
}

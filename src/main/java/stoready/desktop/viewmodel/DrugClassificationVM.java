package stoready.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DrugClassificationVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String language;
    private String code;
    private String name;
    private String description;
}

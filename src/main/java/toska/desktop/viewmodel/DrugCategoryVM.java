package toska.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DrugCategoryVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long drugCategoryBaseId;
    private String code;
    private String name;
    private String description;
}

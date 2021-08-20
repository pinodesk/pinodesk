package toscabox.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProductCategoryVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long parentCategoryId;
    private String languageCode;
    private String code;
    private String name;
    private String description;
}

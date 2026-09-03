package com.pinodesk.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DrugVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long productId;
    private String classificationCode;
    private String indication;
    private String contraindication;
}

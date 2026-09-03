package com.pinodesk.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DoctorCategoryVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String language;
    private String code;
    private String name;
}

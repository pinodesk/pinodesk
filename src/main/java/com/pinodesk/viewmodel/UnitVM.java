package com.pinodesk.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UnitVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String label;
    private String name;
    private String code;
}

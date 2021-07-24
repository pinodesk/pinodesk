package com.getkembang.kembangdesktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CustomerVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String address;
}

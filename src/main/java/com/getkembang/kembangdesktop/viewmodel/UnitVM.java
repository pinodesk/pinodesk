package com.getkembang.kembangdesktop.viewmodel;

import java.util.Date;

import lombok.Data;

@Data
public class UnitVM {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private String label;
    private String name;
}

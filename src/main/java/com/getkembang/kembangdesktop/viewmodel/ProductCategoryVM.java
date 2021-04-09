package com.getkembang.kembangdesktop.viewmodel;

import java.util.Date;

import lombok.Data;

@Data
public class ProductCategoryVM {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Long parentCategoryId;
    private Long languageId;
    private String code;
    private String name;
    private String description;
}

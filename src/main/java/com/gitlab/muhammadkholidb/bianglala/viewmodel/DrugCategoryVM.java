package com.gitlab.muhammadkholidb.bianglala.viewmodel;

import java.util.Date;

import lombok.Data;

@Data
public class DrugCategoryVM {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private String code;
    private String name;
    private String description;
}

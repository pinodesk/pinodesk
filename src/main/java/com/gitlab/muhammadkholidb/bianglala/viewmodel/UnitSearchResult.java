package com.gitlab.muhammadkholidb.bianglala.viewmodel;

import java.util.Date;

import lombok.Data;

@Data
public class UnitSearchResult {

    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private String label;
    private String name;
}

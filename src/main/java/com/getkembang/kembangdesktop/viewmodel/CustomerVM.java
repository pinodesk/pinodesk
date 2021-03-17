package com.getkembang.kembangdesktop.viewmodel;

import java.util.Date;

import lombok.Data;

@Data
public class CustomerVM {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String address;
}

package com.getkembang.kembangdesktop.viewmodel;

import java.util.Date;

import lombok.Data;

@Data
public class ContactVM {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    public String code;
    public String name;
    public String phone;
    private String email;
    private String address;
    private String companyName;
    private String contactType;
}

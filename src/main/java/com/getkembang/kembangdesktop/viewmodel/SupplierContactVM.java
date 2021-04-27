package com.getkembang.kembangdesktop.viewmodel;

import java.util.Date;

import lombok.Data;

@Data
public class SupplierContactVM {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Long supplierId;
    private String name;
    private String phone;
    private String email;
}

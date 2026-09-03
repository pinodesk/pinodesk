package com.pinodesk.viewmodel;

import lombok.Data;

@Data
public class CustomerFilterVM {
    private String name;
    private String code;
    private String phone;
    private String email;
    private String address;
}

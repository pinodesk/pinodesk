package com.pinodesk.viewmodel;

import com.pinodesk.constant.UserStatus;

import lombok.Data;

@Data
public class PaymentMethodFilterVM {
    private String name;
    private String category;
    private UserStatus status;
}

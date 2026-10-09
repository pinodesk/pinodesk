package com.pinodesk.viewmodel;

import com.pinodesk.constant.UserStatus;
import lombok.Data;

@Data
public class ExpenseCategoryFilterVM {
    private String name;
    private UserStatus status;
}

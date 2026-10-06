package com.pinodesk.entity;

import com.pinodesk.sequel.model.DataModel;
import com.pinodesk.constant.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PaymentMethod extends DataModel {
    private String name;
    private String category;
    private boolean defaultMethod;
    private String status = UserStatus.ACTIVE.toString();
}

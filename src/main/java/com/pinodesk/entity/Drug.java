package com.pinodesk.entity;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Drug extends DataModel {

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_CLASSIFICATION_CODE = "classification_code";
    public static final String C_INDICATION = "indication";
    public static final String C_CONTRAINDICATION = "contraindication";

    private Long productId;
    private String classificationCode;
    private String indication;
    private String contraindication;
}

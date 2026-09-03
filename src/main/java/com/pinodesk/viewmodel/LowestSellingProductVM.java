package com.pinodesk.viewmodel;

import lombok.Data;

@Data
public class LowestSellingProductVM {
    private Integer soldQuantity;
    private String categoryName;
    private String productName;
    private String unitLabel;
}

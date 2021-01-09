package com.gitlab.muhammadkholidb.bianglala.viewmodel;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class DrugVM {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private Long productId;
    private Long drugCategoryId;
    private String drugCategoryCode;
    private String drugCategoryName;
    private String indication;
    private String contraindication;
    private BigDecimal prescriptionPrice;
}

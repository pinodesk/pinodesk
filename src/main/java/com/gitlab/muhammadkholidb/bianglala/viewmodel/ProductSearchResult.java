package com.gitlab.muhammadkholidb.bianglala.viewmodel;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ProductSearchResult {
    
    private Long id;

    private Date createdAt;

    private Date updatedAt;
    
    private Date deletedAt;

    private String code;

    private String name;

    private String description;

    private Integer quantity;

    private Long categoryId;

    private String categoryCode;

    private String categoryName;

    private BigDecimal purchasePrice;

    private Long rackId;

    private String rackCode;

    private String rackName;

    private Date expiredDate;

}
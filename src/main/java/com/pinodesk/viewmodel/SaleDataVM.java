package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.pinodesk.constant.SellingMode;

import lombok.Data;

@Data
public class SaleDataVM {
    private List<SaleProductVM> saleProducts;
    private Optional<CustomerVM> customer;
    private SellingMode sellingMode;
    private Integer totalProduct;
    private BigDecimal totalSale;
}

package com.pinodesk.viewmodel;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ConsignmentAddVM {
    private Long supplierId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Integer totalProduct;
    private List<ConsignmentProductVM> consignmentProducts;
}

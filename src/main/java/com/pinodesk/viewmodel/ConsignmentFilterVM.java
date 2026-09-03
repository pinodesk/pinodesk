package com.pinodesk.viewmodel;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ConsignmentFilterVM {
    private String invoiceNumber;
    private LocalDate invoiceDateMin;
    private LocalDate invoiceDateMax;
    private Long supplierId;
}

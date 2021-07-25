package com.getkembang.kembangdesktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PurchaseVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long supplierId;
    private String orderNumber;
    private LocalDate orderDate;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private String paymentMethod;
    private Integer paymentPeriodCount;
    private String paymentPeriodUnit;
}

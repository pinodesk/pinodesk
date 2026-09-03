package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.pinodesk.constant.PaymentStatus;
import com.pinodesk.constant.SellingMode;

import lombok.Data;

@Data
public class SaleEditVM {
    private Long customerId;
    private Long doctorId;
    private SellingMode sellingMode;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDueDate;
    private Integer totalProduct;
    private BigDecimal totalSale;
    private BigDecimal totalPayment;
    private List<SaleProductVM> saleProducts;
}

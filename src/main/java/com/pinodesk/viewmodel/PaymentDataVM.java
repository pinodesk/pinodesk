package com.pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pinodesk.constant.PaymentStatus;

import lombok.Data;

@Data
public class PaymentDataVM {
    private BigDecimal paymentAmount;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDueDate;
    private BigDecimal changeAmount;
    private String invoiceNumber;
    private LocalDateTime paymentDateTime;
}

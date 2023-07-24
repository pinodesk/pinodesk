package pospino.desktop.viewmodel;

import java.time.LocalDate;

import lombok.Data;
import pospino.desktop.constant.PaymentStatus;

@Data
public class PurchaseReportFilterVM {
    private String invoiceNumber;
    private LocalDate invoiceDateMin;
    private LocalDate invoiceDateMax;
    private PaymentStatus paymentStatus;
    private String productName;
    private String supplierName;
}

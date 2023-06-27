package pospino.desktop.viewmodel;

import java.time.LocalDate;

import lombok.Data;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.SellingMode;

@Data
public class SaleReportFilterVM {
    private String invoiceNumber;
    private LocalDate invoiceDateMin;
    private LocalDate invoiceDateMax;
    private SellingMode sellingMode;
    private PaymentStatus paymentStatus;
    private String productName;
    private String customerName;
}

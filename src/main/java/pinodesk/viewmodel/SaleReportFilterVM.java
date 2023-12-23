package pinodesk.viewmodel;

import java.time.LocalDate;

import lombok.Data;
import pinodesk.constant.PaymentStatus;
import pinodesk.constant.SellingMode;

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

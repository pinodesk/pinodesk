package pinus.desktop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Purchase extends DataModel {

    public static final String C_SUPPLIER_ID = "supplier_id";
    public static final String C_INVOICE_NUMBER = "invoice_number";
    public static final String C_INVOICE_DATE = "invoice_date";
    public static final String C_TOTAL_PAYMENT = "total_payment";
    public static final String C_TOTAL_PURCHASE = "total_purchase";
    public static final String C_TOTAL_PRODUCT = "total_product";
    public static final String C_PAYMENT_DUE_DATE = "payment_due_date";
    public static final String C_PAYMENT_STATUS = "payment_status";
    public static final String C_DISCOUNT = "discount";
    public static final String C_TAX = "tax";

    private Long supplierId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private BigDecimal totalPurchase;
    private LocalDate paymentDueDate;
    private String paymentStatus;
    private BigDecimal discount;
    private BigDecimal tax;
}

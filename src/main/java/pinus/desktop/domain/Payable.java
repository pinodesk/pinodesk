package pinus.desktop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Payable extends DataModel {

    public static final String C_SUPPLIER_ID = "supplier_id";
    public static final String C_PURCHASE_ID = "purchase_id";
    public static final String C_INVOICE_NUMBER = "invoice_number";
    public static final String C_INVOICE_DATE = "invoice_date";
    public static final String C_PAYMENT_AMOUNT = "payment_amount";
    public static final String C_PAYMENT_DUE_DATE = "payment_due_date";
    public static final String C_PAYMENT_DATE = "payment_date";
    public static final String C_REMARKS = "remarks";

    private Long supplierId;
    private Long purchaseId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal paymentAmount;
    private LocalDate paymentDueDate;
    private LocalDate paymentDate;
    private String remarks;
}

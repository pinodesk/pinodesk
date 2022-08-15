package pinus.desktop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Receivable extends DataModel {

    public static final String C_CUSTOMER_ID = "customer_id";
    public static final String C_SALE_ID = "sale_id";
    public static final String C_INVOICE_NUMBER = "invoice_number";
    public static final String C_INVOICE_DATE = "invoice_date";
    public static final String C_AMOUNT = "amount";
    public static final String C_DUE_DATE = "due_date";
    public static final String C_COMPLETION_DATE = "completion_date";
    public static final String C_REMARKS = "remarks";

    private Long customerId;
    private Long saleId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate completionDate;
    private String remarks;
}

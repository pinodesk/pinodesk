package pinus.desktop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Sale extends DataModel {

    public static final String C_CUSTOMER_ID = "customer_id";
    public static final String C_DOCTOR_ID = "doctor_id";
    public static final String C_SELLING_MODE = "selling_mode";
    public static final String C_INVOICE_NUMBER = "invoice_number";
    public static final String C_PAYMENT_STATUS = "payment_status";
    public static final String C_PAYMENT_DUE_DATE = "payment_due_date";
    public static final String C_TOTAL_PRODUCT = "total_product";
    public static final String C_TOTAL_PAYMENT = "total_payment";
    public static final String C_TOTAL_SALE = "total_sale";

    private Long customerId;
    private Long doctorId;
    private String sellingMode;
    private String invoiceNumber;
    private String paymentStatus;
    private LocalDate paymentDueDate;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private BigDecimal totalSale;
}

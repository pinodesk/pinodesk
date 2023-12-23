package pinodesk.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gitlab.mudiasoft.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Purchase extends DataModel {

    public static final String C_SUPPLIER_ID = "supplier_id";
    public static final String C_INVOICE_NUMBER = "invoice_number";
    public static final String C_INVOICE_DATE = "invoice_date";
    public static final String C_TOTAL_PAYMENT = "total_payment";
    public static final String C_TOTAL_PRICE = "total_price";
    public static final String C_TOTAL_PRODUCT = "total_product";
    public static final String C_PAYMENT_DUE_DATE = "payment_due_date";
    public static final String C_PAYMENT_STATUS = "payment_status";
    public static final String C_ADDITIONAL_DISCOUNT = "additional_discount";
    public static final String C_TOTAL_DISCOUNT = "total_discount";
    public static final String C_TAX = "tax";
    public static final String C_USER_ID = "user_id";

    private Long supplierId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private BigDecimal totalPrice;
    private LocalDate paymentDueDate;
    private String paymentStatus;
    private BigDecimal additionalDiscount;
    private BigDecimal totalDiscount;
    private BigDecimal tax;
    private Long userId;
}

package pinus.desktop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Sale extends DataModel {

    public static final String TABLE_NAME = "sale";

    public static final String C_CUSTOMER_ID = "customer_id";
    public static final String C_DOCTOR_ID = "doctor_id";
    public static final String C_SELLING_MODE = "selling_mode";
    public static final String C_INVOICE_NUMBER = "invoice_number";
    public static final String C_PAYMENT_STATUS = "payment_status";
    public static final String C_PAYMENT_DUE_DATE = "payment_due_date";
    public static final String C_TOTAL_PRODUCT = "total_product";
    public static final String C_TOTAL_PAYMENT = "total_payment";

    @DataColumn(C_CUSTOMER_ID)
    private Long customerId;

    @DataColumn(C_DOCTOR_ID)
    private Long doctorId;

    @DataColumn(C_SELLING_MODE)
    private String sellingMode;

    @DataColumn(C_INVOICE_NUMBER)
    private String invoiceNumber;

    @DataColumn(C_PAYMENT_STATUS)
    private String paymentStatus;

    @DataColumn(C_PAYMENT_DUE_DATE)
    private LocalDate paymentDueDate;

    @DataColumn(C_TOTAL_PRODUCT)
    private Integer totalProduct;

    @DataColumn(C_TOTAL_PAYMENT)
    private BigDecimal totalPayment;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

package pinus.desktop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Purchase extends DataModel {

    public static final String TABLE_NAME = "purchase";

    public static final String C_SUPPLIER_ID = "supplier_id";
    public static final String C_ORDER_NUMBER = "order_number";
    public static final String C_ORDER_DATE = "order_date";
    public static final String C_TOTAL_PAYMENT = "total_payment";
    public static final String C_TOTAL_PURCHASE = "total_purchase";
    public static final String C_TOTAL_PRODUCT = "total_product";
    public static final String C_PAYMENT_METHOD = "payment_method";
    public static final String C_PAYMENT_PERIOD_COUNT = "payment_period_count";
    public static final String C_PAYMENT_PERIOD_UNIT = "payment_period_unit";
    public static final String C_PAYMENT_DUE_DATE = "payment_due_date";
    public static final String C_PAYMENT_STATUS = "payment_status";
    public static final String C_DISCOUNT = "discount";
    public static final String C_TAX = "tax";

    @DataColumn(C_SUPPLIER_ID)
    private Long supplierId;

    @DataColumn(C_ORDER_NUMBER)
    private String orderNumber;

    @DataColumn(C_ORDER_DATE)
    private LocalDate orderDate;

    @DataColumn(C_TOTAL_PRODUCT)
    private Integer totalProduct;

    @DataColumn(C_TOTAL_PAYMENT)
    private BigDecimal totalPayment;

    @DataColumn(C_TOTAL_PURCHASE)
    private BigDecimal totalPurchase;

    @DataColumn(C_PAYMENT_METHOD)
    private String paymentMethod;

    @DataColumn(C_PAYMENT_PERIOD_COUNT)
    private Integer paymentPeriodCount;

    @DataColumn(C_PAYMENT_PERIOD_UNIT)
    private String paymentPeriodUnit;

    @DataColumn(C_PAYMENT_DUE_DATE)
    private LocalDate paymentDueDate;

    @DataColumn(C_PAYMENT_STATUS)
    private String paymentStatus;

    @DataColumn(C_DISCOUNT)
    private BigDecimal discount;

    @DataColumn(C_TAX)
    private BigDecimal tax;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

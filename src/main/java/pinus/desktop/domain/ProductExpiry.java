package pinus.desktop.domain;

import java.time.LocalDate;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductExpiry extends DataModel {

    public static final String TABLE_NAME = "product_expiry";

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_EXPIRED_DATE = "expired_date";
    public static final String C_BATCH_NUMBER = "batch_number";
    public static final String C_QUANTITY_IN = "quantity_in";
    public static final String C_QUANTITY_OUT = "quantity_out";
    public static final String C_FINAL_QUANTITY = "final_quantity";
    public static final String C_PURCHASE_ID = "purchase_id";
    public static final String C_PURCHASE_INVOICE_NUMBER = "purchase_invoice_number";
    public static final String C_SALE_ID = "sale_id";
    public static final String C_SALE_INVOICE_NUMBER = "sale_invoice_number";
    public static final String C_USER_ID = "user_id";
    public static final String C_ACTIVITY = "activity";
    public static final String C_REMARKS = "remarks";

    @DataColumn(C_PRODUCT_ID)
    private Long productId;

    @DataColumn(C_EXPIRED_DATE)
    private LocalDate expiredDate;

    @DataColumn(C_BATCH_NUMBER)
    private String batchNumber;

    @DataColumn(C_QUANTITY_IN)
    private Integer quantityIn;

    @DataColumn(C_QUANTITY_OUT)
    private Integer quantityOut;

    @DataColumn(C_FINAL_QUANTITY)
    private Integer finalQuantity;

    @DataColumn(C_PURCHASE_ID)
    private Long purchaseId;

    @DataColumn(C_PURCHASE_INVOICE_NUMBER)
    private String purchaseInvoiceNumber;

    @DataColumn(C_SALE_ID)
    private Long saleId;

    @DataColumn(C_SALE_INVOICE_NUMBER)
    private String saleInvoiceNumber;

    @DataColumn(C_USER_ID)
    private Long userId;

    @DataColumn(C_ACTIVITY)
    private String activity;

    @DataColumn(C_REMARKS)
    private String remarks;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

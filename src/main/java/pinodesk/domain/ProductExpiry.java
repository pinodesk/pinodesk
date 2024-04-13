package pinodesk.domain;

import java.time.LocalDate;

import com.mudiatech.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductExpiry extends DataModel {

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_EXPIRED_DATE = "expired_date";
    public static final String C_BATCH_NUMBER = "batch_number";
    public static final String C_QUANTITY_IN = "quantity_in";
    public static final String C_QUANTITY_OUT = "quantity_out";
    public static final String C_FINAL_QUANTITY = "final_quantity";
    public static final String C_FINAL_QUANTITY_EXPIRED_DATE = "final_quantity_expired_date";
    public static final String C_PURCHASE_ID = "purchase_id";
    public static final String C_PURCHASE_INVOICE_NUMBER = "purchase_invoice_number";
    public static final String C_SALE_ID = "sale_id";
    public static final String C_SALE_DETAIL_ID = "sale_detail_id";
    public static final String C_SALE_INVOICE_NUMBER = "sale_invoice_number";
    public static final String C_USER_ID = "user_id";
    public static final String C_ACTIVITY = "activity";
    public static final String C_REMARKS = "remarks";

    private Long productId;
    private LocalDate expiredDate;
    private String batchNumber;
    private Integer quantityIn;
    private Integer quantityOut;
    private Integer finalQuantity;
    private Integer finalQuantityExpiredDate;
    private Long purchaseId;
    private String purchaseInvoiceNumber;
    private Long saleId;
    private Long saleDetailId;
    private String saleInvoiceNumber;
    private Long userId;
    private String activity;
    private String remarks;
}

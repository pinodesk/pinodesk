package pinus.desktop.domain;

import java.math.BigDecimal;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductPrice extends DataModel {

    public static final String TABLE_NAME = "product_price";

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_GENERAL_SELLING_PRICE = "general_selling_price";
    public static final String C_PRESCRIPTION_SELLING_PRICE = "prescription_selling_price";
    public static final String C_PURCHASE_ID = "purchase_id";
    public static final String C_PURCHASE_INVOICE_NUMBER = "purchase_invoice_number";
    public static final String C_USER_ID = "user_id";
    public static final String C_ACTIVITY = "activity";
    public static final String C_REMARKS = "remarks";

    @DataColumn(C_PRODUCT_ID)
    private Long productId;

    @DataColumn(C_GENERAL_SELLING_PRICE)
    private BigDecimal generalSellingPrice;

    @DataColumn(C_PRESCRIPTION_SELLING_PRICE)
    private BigDecimal prescriptionSellingPrice;

    @DataColumn(C_PURCHASE_ID)
    private Long purchaseId;

    @DataColumn(C_PURCHASE_INVOICE_NUMBER)
    private String purchaseInvoiceNumber;

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

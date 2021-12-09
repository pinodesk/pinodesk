package pinus.desktop.domain;

import java.math.BigDecimal;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PurchaseDetail extends DataModel {

    public static final String TABLE_NAME = "purchase_detail";

    public static final String C_PURCHASE_ID = "purchase_id";
    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_PRODUCT_PRICE = "product_price";
    public static final String C_QUANTITY = "quantity";
    public static final String C_SUBTOTAL = "subtotal";

    @DataColumn(C_PURCHASE_ID)
    private Long purchaseId;

    @DataColumn(C_PRODUCT_ID)
    private Long productId;

    @DataColumn(C_QUANTITY)
    private Integer quantity;

    @DataColumn(C_PRODUCT_PRICE)
    private BigDecimal productPrice;

    @DataColumn(C_SUBTOTAL)
    private BigDecimal subtotal;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

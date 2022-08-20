package stoready.desktop.domain;

import java.math.BigDecimal;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PurchaseDetail extends DataModel {

    public static final String C_PURCHASE_ID = "purchase_id";
    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_BUYING_PRICE = "buying_price";
    public static final String C_QUANTITY = "quantity";
    public static final String C_SUBTOTAL = "subtotal";

    private Long purchaseId;
    private Long productId;
    private Integer quantity;
    private BigDecimal buyingPrice;
    private BigDecimal subtotal;
}

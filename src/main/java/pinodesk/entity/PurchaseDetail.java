package pinodesk.entity;

import java.math.BigDecimal;

import com.mudiatech.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PurchaseDetail extends DataModel {

    public static final String C_PURCHASE_ID = "purchase_id";
    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_BUYING_PRICE = "buying_price";
    public static final String C_QUANTITY = "quantity";
    public static final String C_SUBTOTAL_PRICE = "subtotal_price";
    public static final String C_DISCOUNT_TYPE = "discount_type";
    public static final String C_DISCOUNT_AMOUNT = "discount_amount";
    public static final String C_BUYING_PRICE_DISCOUNT = "buying_price_discount";
    public static final String C_SUBTOTAL_DISCOUNT = "subtotal_discount";

    private Long purchaseId;
    private Long productId;
    private Integer quantity;
    private BigDecimal buyingPrice;
    private BigDecimal subtotalPrice;
    private String discountType;
    private BigDecimal discountAmount;
    private BigDecimal buyingPriceDiscount;
    private BigDecimal subtotalDiscount;
}

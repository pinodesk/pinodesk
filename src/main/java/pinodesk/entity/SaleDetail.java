package pinodesk.entity;

import java.math.BigDecimal;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SaleDetail extends DataModel {

    public static final String C_SALE_ID = "sale_id";
    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_SELLING_PRICE = "selling_price";
    public static final String C_QUANTITY = "quantity";
    public static final String C_SUBTOTAL = "subtotal";

    private Long saleId;
    private Long productId;
    private Integer quantity;
    private BigDecimal sellingPrice;
    private BigDecimal subtotal;
}

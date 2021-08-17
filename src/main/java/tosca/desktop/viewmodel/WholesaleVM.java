package tosca.desktop.viewmodel;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class WholesaleVM {
    private Long productId;
    private Integer purchaseQuantity;
    private BigDecimal sellingPrice;
    private String vatIncluded;
}

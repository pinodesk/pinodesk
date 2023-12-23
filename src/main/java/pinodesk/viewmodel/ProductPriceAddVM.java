package pinodesk.viewmodel;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductPriceAddVM {
    private Long productId;
    private BigDecimal generalSellingPrice;
    private BigDecimal prescriptionSellingPrice;
    private String remarks;
}

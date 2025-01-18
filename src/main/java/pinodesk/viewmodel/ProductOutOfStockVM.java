package pinodesk.viewmodel;

import lombok.Data;

@Data
public class ProductOutOfStockVM {
    private String categoryName;
    private String productName;
    private Long productId;
    private Integer quantity;
}

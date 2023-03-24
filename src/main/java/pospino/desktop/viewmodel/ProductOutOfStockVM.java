package pospino.desktop.viewmodel;

import lombok.Data;

@Data
public class ProductOutOfStockVM {
    private String categoryName;
    private String productName;
    private Integer quantity;
}

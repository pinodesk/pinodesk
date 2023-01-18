package pospino.desktop.viewmodel;

import lombok.Data;

@Data
public class ProductStockAddVM {
    private Long productId;
    private Integer quantity;
    private String remarks;
}

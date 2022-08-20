package stoready.desktop.viewmodel;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProductExpiryAddVM {
    private Long productId;
    private LocalDate expiredDate;
    private String batchNumber;
    private Integer quantity;
    private String remarks;
}

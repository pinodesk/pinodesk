package pinodesk.viewmodel;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProductClosestExpiryVM {
    private String categoryName;
    private Long productId;
    private String productName;
    private LocalDate expiredDate;
}

package pospino.desktop.viewmodel;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProductClosestExpiryVM {
    private String categoryName;
    private String productName;
    private LocalDate expiredDate;
}

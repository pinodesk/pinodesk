package pospino.desktop.viewmodel;

import java.time.LocalDate;

import lombok.Data;

@Data
public class GroupedProductExpiryVM {
    private Long productId;
    private LocalDate expiredDate;
    private Integer quantity;
}

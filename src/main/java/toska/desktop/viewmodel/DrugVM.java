package toska.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DrugVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long productId;
    private Long drugCategoryId;
    private String drugCategoryCode;
    private String drugCategoryName;
    private String indication;
    private String contraindication;
    private BigDecimal prescriptionPrice;
}

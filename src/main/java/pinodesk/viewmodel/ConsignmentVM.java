package pinodesk.viewmodel;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConsignmentVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Long supplierId;
    private String supplierName;
    private Long userId;
    private String userFullName;
    private Integer totalProduct;
}

package pospino.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SupplierContactVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long supplierId;
    private String name;
    private String phone;
    private String email;
}

package stoready.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SupplierVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String website;
    private String address;
}

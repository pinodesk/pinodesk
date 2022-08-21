package stoready.desktop.viewmodel;

import lombok.Data;

@Data
public class SupplierContactAddVM {
    private Long supplierId;
    private String name;
    private String phone;
    private String email;
}

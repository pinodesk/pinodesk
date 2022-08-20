package stoready.desktop.viewmodel;

import lombok.Data;

@Data
public class CustomerEditVM {
    private Long id;
    private String name;
    private String code;
    private String phone;
    private String email;
    private String address;
}

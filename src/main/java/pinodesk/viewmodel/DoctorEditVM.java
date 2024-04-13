package pinodesk.viewmodel;

import lombok.Data;

@Data
public class DoctorEditVM {
    private DoctorCategoryVM category;
    private String code;
    private String name;
    private String registrationNumber;
    private String medicalLicenseNumber;
    private String phone;
    private String email;
    private String address;
}

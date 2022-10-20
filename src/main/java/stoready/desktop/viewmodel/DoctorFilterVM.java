package stoready.desktop.viewmodel;

import lombok.Data;

@Data
public class DoctorFilterVM {
    private String code;
    private String name;
    private String registrationNumber;
    private String medicalLicenseNumber;
    private DoctorCategoryVM category;
}

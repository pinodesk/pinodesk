package pinus.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DoctorVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String categoryCode;
    private String categoryName;
    private String code;
    private String name;
    private String registrationNumber;
    private String medicalLicenseNumber;
}

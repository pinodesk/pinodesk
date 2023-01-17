package pospino.desktop.viewmodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DoctorVM {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    private String code;
    private String name;
    private String registrationNumber;
    private String medicalLicenseNumber;
    private String phone;
    private String email;
    private String address;
}

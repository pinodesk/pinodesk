package com.pinodesk.apimodel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterInstallationResponse {
    private String installationId;
    private String installationToken;
    private String deviceSignature;
    private LocalDateTime registeredAt;
}
package com.pinodesk.apimodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterInstallationRequest {
    private String email;
    private String installationRequestId;
    private String installationCode;
    private String releasePlatform;
    private String releaseVersion;
    private String deviceSignature;
    private String deviceModel;
    private String deviceManufacturer;
    private String osName;
    private String osArch;
    private String osVersion;
    private String osFamily;
    private Integer osBitness;
    private String cpuName;
    private String cpuFamily;
    private String cpuVendor;
    private Long ramSize;
    private Long storageSize;
}
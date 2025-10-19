package pinodesk.apimodel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ActivateReleaseRequest {
    private String activationCode;
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

package pinodesk.apimodel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActivateReleaseResponse {
    private Long activationDeviceId;
    private Long activationId;
    private String email;
    private String code;
    private String status;
}

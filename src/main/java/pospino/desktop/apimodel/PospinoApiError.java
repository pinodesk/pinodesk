package pospino.desktop.apimodel;

import lombok.Data;

@Data
public class PospinoApiError {
    private String code;
    private String message;
    private String debug;
}

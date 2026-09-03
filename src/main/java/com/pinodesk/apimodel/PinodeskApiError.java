package com.pinodesk.apimodel;

import lombok.Data;

@Data
public class PinodeskApiError {
    private String code;
    private String message;
    private String debug;
}

package com.pinodesk.apimodel;

import lombok.Data;

@Data
public class PinodeskApiResponse<T> {
    private boolean success;
    private T data;
    private PinodeskApiError error;
}

package pospino.desktop.apimodel;

import lombok.Data;

@Data
public class PospinoApiResponse<T> {
    private boolean success;
    private T data;
    private PospinoApiError error;
}

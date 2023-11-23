package pospino.desktop.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PospinoApiException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String code;
    private final String message;

}

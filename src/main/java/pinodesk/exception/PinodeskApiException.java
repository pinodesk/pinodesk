package pinodesk.exception;

import com.pinodesk.pandora.utility.IMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PinodeskApiException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String code;
    private final String message;
    private final IMessage messageCode;

}

package stoready.desktop.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import stoready.desktop.constant.MessageCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class PrinterException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final MessageCode messageCode;
}

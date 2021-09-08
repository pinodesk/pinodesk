package toscabox.desktop.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import toscabox.desktop.constant.DomainError;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class DomainException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final DomainError error;

}

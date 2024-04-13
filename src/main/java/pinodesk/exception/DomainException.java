package pinodesk.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pinodesk.constant.DomainError;

@Data
@EqualsAndHashCode(callSuper = false)
public class DomainException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final DomainError error;
    private final Object[] arguments;

    public DomainException(DomainError error) {
        this.error = error;
        this.arguments = new Object[] {};
    }

    public DomainException(DomainError error, Object... arguments) {
        this.error = error;
        this.arguments = arguments;
    }

}

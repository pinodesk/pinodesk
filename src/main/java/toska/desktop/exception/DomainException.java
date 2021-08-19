package toska.desktop.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import toska.desktop.constant.DomainError;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class DomainException extends RuntimeException {
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final DomainError error;

}

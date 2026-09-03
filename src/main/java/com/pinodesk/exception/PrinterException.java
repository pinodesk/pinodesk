package com.pinodesk.exception;

import com.pinodesk.constant.MessageCode;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PrinterException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final MessageCode messageCode;
    private final Object[] arguments;

    public PrinterException(MessageCode messageCode, Object... arguments) {
        this.messageCode = messageCode;
        this.arguments = arguments;
    }

}

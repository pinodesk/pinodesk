package com.getkembang.kembangdesktop.exception;

import com.getkembang.kembangdesktop.constant.DomainError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

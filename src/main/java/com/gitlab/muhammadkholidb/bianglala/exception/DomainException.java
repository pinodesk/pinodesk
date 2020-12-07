package com.gitlab.muhammadkholidb.bianglala.exception;

import com.gitlab.muhammadkholidb.bianglala.constant.DomainError;

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

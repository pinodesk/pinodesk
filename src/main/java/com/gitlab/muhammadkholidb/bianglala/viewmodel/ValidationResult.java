package com.gitlab.muhammadkholidb.bianglala.viewmodel;

import com.gitlab.muhammadkholidb.bianglala.constant.MessageCode;

import lombok.Getter;

@Getter
public class ValidationResult {

    private boolean error;
    private MessageCode messageCode;

    public void setError(MessageCode messageCode) {
        this.error = true;
        this.messageCode = messageCode;
    }

}

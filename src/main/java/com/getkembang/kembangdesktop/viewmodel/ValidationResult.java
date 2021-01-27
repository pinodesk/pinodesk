package com.getkembang.kembangdesktop.viewmodel;

import com.getkembang.kembangdesktop.constant.MessageCode;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidationResult {

    @Getter
    private MessageCode messageCode;

    public ValidationResult(MessageCode messageCode) {
        this.messageCode = messageCode;
    }

    public void setError(MessageCode messageCode) {
        this.messageCode = messageCode;
    }

    public boolean isError() {
        return this.messageCode != null;
    }

}

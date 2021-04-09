package com.getkembang.kembangdesktop.utility;

import java.util.HashSet;
import java.util.Set;

import com.getkembang.kembangdesktop.constant.MessageCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ValidationResult {

    @Getter
    private Set<MessageCode> messageCodes;

    public void addError(MessageCode messageCode) {
        if (messageCodes == null) {
            messageCodes = new HashSet<>();
        }
        messageCodes.add(messageCode);
    }

    public boolean hasError() {
        return messageCodes != null && !messageCodes.isEmpty();
    }

}

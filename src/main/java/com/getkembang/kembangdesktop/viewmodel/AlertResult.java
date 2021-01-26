package com.getkembang.kembangdesktop.viewmodel;

import java.util.Optional;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlertResult {

    private Optional<ButtonType> result;

    public boolean isOk() {
        return hasButtonData(ButtonData.OK_DONE);
    }

    public boolean isNo() {
        return hasButtonData(ButtonData.NO);
    }

    public boolean isYes() {
        return hasButtonData(ButtonData.YES);
    }

    public boolean isConfirmed() {
        return isYes();
    }

    public boolean hasButtonData(ButtonData buttonData) {
        return result.isPresent() && result.get().getButtonData().equals(buttonData);
    }

}

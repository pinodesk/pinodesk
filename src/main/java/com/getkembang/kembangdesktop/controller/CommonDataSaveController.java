package com.getkembang.kembangdesktop.controller;

import com.getkembang.kembangdesktop.viewmodel.ValidationResult;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public abstract class CommonDataSaveController extends CommonParentVBoxController {
    
    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnSave;

    private boolean hasDataSaved;

    private boolean lastDataSaved;

    protected boolean isLastDataSaved() {
        return lastDataSaved;
    }

    @FXML
    void onActionBtnSave(ActionEvent event) {
        processDataSave();
        if (hasDataSaved) {
            setPrevPageData(Boolean.TRUE);
            close();
        }
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        if (hasDataSaved) {
            setPrevPageData(Boolean.TRUE);
        }
        close();
    }

    protected void processDataSave() {
        lastDataSaved = false;
        ValidationResult result = validateValues();
        if (result.hasError()) {
            displayError(result.getMessageCodes());
            return;
        }
        lastDataSaved = save();
        hasDataSaved = true;
    }

    protected abstract ValidationResult validateValues();

    protected abstract boolean save();

}

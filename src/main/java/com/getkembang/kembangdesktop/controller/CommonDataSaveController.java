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

    private boolean dataSaved;

    protected boolean isDataSaved() {
        return dataSaved;
    }

    @FXML
    void onActionBtnSave(ActionEvent event) {
        processDataSave();
        if (dataSaved) {
            setPrevPageData(Boolean.TRUE);
            close();
        }
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        if (dataSaved) {
            setPrevPageData(Boolean.TRUE);
        }
        close();
    }

    protected void processDataSave() {
        dataSaved = false;
        ValidationResult result = validateValues();
        if (result.hasError()) {
            displayError(result.getMessageCodes());
            return;
        }
        dataSaved = save();
    }

    protected abstract ValidationResult validateValues();

    protected abstract boolean save();

}

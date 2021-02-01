package com.getkembang.kembangdesktop.controller;

import com.getkembang.kembangdesktop.viewmodel.ValidationResult;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public abstract class BaseDataModController extends BaseParentVBoxController {
    
    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnSave;

    protected boolean saved;

    @FXML
    void onActionBtnSave(ActionEvent event) {
        ValidationResult result = validateValues();
        if (result.isError()) {
            displayError(result.getMessageCode());
            return;
        }
        if (save()) {
            setPrevPageData(Boolean.TRUE);
            close();
        }
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        if (saved) {
            setPrevPageData(Boolean.TRUE);
        }
        close();
    }

    protected abstract ValidationResult validateValues();

    protected abstract boolean save();

}

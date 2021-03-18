package com.getkembang.kembangdesktop.controller;

import com.getkembang.kembangdesktop.utility.FXUtils;
import com.getkembang.kembangdesktop.utility.ValidationResult;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

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
        if (!hasDataSaved) {
            hasDataSaved = true;
        }
    }

    @Override
    protected void initParentVBoxControlActions() {
        initDataSaveControlActions();
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyCode.ENTER.equals(event.getCode()) || FXUtils.CTRL_S.match(event)) {
                btnSave.fire();
                return;
            }
            if (KeyCode.ESCAPE.equals(event.getCode())) {
                btnCancel.fire();
                return;
            }
        });
    }

    @Override
    protected void initControlValues() {
        initDataSaveControlValues();
    }

    protected abstract void initDataSaveControlActions();

    protected abstract void initDataSaveControlValues();

    protected abstract ValidationResult validateValues();

    protected abstract boolean save();

}

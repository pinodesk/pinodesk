package com.getkembang.kembangdesktop.controller;

import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.utility.ValidationResult;

import org.apache.commons.lang3.ObjectUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

public abstract class CommonDataSaveController extends CommonParentVBoxController {

    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnSave;

    /**
     * A flag indicating the controller has done at least once successful data saving.
     */
    private boolean hasDataSaved;

    private Object lastDataSaved;

    protected boolean isLastDataSaved() {
        return Boolean.TRUE.equals(lastDataSaved) || ObjectUtils.isNotEmpty(lastDataSaved);
    }

    @FXML
    void onActionBtnSave(ActionEvent event) {
        processDataSave();
        if (hasDataSaved) {
            setPageData(lastDataSaved);
            close();
        }
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        if (hasDataSaved) {
            setPageData(lastDataSaved);
        }
        close();
    }

    protected void processDataSave() {
        lastDataSaved = null;
        ValidationResult result = validateValues();
        if (result.hasError()) {
            displayError(result.getMessages());
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
            if (KeyCode.ENTER.equals(event.getCode()) || KeyConstants.CTRL_S.match(event)) {
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

    /**
     * Handles the process of storing and returns true or non empty object for successful operation.
     * 
     * @return the saved object.
     */
    protected abstract Object save();

}

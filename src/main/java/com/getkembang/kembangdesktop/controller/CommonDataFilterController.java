package com.getkembang.kembangdesktop.controller;

import com.getkembang.kembangdesktop.utility.FXUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

public abstract class CommonDataFilterController<T> extends CommonParentVBoxController {

    protected T currentFilter;

    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnReset;

    @FXML
    protected Button btnFilter;

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        setPrevPageData(currentFilter);
        close();
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPrevPageData(getFreshFilterValues());
        close();
    }

    @FXML
    void onActionBtnReset(ActionEvent event) {
        resetControls();
    }

    @Override
    protected void initControlValues() {
        currentFilter = getPageData();
        initDataFilterControlValues();
    }

    @Override
    protected void initParentVBoxControlActions() {
        initDataFilterControlActions();
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyCode.ENTER.equals(event.getCode()) || FXUtils.CTRL_S.match(event)) {
                btnFilter.fire();
                return;
            }
            if (KeyCode.ESCAPE.equals(event.getCode())) {
                btnCancel.fire();
                return;
            }
            if (FXUtils.CTRL_R.match(event)) {
                btnReset.fire();
                return;
            }
        });
    }

    protected abstract void initDataFilterControlValues();

    protected abstract void initDataFilterControlActions();

    protected abstract T getFreshFilterValues();

    protected abstract void resetControls();

}

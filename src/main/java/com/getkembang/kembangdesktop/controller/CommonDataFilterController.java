package com.getkembang.kembangdesktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    protected void initControlsValues() {
        currentFilter = getPageData();
        initFilterControlsValues();
        contentPane.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                btnFilter.fire();
            }
        });
    }

    protected abstract void initFilterControlsValues();

    protected abstract T getFreshFilterValues();

    protected abstract void resetControls();

}

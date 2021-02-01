package com.getkembang.kembangdesktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseDataFilterController<T> extends BaseParentVBoxController {
    
    private T filterVM;

    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnReset;

    @FXML
    protected Button btnFilter;

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        setPrevPageData(filterVM);
        close();
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setFilterValues(filterVM);
        setPrevPageData(filterVM);
        close();
    }

    @FXML
    void onActionBtnReset(ActionEvent event) {
        resetControls();
    }

    @Override
    protected void initControlsValues() {
        filterVM = getPageData();
        initFilterControlsValues(filterVM);
        contentPane.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                btnFilter.fire();
            }
        });
    }

    protected abstract void initFilterControlsValues(T vm);

    protected abstract void setFilterValues(T vm);
    
    protected abstract void resetControls();

}

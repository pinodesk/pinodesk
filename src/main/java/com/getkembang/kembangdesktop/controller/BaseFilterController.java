package com.getkembang.kembangdesktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseFilterController<T> extends BaseController {
    
    private T filterVM;

    @FXML
    private VBox contentPane;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnFilter;

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

    @Override
    protected Stage getCurrentStage() {
        return (Stage) contentPane.getScene().getWindow();
    }

    protected abstract void initFilterControlsValues(final T vm);

    protected abstract void setFilterValues(final T vm);
    
    protected abstract void resetControls();

}

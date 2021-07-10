package com.getkembang.kembangdesktop.controller.purchase;

import com.getkembang.kembangdesktop.controller.CommonDataSaveController;

import org.controlsfx.validation.ValidationSupport;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PurchaseAddController extends CommonDataSaveController {

    @FXML
    private Button btnSaveAndAdd;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        
    }

    @Override
    protected void initDataSaveControlActions() {

    }

    @Override
    protected void initDataSaveControlValues() {
        
    }

    @Override
    protected void registerValidator(ValidationSupport vs) {
        
    }

    @Override
    protected Object save() {
        return true;
    }

}

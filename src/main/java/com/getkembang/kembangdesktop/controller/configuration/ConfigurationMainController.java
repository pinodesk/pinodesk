package com.getkembang.kembangdesktop.controller.configuration;

import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.BaseController;
import com.getkembang.kembangdesktop.service.ConfigurationService;

import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConfigurationMainController extends BaseController {

    @FXML
    private Button btnSave;

    @FXML
    private TextField tfStoreName;

    @FXML
    private TextField tfStoreAddress;

    @FXML
    private TextField tfVatPercentage;

    @FXML
    private ComboBox<?> cbDrugCategoryBase;

    @FXML
    private ComboBox<?> cbLanguage;

    private ConfigurationService configurationService;

    @FXML
    void onActionBtnSave(ActionEvent event) {

    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        configurationService = ctx.getBean(ConfigurationService.class);
    }

    @Override
    protected void initControlActions() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void initControlValues() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected Page getCurrentPage() {
        return Page.SETTINGS_CONFIGURATION_MAIN;
    }

    @Override
    protected Stage getCurrentStage() {
        // TODO Auto-generated method stub
        return null;
    }

}

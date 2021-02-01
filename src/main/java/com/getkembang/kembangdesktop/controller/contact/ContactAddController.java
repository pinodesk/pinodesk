package com.getkembang.kembangdesktop.controller.contact;

import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.BaseParentVBoxController;
import com.getkembang.kembangdesktop.viewmodel.BasicComboBoxVM;

import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

// BaseSaveController
public class ContactAddController extends BaseParentVBoxController {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfPhone;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfAddress;

    @FXML
    private TextField tfCompanyName;

    @FXML
    private ComboBox<BasicComboBoxVM> cbContactType;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSaveAndAdd;

    @FXML
    private Button btnSave;

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        close();
    }

    @FXML
    void onActionBtnSave(ActionEvent event) {
        close();
    }

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        close();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initControlsActions() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initControlsValues() {
        // TODO Auto-generated method stub

    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_CONTACT_ADD;
    }

}

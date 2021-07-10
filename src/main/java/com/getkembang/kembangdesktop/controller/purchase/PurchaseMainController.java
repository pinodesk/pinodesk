package com.getkembang.kembangdesktop.controller.purchase;

import java.util.Date;

import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.BaseController;
import com.getkembang.kembangdesktop.viewmodel.CustomerVM;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;

import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class PurchaseMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<CustomerVM> tblPurchaseOrder;

    @FXML
    private TableColumn<CustomerVM, String> colCode;

    @FXML
    private TableColumn<CustomerVM, String> colName;

    @FXML
    private TableColumn<CustomerVM, String> colPhone;

    @FXML
    private TableColumn<CustomerVM, String> colEmail;

    @FXML
    private TableColumn<CustomerVM, String> colAddress;

    @FXML
    private TableColumn<CustomerVM, Date> colCreatedAt;

    @FXML
    private TableColumn<CustomerVM, Date> colUpdatedAt;

    @FXML
    private Label lblRows;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.TRANSACTION_PURCHASE_ADD, true);
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        
    }

    @Override
    protected void initControlActions() {
        
    }

    @Override
    protected void initControlValues() {
        
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

}

package com.getkembang.kembangdesktop.controller.supplier;

import java.util.Date;
import java.util.stream.Collectors;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.BaseController;
import com.getkembang.kembangdesktop.service.SupplierService;
import com.getkembang.kembangdesktop.viewmodel.SupplierFilterVM;
import com.getkembang.kembangdesktop.viewmodel.SupplierVM;
import com.gitlab.muhammadkholidb.pandora.factory.DateCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class SupplierMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<SupplierVM> tableSupplier;

    @FXML
    private TableColumn<SupplierVM, String> colCode;

    @FXML
    private TableColumn<SupplierVM, String> colName;

    @FXML
    private TableColumn<SupplierVM, String> colPhone;

    @FXML
    private TableColumn<SupplierVM, String> colEmail;

    @FXML
    private TableColumn<SupplierVM, String> colAddress;

    @FXML
    private TableColumn<SupplierVM, String> colWebsite;

    @FXML
    private TableColumn<SupplierVM, Date> colCreatedAt;

    @FXML
    private TableColumn<SupplierVM, Date> colUpdatedAt;

    @FXML
    private Label lblRows;

    private SupplierService supplierService;

    private SupplierFilterVM supplierFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.MASTER_SUPPLIER_ADD, false, we -> {
            if (getPageData() != null) {
                searchSuppliers();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(supplierFilter);
        StageUtils.modal(Page.MASTER_SUPPLIER_FILTER, false, we -> {
            supplierFilter = getPageData();
            searchSuppliers();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<SupplierVM> items = tableSupplier.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_CUSTOMERS);
            if (result.isConfirmed()) {
                supplierService.removeSuppliers(items.stream().map(SupplierVM::getId).collect(Collectors.toList()));
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_CUSTOMERS);
                searchSuppliers();
            }
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        supplierService = ctx.getBean(SupplierService.class);
    }

    @Override
    protected void initControlActions() {
        TableViewUtils.setColumnValue(colCode, SupplierVM::getCode);
        TableViewUtils.setColumnValue(colName, SupplierVM::getName);
        TableViewUtils.setColumnValue(colPhone, SupplierVM::getPhone);
        TableViewUtils.setColumnValue(colEmail, SupplierVM::getEmail);
        TableViewUtils.setColumnValue(colAddress, SupplierVM::getAddress);
        TableViewUtils.setColumnValue(colWebsite, SupplierVM::getWebsite);
        TableViewUtils.initTableColumn(colCreatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                SupplierVM::getCreatedAt);
        TableViewUtils.initTableColumn(colUpdatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                SupplierVM::getUpdatedAt);
        tableSupplier.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableSupplier.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableSupplier();
            }
        });
        tableSupplier.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableSupplier();
            }
        });
    }

    @Override
    protected void initControlValues() {
        supplierFilter = new SupplierFilterVM();
        searchSuppliers();
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_CUSTOMER_MAIN;
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    @SuppressWarnings("unchecked")
    private void searchSuppliers() {
        tableSupplier.setPlaceholder(new Label(translate("lbl.loadingdata")));
        tableSupplier.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> supplierService.searchSuppliers(supplierFilter))
                .thenAccept(customers -> Platform.runLater(() -> {
                    if (customers.isEmpty()) {
                        tableSupplier.setPlaceholder(new Label(translate("lbl.nodata")));
                        lblRows.setText("0");
                    }
                    tableSupplier.setItems(FXCollections.observableList(customers));
                    tableSupplier.getSortOrder().setAll(colName); // Always sort by name after searching
                    lblRows.setText(customers.size() + "");
                }));
    }

    private void handleActionTableSupplier() {
        SupplierVM selected = tableSupplier.getSelectionModel().getSelectedItem();
        setPageData(selected);
        StageUtils.modal(Page.MASTER_SUPPLIER_EDIT, false, event -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchSuppliers();
            }
        });
    }

}

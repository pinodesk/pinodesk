package com.getkembang.kembangdesktop.controller.customer;

import java.util.Date;
import java.util.stream.Collectors;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.BaseController;
import com.getkembang.kembangdesktop.javafx.factory.DateCellFactory;
import com.getkembang.kembangdesktop.service.CustomerService;
import com.getkembang.kembangdesktop.utility.Async;
import com.getkembang.kembangdesktop.utility.FXUtils;
import com.getkembang.kembangdesktop.utility.TableViewUtils;
import com.getkembang.kembangdesktop.viewmodel.AlertResult;
import com.getkembang.kembangdesktop.viewmodel.CustomerFilterVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerVM;

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

public class CustomerMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<CustomerVM> tableCustomer;

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

    private CustomerService customerService;

    private CustomerFilterVM customerFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        Page nextPage = Page.MASTER_CUSTOMER_ADD;
        setNextPage(nextPage);
        FXUtils.show(nextPage, false, we -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchCustomers();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        Page nextPage = Page.MASTER_CUSTOMER_FILTER;
        setNextPageData(nextPage, customerFilter);
        FXUtils.show(nextPage, false, we -> {
            customerFilter = getPageData();
            searchCustomers();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<CustomerVM> items = tableCustomer.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_CUSTOMERS);
            if (result.isConfirmed()) {
                customerService.removeCustomers(items.stream().map(CustomerVM::getId).collect(Collectors.toList()));
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_CUSTOMERS);
                searchCustomers();
            }
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        customerService = ctx.getBean(CustomerService.class);
    }

    @Override
    protected void initControlActions() {
        TableViewUtils.setColumnValue(colCode, CustomerVM::getCode);
        TableViewUtils.setColumnValue(colName, CustomerVM::getName);
        TableViewUtils.setColumnValue(colPhone, CustomerVM::getPhone);
        TableViewUtils.setColumnValue(colEmail, CustomerVM::getEmail);
        TableViewUtils.setColumnValue(colAddress, CustomerVM::getAddress);
        TableViewUtils.initTableColumn(colCreatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                CustomerVM::getCreatedAt);
        TableViewUtils.initTableColumn(colUpdatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                CustomerVM::getUpdatedAt);
        tableCustomer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableCustomer.setOnMouseClicked(event -> {
            if (FXUtils.isDoubleClick(event)) {
                handleActionTableCustomer();
            }
        });
        tableCustomer.setOnKeyPressed(event -> {
            if (FXUtils.isEnter(event)) {
                handleActionTableCustomer();
            }
        });
    }

    @Override
    protected void initControlValues() {
        customerFilter = new CustomerFilterVM();
        searchCustomers();
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
    private void searchCustomers() {
        tableCustomer.setPlaceholder(new Label(translate("lbl.loadingdata")));
        tableCustomer.setItems(FXCollections.observableArrayList());
        Async.supply(() -> customerService.searchCustomers(customerFilter))
                .thenAccept(customers -> Platform.runLater(() -> {
                    if (customers.isEmpty()) {
                        tableCustomer.setPlaceholder(new Label(translate("lbl.nodata")));
                        lblRows.setText("0");
                    }
                    tableCustomer.setItems(FXCollections.observableList(customers));
                    tableCustomer.getSortOrder().setAll(colName); // Always sort by name after searching
                    lblRows.setText(customers.size() + "");
                }));
    }

    private void handleActionTableCustomer() {
        CustomerVM selected = tableCustomer.getSelectionModel().getSelectedItem();
        Page nextPage = Page.MASTER_CUSTOMER_EDIT;
        setNextPageData(nextPage, selected);
        FXUtils.show(nextPage, false, event -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchCustomers();
            }
        });
    }

}

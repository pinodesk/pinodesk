package pinus.desktop.controller.customer;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
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
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.constant.Page;
import pinus.desktop.controller.BaseController;
import pinus.desktop.service.CustomerService;
import pinus.desktop.viewmodel.CustomerFilterVM;
import pinus.desktop.viewmodel.CustomerVM;

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
    private TableColumn<CustomerVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<CustomerVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private CustomerService customerService;

    private CustomerFilterVM customerFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.MASTER_CUSTOMER_ADD, false, we -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchCustomers();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(customerFilter);
        StageUtils.modal(Page.MASTER_CUSTOMER_FILTER, false, we -> {
            CustomerFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            customerFilter = result;
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
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                CustomerVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                CustomerVM::getUpdatedAt);
        tableCustomer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableCustomer.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableCustomer();
            }
        });
        tableCustomer.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
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
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchCustomers() {
        tableCustomer.setPlaceholder(new Label(translate(CommonLabel.LBL_LOADING_DATA)));
        tableCustomer.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> customerService.searchCustomers(customerFilter))
                .thenAccept(customers -> Platform.runLater(() -> {
                    if (customers.isEmpty()) {
                        tableCustomer.setPlaceholder(new Label(translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tableCustomer.setItems(FXCollections.observableList(customers));
                    TableViewUtils.sortDescending(tableCustomer, colUpdatedAt);
                    lblRows.setText(customers.size() + "");
                }));
    }

    private void handleActionTableCustomer() {
        if (TableViewUtils.hasItemSelected(tableCustomer)) {
            setPageData(TableViewUtils.getSelectedItem(tableCustomer));
            StageUtils.modal(Page.MASTER_CUSTOMER_EDIT, false, event -> {
                if (Boolean.TRUE.equals(getPageData())) {
                    searchCustomers();
                }
            });

        }
    }

}

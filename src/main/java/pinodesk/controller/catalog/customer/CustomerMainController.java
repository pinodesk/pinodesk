package pinodesk.controller.catalog.customer;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.gitlab.mudiasoft.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.toolbox.data.StringNumberUtils;
import com.gitlab.mudiasoft.toolbox.future.AsyncUtils;

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
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.MenuCodeConstants;
import pinodesk.constant.MessageCode;
import pinodesk.constant.Page;
import pinodesk.controller.BaseController;
import pinodesk.service.CustomerService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.CustomerFilterVM;
import pinodesk.viewmodel.CustomerVM;

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
        StageUtils.modal(Page.CATALOG_CUSTOMER_ADD, false, we -> {
            if (getPageData() != null) {
                searchCustomers();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(customerFilter);
        StageUtils.modal(Page.CATALOG_CUSTOMER_FILTER, false, we -> {
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
    protected void initServices() {
        customerService = SpringUtils.getBean(CustomerService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.CATALOG_CUSTOMERS, btnAdd, btnRemove);
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
        tableCustomer.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
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
        tableCustomer.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tableCustomer.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> customerService.searchCustomers(customerFilter))
                .thenAccept(customers -> Platform.runLater(() -> {
                    if (customers.isEmpty()) {
                        tableCustomer.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tableCustomer.setItems(FXCollections.observableList(customers));
                    TableViewUtils.sortDescending(tableCustomer, colUpdatedAt);
                    lblRows.setText(StringNumberUtils.format(customers.size(), resources.getLocale()));
                }));
    }

    private void handleActionTableCustomer() {
        if (TableViewUtils.hasItemSelected(tableCustomer)) {
            setPageData(TableViewUtils.getSelectedItem(tableCustomer));
            StageUtils.modal(Page.CATALOG_CUSTOMER_EDIT, false, event -> {
                if (getPageData() != null) {
                    searchCustomers();
                }
            });

        }
    }

}

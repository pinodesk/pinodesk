package pinodesk.controller.catalog.customer;

import java.time.LocalDateTime;

import com.gitlab.mudiasoft.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.toolbox.future.AsyncUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.controller.CommonDataChooseController;
import pinodesk.service.CustomerService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.CustomerVM;

public class CustomerChooseController extends CommonDataChooseController<CustomerVM> {

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<CustomerVM> tblCustomer;

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

    private CustomerService customerService;

    @Override
    protected void initDataChooseControlActions() {
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
        tblCustomer.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblCustomer.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblCustomer.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                btnChoose.fire();
            }
        });
        tfSearch.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                searchCustomers();
            }
        });
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        // Nothing to do
    }

    @Override
    protected CustomerVM getSelectedData() {
        return tblCustomer.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices() {
        customerService = SpringUtils.getBean(CustomerService.class);
    }

    private void searchCustomers() {
        tblCustomer.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblCustomer.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> customerService.searchCustomersByKeyword(tfSearch.getText()))
                .thenAccept(suppliers -> Platform.runLater(() -> {
                    if (suppliers.isEmpty()) {
                        tblCustomer.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblCustomer.setItems(FXCollections.observableList(suppliers));
                    TableViewUtils.sortAscending(tblCustomer, colName);
                }));
    }

}

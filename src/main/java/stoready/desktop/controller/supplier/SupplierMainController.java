package stoready.desktop.controller.supplier;

import java.time.LocalDateTime;

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
import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.CommonLabel;
import stoready.desktop.constant.MenuCodeConstants;
import stoready.desktop.constant.MessageCode;
import stoready.desktop.constant.Page;
import stoready.desktop.controller.BaseController;
import stoready.desktop.service.SupplierService;
import stoready.desktop.viewmodel.SupplierFilterVM;
import stoready.desktop.viewmodel.SupplierVM;

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
    private TableColumn<SupplierVM, LocalDateTime> colUpdatedAt;

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
            SupplierFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            supplierFilter = result;
            searchSuppliers();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<SupplierVM> items = tableSupplier.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_SUPPLIERS);
            if (result.isConfirmed()) {
                supplierService.removeSuppliers(items.stream().map(SupplierVM::getId).toList());
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_SUPPLIERS);
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
        disableWriteAction(MenuCodeConstants.MASTER_SUPPLIERS, btnAdd, btnRemove);
        TableViewUtils.setColumnValue(colCode, SupplierVM::getCode);
        TableViewUtils.setColumnValue(colName, SupplierVM::getName);
        TableViewUtils.setColumnValue(colPhone, SupplierVM::getPhone);
        TableViewUtils.setColumnValue(colEmail, SupplierVM::getEmail);
        TableViewUtils.setColumnValue(colAddress, SupplierVM::getAddress);
        TableViewUtils.setColumnValue(colWebsite, SupplierVM::getWebsite);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                SupplierVM::getUpdatedAt);
        tableSupplier.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
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
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchSuppliers() {
        tableSupplier.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tableSupplier.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> supplierService.searchSuppliers(supplierFilter))
                .thenAccept(suppliers -> Platform.runLater(() -> {
                    if (suppliers.isEmpty()) {
                        tableSupplier.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tableSupplier.setItems(FXCollections.observableList(suppliers));
                    TableViewUtils.sortDescending(tableSupplier, colUpdatedAt);
                    lblRows.setText(suppliers.size() + "");
                }));
    }

    private void handleActionTableSupplier() {
        if (TableViewUtils.hasItemSelected(tableSupplier)) {
            setPageData(TableViewUtils.getSelectedItem(tableSupplier));
            StageUtils.modal(Page.MASTER_SUPPLIER_EDIT, false, event -> {
                if (getPageData() != null) {
                    searchSuppliers();
                }
            });
        }
    }

}

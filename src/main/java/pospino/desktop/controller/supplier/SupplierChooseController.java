package pospino.desktop.controller.supplier;

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
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.controller.CommonDataChooseController;
import pospino.desktop.service.SupplierService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.SupplierVM;

public class SupplierChooseController extends CommonDataChooseController<SupplierVM> {

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<SupplierVM> tblSupplier;

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

    private SupplierService supplierService;

    @Override
    protected void initDataChooseControlActions() {
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
        tblSupplier.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblSupplier.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblSupplier.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                btnChoose.fire();
            }
        });
        tfSearch.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                searchSuppliers();
            }
        });
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        // Nothing to do
    }

    @Override
    protected SupplierVM getSelectedData() {
        return tblSupplier.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices() {
        supplierService = SpringUtils.getBean(SupplierService.class);
    }

    private void searchSuppliers() {
        tblSupplier.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblSupplier.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> supplierService.searchSuppliersByKeyword(tfSearch.getText()))
                .thenAccept(suppliers -> Platform.runLater(() -> {
                    if (suppliers.isEmpty()) {
                        tblSupplier.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblSupplier.setItems(FXCollections.observableList(suppliers));
                    TableViewUtils.sortAscending(tblSupplier, colName);
                }));
    }

}

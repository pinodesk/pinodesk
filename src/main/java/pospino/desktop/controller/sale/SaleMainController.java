package pospino.desktop.controller.sale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

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
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.SellingMode;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.BaseController;
import pospino.desktop.service.SaleService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.SaleFilterVM;
import pospino.desktop.viewmodel.SaleVM;

public class SaleMainController extends BaseController {

    @FXML
    private Label lblRows;

    @FXML
    private Button btnFilter;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnAdd;

    @FXML
    private TableView<SaleVM> tblSales;

    @FXML
    private TableColumn<SaleVM, String> colInvoiceNumber;

    @FXML
    private TableColumn<SaleVM, LocalDate> colInvoiceDate;

    @FXML
    private TableColumn<SaleVM, String> colSellingMode;

    @FXML
    private TableColumn<SaleVM, String> colCustomerName;

    @FXML
    private TableColumn<SaleVM, String> colDoctorName;

    @FXML
    private TableColumn<SaleVM, Integer> colTotalProduct;

    @FXML
    private TableColumn<SaleVM, BigDecimal> colTotalPayment;

    @FXML
    private TableColumn<SaleVM, String> colPaymentStatus;

    @FXML
    private TableColumn<SaleVM, LocalDate> colDueDate;

    @FXML
    private TableColumn<SaleVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<SaleVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Button btnCashier;

    private SaleService saleService;

    private SaleFilterVM saleFilter;

    @FXML
    void onActionBtnCashier(ActionEvent event) {
        btnCashier.setDisable(true);
        StageUtils.open(Page.TRANSACTION_SALE_CASHIER_MAIN, true, we -> {
            // Remove the last data from the stack and ignore (if not used) to avoid such
            // this issue https://gitlab.com/pospino/pospino-desktop/-/issues/52
            getPageData();
            searchSales();
            btnCashier.setDisable(false);
        });
    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.TRANSACTION_SALE_ADD, true, we -> {
            if (getPageData() != null) {
                searchSales();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(saleFilter);
        StageUtils.modal(Page.TRANSACTION_SALE_FILTER, false, we -> {
            SaleFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            saleFilter = result;
            searchSales();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<SaleVM> items = tblSales.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_SALES);
            if (result.isConfirmed()) {
                saleService.removeSales(items.stream().map(SaleVM::getId).toList());
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_SALES);
                searchSales();
            }
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        saleService = SpringUtils.getBean(SaleService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.TRANSACTION_SALES, btnAdd, btnRemove);
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colInvoiceNumber, SaleVM::getInvoiceNumber);
        TableViewUtils.setColumnValue(colCustomerName, SaleVM::getCustomerName);
        TableViewUtils.setColumnValue(colDueDate, SaleVM::getPaymentDueDate);
        TableViewUtils.setColumnValue(colDoctorName, SaleVM::getDoctorName);
        TableViewUtils.setColumnValue(
                colPaymentStatus,
                vm -> PaymentStatus.PAID.toString().equals(vm.getPaymentStatus()) ?
                        t.translate(CommonLabel.LBL_PAID) : t.translate(CommonLabel.LBL_UNPAID));
        TableViewUtils.setColumnValue(
                colSellingMode,
                vm -> SellingMode.GENERAL.toString().equals(vm.getSellingMode()) ?
                        t.translate(CommonLabel.LBL_GENERAL) : t.translate(CommonLabel.LBL_PRESCRIPTION));
        TableViewUtils.initTableColumn(
                colTotalProduct,
                new NumberCellFactory<>(locale),
                SaleVM::getTotalProduct,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colTotalPayment,
                new NumberCellFactory<>(locale),
                SaleVM::getTotalPayment,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colInvoiceDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                vm -> vm.getCreatedAt().toLocalDate());
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                SaleVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                SaleVM::getUpdatedAt);
        tblSales.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblSales.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblSales.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableSales();
            }
        });
        tblSales.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableSales();
            }
        });
    }

    @Override
    protected void initControlValues() {
        saleFilter = new SaleFilterVM();
        searchSales();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchSales() {
        tblSales.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblSales.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> saleService.searchSales(saleFilter)).thenAccept(sales -> Platform.runLater(() -> {
            if (sales.isEmpty()) {
                tblSales.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                lblRows.setText("0");
            }
            tblSales.setItems(FXCollections.observableList(sales));
            TableViewUtils.sortDescending(tblSales, colUpdatedAt);
            lblRows.setText(sales.size() + "");
        }));
    }

    private void handleActionTableSales() {
        if (TableViewUtils.hasItemSelected(tblSales)) {
            setPageData(TableViewUtils.getSelectedItem(tblSales));
            StageUtils.modal(Page.TRANSACTION_SALE_EDIT, event -> {
                // Remove the last data from the stack and ignore (if not used) to avoid such
                // this issue https://gitlab.com/pospino/pospino-desktop/-/issues/52
                getPageData();
                searchSales();
            });
        }
    }

}

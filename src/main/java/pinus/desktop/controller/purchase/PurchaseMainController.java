package pinus.desktop.controller.purchase;

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
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.constant.Page;
import pinus.desktop.constant.PaymentStatus;
import pinus.desktop.constant.StyleConstants;
import pinus.desktop.controller.BaseController;
import pinus.desktop.service.PurchaseService;
import pinus.desktop.util.SpringUtils;
import pinus.desktop.viewmodel.PurchaseFilterVM;
import pinus.desktop.viewmodel.PurchaseVM;

public class PurchaseMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<PurchaseVM> tblPurchase;

    @FXML
    private TableColumn<PurchaseVM, String> colOrderNumber;

    @FXML
    private TableColumn<PurchaseVM, LocalDate> colOrderDate;

    @FXML
    private TableColumn<PurchaseVM, String> colSupplierName;

    @FXML
    private TableColumn<PurchaseVM, Integer> colTotalProduct;

    @FXML
    private TableColumn<PurchaseVM, BigDecimal> colTotalPayment;

    @FXML
    private TableColumn<PurchaseVM, BigDecimal> colTotalPurchase;

    @FXML
    private TableColumn<PurchaseVM, BigDecimal> colTax;

    @FXML
    private TableColumn<PurchaseVM, BigDecimal> colDiscount;

    @FXML
    private TableColumn<PurchaseVM, String> colPaymentStatus;

    @FXML
    private TableColumn<PurchaseVM, LocalDate> colDueDate;

    @FXML
    private TableColumn<PurchaseVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private PurchaseService purchaseService;
    private PurchaseFilterVM purchaseFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.TRANSACTION_PURCHASE_ADD, true, we -> {
            if (getPageData() != null) {
                searchPurchases();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(purchaseFilter);
        StageUtils.modal(Page.TRANSACTION_PURCHASE_FILTER, false, we -> {
            PurchaseFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            purchaseFilter = result;
            searchPurchases();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<PurchaseVM> items = tblPurchase.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_PURCHASES);
            if (result.isConfirmed()) {
                purchaseService.removePurchases(items.stream().map(PurchaseVM::getId).toList());
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_PURCHASES);
                searchPurchases();
            }
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        purchaseService = SpringUtils.getBean(PurchaseService.class);
    }

    @Override
    protected void initControlActions() {
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colOrderNumber, PurchaseVM::getInvoiceNumber);
        TableViewUtils.setColumnValue(colOrderDate, PurchaseVM::getInvoiceDate);
        TableViewUtils.setColumnValue(colDueDate, PurchaseVM::getPaymentDueDate);
        TableViewUtils.setColumnValue(colSupplierName, PurchaseVM::getSupplierName);
        TableViewUtils.setColumnValue(colPaymentStatus, vm -> {
            PaymentStatus ps = PaymentStatus.valueOf(vm.getPaymentStatus());
            return PaymentStatus.PAID.equals(ps) ?
                    translator.translate(CommonLabel.LBL_PAID) : translator.translate(CommonLabel.LBL_UNPAID);
        });
        TableViewUtils.initTableColumn(
                colTotalProduct,
                new NumberCellFactory<>(locale),
                PurchaseVM::getTotalProduct,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colTotalPayment,
                new NumberCellFactory<>(locale),
                PurchaseVM::getTotalPayment,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colTax,
                new NumberCellFactory<>(locale),
                PurchaseVM::getTax,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colDiscount,
                new NumberCellFactory<>(locale),
                PurchaseVM::getDiscount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colTotalPurchase,
                new NumberCellFactory<>(locale),
                PurchaseVM::getTotalPurchase,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colOrderDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PurchaseVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                PurchaseVM::getUpdatedAt);
        tblPurchase.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_NO_DATA)));
        tblPurchase.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblPurchase.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTablePurchase();
            }
        });
        tblPurchase.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTablePurchase();
            }
        });
    }

    @Override
    protected void initControlValues() {
        purchaseFilter = new PurchaseFilterVM();
        searchPurchases();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchPurchases() {
        tblPurchase.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_LOADING_DATA)));
        tblPurchase.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> purchaseService.searchPurchases(purchaseFilter))
                .thenAccept(purchases -> Platform.runLater(() -> {
                    if (purchases.isEmpty()) {
                        tblPurchase.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblPurchase.setItems(FXCollections.observableList(purchases));
                    TableViewUtils.sortDescending(tblPurchase, colUpdatedAt);
                    lblRows.setText(purchases.size() + "");
                }));
    }

    private void handleActionTablePurchase() {
        if (TableViewUtils.hasItemSelected(tblPurchase)) {
            setPageData(TableViewUtils.getSelectedItem(tblPurchase));
            StageUtils.modal(Page.TRANSACTION_PURCHASE_EDIT, event -> {
                searchPurchases();
            });
        }
    }

}

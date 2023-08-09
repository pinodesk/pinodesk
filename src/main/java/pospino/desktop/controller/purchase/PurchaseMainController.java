package pospino.desktop.controller.purchase;

import static pospino.desktop.constant.CommonConstants.DECIMAL_SCALE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import com.gitlab.mudiasoft.pandora.factory.LocalDateCellFactory;
import com.gitlab.mudiasoft.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.toolbox.data.StringNumberUtils;
import com.gitlab.mudiasoft.toolbox.future.AsyncUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.BaseController;
import pospino.desktop.service.PurchaseService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.PurchaseFilterVM;
import pospino.desktop.viewmodel.PurchaseVM;

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
    private TableColumn<PurchaseVM, BigDecimal> colTotalPrice;

    @FXML
    private TableColumn<PurchaseVM, BigDecimal> colTax;

    @FXML
    private TableColumn<PurchaseVM, BigDecimal> colTotalDiscount;

    @FXML
    private TableColumn<PurchaseVM, String> colPaymentStatus;

    @FXML
    private TableColumn<PurchaseVM, LocalDate> colDueDate;

    @FXML
    private TableColumn<PurchaseVM, String> colUser;

    @FXML
    private TableColumn<PurchaseVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<PurchaseVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    @FXML
    private Label lblPeriod;

    @FXML
    private Label lblExpense;

    @FXML
    private Label lblPurchaseCount;

    @FXML
    private VBox vboxSummary;

    @FXML
    private Button btnToggleSummary;

    @FXML
    private FontAwesomeIconView faBtnSummary;

    private PurchaseService purchaseService;
    private PurchaseFilterVM purchaseFilter;

    @FXML
    void onActionBtnToggleSummary(ActionEvent event) {
        boolean visible = vboxSummary.isVisible();
        setVisibleInLayout(!visible, vboxSummary);
        faBtnSummary.setGlyphName(visible ? FontAwesomeIcon.ANGLE_LEFT.name() : FontAwesomeIcon.ANGLE_RIGHT.name());
    }

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
    protected void initServices() {
        purchaseService = SpringUtils.getBean(PurchaseService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.TRANSACTION_PURCHASES, btnAdd, btnRemove);
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colOrderNumber, PurchaseVM::getInvoiceNumber);
        TableViewUtils.setColumnValue(colOrderDate, PurchaseVM::getInvoiceDate);
        TableViewUtils.setColumnValue(colDueDate, PurchaseVM::getPaymentDueDate);
        TableViewUtils.setColumnValue(colSupplierName, PurchaseVM::getSupplierName);
        TableViewUtils.setColumnValue(colUser, PurchaseVM::getUserFullName);
        TableViewUtils.setColumnValue(
                colPaymentStatus,
                vm -> PaymentStatus.PAID.toString().equals(vm.getPaymentStatus()) ?
                        t.translate(CommonLabel.LBL_PAID) : t.translate(CommonLabel.LBL_UNPAID));
        TableViewUtils.initTableColumn(
                colTotalProduct,
                new NumberCellFactory<>(locale),
                PurchaseVM::getTotalProduct,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colTotalPayment,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                PurchaseVM::getTotalPayment,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colTax,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                PurchaseVM::getTax,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colTotalDiscount,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                PurchaseVM::getTotalDiscount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colTotalPrice,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                PurchaseVM::getTotalPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colOrderDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PurchaseVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                PurchaseVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                PurchaseVM::getUpdatedAt);
        tblPurchase.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
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
        purchaseFilter = getPageData();
        if (purchaseFilter == null) {
            purchaseFilter = new PurchaseFilterVM();
        }
        searchPurchases();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchPurchases() {
        tblPurchase.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblPurchase.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> purchaseService.searchPurchases(purchaseFilter))
                .thenAccept(purchases -> Platform.runLater(() -> {
                    if (purchases.isEmpty()) {
                        tblPurchase.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblPurchase.setItems(FXCollections.observableList(purchases));
                    TableViewUtils.sortDescending(tblPurchase, colUpdatedAt);
                    lblRows.setText(StringNumberUtils.format(purchases.size(), resources.getLocale()));
                }));
    }

    private void handleActionTablePurchase() {
        if (TableViewUtils.hasItemSelected(tblPurchase)) {
            setPageData(TableViewUtils.getSelectedItem(tblPurchase));
            StageUtils.modal(Page.TRANSACTION_PURCHASE_EDIT, event -> {
                // Remove the last data from the stack and ignore (if not used) to avoid such
                // this issue https://gitlab.com/pospino/pospino-desktop/-/issues/52
                getPageData();
                searchPurchases();
            });
        }
    }

}

package toscabox.desktop.controller.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Collectors;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
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
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import toscabox.desktop.constant.CommonConstants;
import toscabox.desktop.constant.ConfigurationConstants;
import toscabox.desktop.constant.MessageCode;
import toscabox.desktop.constant.Page;
import toscabox.desktop.constant.PaymentMethod;
import toscabox.desktop.constant.PaymentPeriodUnit;
import toscabox.desktop.constant.PaymentStatus;
import toscabox.desktop.constant.StyleConstants;
import toscabox.desktop.controller.BaseController;
import toscabox.desktop.service.ConfigurationService;
import toscabox.desktop.service.PurchaseService;
import toscabox.desktop.utility.SpringUtils;
import toscabox.desktop.viewmodel.PurchaseFilterVM;
import toscabox.desktop.viewmodel.PurchaseVM;

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
    private TableColumn<PurchaseVM, String> colPaymentMethod;

    @FXML
    private TableColumn<PurchaseVM, String> colPaymentPeriod;

    @FXML
    private TableColumn<PurchaseVM, String> colPaymentStatus;

    @FXML
    private TableColumn<PurchaseVM, LocalDate> colDueDate;

    @FXML
    private TableColumn<PurchaseVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<PurchaseVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private PurchaseService purchaseService;
    private ConfigurationService configurationService;
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
            purchaseFilter = getPageData();
            searchPurchases();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<PurchaseVM> items = tblPurchase.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_PURCHASES);
            if (result.isConfirmed()) {
                purchaseService.removePurchases(items.stream().map(PurchaseVM::getId).collect(Collectors.toList()));
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_PURCHASES);
                searchPurchases();
            }
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        purchaseService = SpringUtils.getBean(PurchaseService.class);
        configurationService = SpringUtils.getBean(ConfigurationService.class);
    }

    @Override
    protected void initControlActions() {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        Locale locale = new Locale(languageCode);
        TableViewUtils.setColumnValue(colOrderNumber, PurchaseVM::getOrderNumber);
        TableViewUtils.setColumnValue(colOrderDate, PurchaseVM::getOrderDate);
        TableViewUtils.setColumnValue(colDueDate, PurchaseVM::getPaymentDueDate);
        TableViewUtils.setColumnValue(colSupplierName, PurchaseVM::getSupplierName);
        TableViewUtils.setColumnValue(colPaymentMethod, vm -> {
            PaymentMethod pm = PaymentMethod.valueOf(vm.getPaymentMethod());
            return PaymentMethod.CASH.equals(pm) ? translate("lbl.cash") : translate("lbl.credit");
        });
        TableViewUtils.setColumnValue(colPaymentStatus, vm -> {
            PaymentStatus ps = PaymentStatus.valueOf(vm.getPaymentStatus());
            return PaymentStatus.PAID.equals(ps) ? translate("lbl.paid") : translate("lbl.unpaid");
        });
        TableViewUtils.setColumnValue(colPaymentPeriod, vm -> {
            PaymentMethod pm = PaymentMethod.valueOf(vm.getPaymentMethod());
            if (PaymentMethod.CASH.equals(pm)) {
                return null;
            }
            String periodUnit = PaymentPeriodUnit.valueOf(vm.getPaymentPeriodUnit()).name().toLowerCase();
            return vm.getPaymentPeriodCount() + " " + translate("lbl." + periodUnit);
        });
        TableViewUtils.initTableColumn(colTotalProduct, new NumberCellFactory<>(locale), PurchaseVM::getTotalProduct,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colTotalPayment, new NumberCellFactory<>(locale), PurchaseVM::getTotalPayment,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colOrderDate, new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PurchaseVM::getOrderDate);
        TableViewUtils.initTableColumn(colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN), PurchaseVM::getCreatedAt);
        TableViewUtils.initTableColumn(colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN), PurchaseVM::getUpdatedAt);
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

    @SuppressWarnings("unchecked")
    private void searchPurchases() {
        tblPurchase.setPlaceholder(new Label(translate("lbl.loadingdata")));
        tblPurchase.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> purchaseService.searchPurchases(purchaseFilter))
                .thenAccept(purchases -> Platform.runLater(() -> {
                    if (purchases.isEmpty()) {
                        tblPurchase.setPlaceholder(new Label(translate("lbl.nodata")));
                        lblRows.setText("0");
                    }
                    tblPurchase.setItems(FXCollections.observableList(purchases));
                    colCreatedAt.setSortType(SortType.DESCENDING);
                    tblPurchase.getSortOrder().setAll(colCreatedAt);
                    lblRows.setText(purchases.size() + "");
                }));
    }

    private void handleActionTablePurchase() {
        if (TableViewUtils.hasItemSelected(tblPurchase)) {
            setPageData(TableViewUtils.getSelectedItem(tblPurchase));
            StageUtils.modal(Page.TRANSACTION_PURCHASE_EDIT, false, event -> {
                if (Boolean.TRUE.equals(getPageData())) {
                    searchPurchases();
                }
            });
        }
    }

}

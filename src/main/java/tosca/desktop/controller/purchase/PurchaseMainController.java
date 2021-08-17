package tosca.desktop.controller.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import tosca.desktop.constant.CommonConstants;
import tosca.desktop.constant.ConfigurationConstants;
import tosca.desktop.constant.Page;
import tosca.desktop.constant.StyleConstants;
import tosca.desktop.controller.BaseController;
import tosca.desktop.service.ConfigurationService;
import tosca.desktop.service.PurchaseService;
import tosca.desktop.utility.SpringUtils;
import tosca.desktop.viewmodel.PurchaseFilterVM;
import tosca.desktop.viewmodel.PurchaseVM;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

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
        StageUtils.modal(Page.TRANSACTION_PURCHASE_ADD, true);
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        // not yet
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        // not yet
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
        TableViewUtils.setColumnValue(colSupplierName, vm -> vm.getSupplierId().toString());
        TableViewUtils.setColumnValue(colPaymentMethod, PurchaseVM::getPaymentMethod);
        TableViewUtils.setColumnValue(colPaymentPeriod,
                vm -> vm.getPaymentPeriodCount() + " " + vm.getPaymentPeriodUnit());
        TableViewUtils.initTableColumn(colTotalProduct, new NumberCellFactory<>(locale), PurchaseVM::getTotalProduct,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colTotalPayment, new NumberCellFactory<>(locale), PurchaseVM::getTotalPayment,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colOrderDate, new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PurchaseVM::getOrderDate);
        TableViewUtils.initTableColumn(colCreatedAt, new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                PurchaseVM::getCreatedAt);
        TableViewUtils.initTableColumn(colUpdatedAt, new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                PurchaseVM::getUpdatedAt);
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
                    tblPurchase.getSortOrder().setAll(colOrderNumber); // Always sort by name after searching
                    lblRows.setText(purchases.size() + "");
                }));
    }

    private void handleActionTablePurchase() {
        PurchaseVM selected = tblPurchase.getSelectionModel().getSelectedItem();
        setPageData(selected);
        StageUtils.modal(Page.TRANSACTION_PURCHASE_EDIT, false, event -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchPurchases();
            }
        });
    }

}

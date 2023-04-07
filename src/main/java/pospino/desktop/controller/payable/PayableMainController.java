package pospino.desktop.controller.payable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import com.gitlab.mudiasoft.pandora.factory.LocalDateCellFactory;
import com.gitlab.mudiasoft.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.toolbox.data.StringNumberUtils;
import com.gitlab.mudiasoft.toolbox.future.AsyncUtils;

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
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.BaseController;
import pospino.desktop.service.PayableService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.PayableFilterVM;
import pospino.desktop.viewmodel.PayableVM;

public class PayableMainController extends BaseController {

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<PayableVM> tblPayables;

    @FXML
    private TableColumn<PayableVM, String> colSupplierName;

    @FXML
    private TableColumn<PayableVM, String> colInvoiceNumber;

    @FXML
    private TableColumn<PayableVM, LocalDate> colInvoiceDate;

    @FXML
    private TableColumn<PayableVM, BigDecimal> colAmount;

    @FXML
    private TableColumn<PayableVM, LocalDate> colDueDate;

    @FXML
    private TableColumn<PayableVM, LocalDate> colCompletionDate;

    @FXML
    private TableColumn<PayableVM, String> colRemarks;

    @FXML
    private TableColumn<PayableVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<PayableVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private PayableService payableService;
    private PayableFilterVM payableFilter;

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(payableFilter);
        StageUtils.modal(Page.TRANSACTION_PAYABLE_FILTER, false, we -> {
            PayableFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            payableFilter = result;
            searchPayables();
        });
    }

    @Override
    protected void initServices() {
        payableService = SpringUtils.getBean(PayableService.class);
    }

    @Override
    protected void initControlActions() {
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colSupplierName, PayableVM::getSupplierName);
        TableViewUtils.setColumnValue(colInvoiceNumber, PayableVM::getInvoiceNumber);
        TableViewUtils.setColumnValue(colRemarks, PayableVM::getRemarks);
        TableViewUtils.initTableColumn(
                colAmount,
                new NumberCellFactory<>(locale),
                PayableVM::getAmount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colInvoiceDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PayableVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colCompletionDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PayableVM::getCompletionDate);
        TableViewUtils.initTableColumn(
                colDueDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PayableVM::getDueDate);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                PayableVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                PayableVM::getUpdatedAt);
        tblPayables.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblPayables.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblPayables.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTablePayables();
            }
        });
        tblPayables.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTablePayables();
            }
        });
    }

    @Override
    protected void initControlValues() {
        payableFilter = new PayableFilterVM();
        searchPayables();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchPayables() {
        tblPayables.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblPayables.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> payableService.searchPayables(payableFilter))
                .thenAccept(payables -> Platform.runLater(() -> {
                    if (payables.isEmpty()) {
                        tblPayables.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblPayables.setItems(FXCollections.observableList(payables));
                    TableViewUtils.sortDescending(tblPayables, colUpdatedAt);
                    lblRows.setText(StringNumberUtils.format(payables.size(), resources.getLocale()));
                }));
    }

    private void handleActionTablePayables() {
        if (TableViewUtils.hasItemSelected(tblPayables)) {
            setPageData(TableViewUtils.getSelectedItem(tblPayables));
            StageUtils.modal(Page.TRANSACTION_PAYABLE_EDIT, false, event -> {
                // Remove the last data from the stack and ignore (if not used) to avoid such
                // this issue https://gitlab.com/pospino/pospino-desktop/-/issues/52
                getPageData();
                searchPayables();
            });
        }
    }

}

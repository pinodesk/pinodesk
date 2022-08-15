package pinus.desktop.controller.payable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

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
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.Page;
import pinus.desktop.constant.StyleConstants;
import pinus.desktop.controller.BaseController;
import pinus.desktop.service.PayableService;
import pinus.desktop.util.SpringUtils;
import pinus.desktop.viewmodel.PayableFilterVM;
import pinus.desktop.viewmodel.PayableVM;

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
    protected void initServices(ApplicationContext ctx) {
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
                PayableVM::getPaymentDueDate);
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
                    lblRows.setText(payables.size() + "");
                }));
    }

    private void handleActionTablePayables() {
        // TODO
    }

}

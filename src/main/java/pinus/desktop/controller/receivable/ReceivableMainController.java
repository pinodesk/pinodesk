package pinus.desktop.controller.receivable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
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
import pinus.desktop.constant.StyleConstants;
import pinus.desktop.controller.BaseController;
import pinus.desktop.service.ReceivableService;
import pinus.desktop.util.SpringUtils;
import pinus.desktop.viewmodel.ReceivableVM;

public class ReceivableMainController extends BaseController {

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<ReceivableVM> tblReceivables;

    @FXML
    private TableColumn<ReceivableVM, String> colCustomerName;

    @FXML
    private TableColumn<ReceivableVM, String> colInvoiceNumber;

    @FXML
    private TableColumn<ReceivableVM, LocalDate> colInvoiceDate;

    @FXML
    private TableColumn<ReceivableVM, BigDecimal> colPaymentAmount;

    @FXML
    private TableColumn<ReceivableVM, LocalDate> colDueDate;

    @FXML
    private TableColumn<ReceivableVM, LocalDate> colPaymentDate;

    @FXML
    private TableColumn<ReceivableVM, String> colRemarks;

    @FXML
    private TableColumn<ReceivableVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<ReceivableVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private ReceivableService receivableService;

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        // TODO
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        receivableService = SpringUtils.getBean(ReceivableService.class);
    }

    @Override
    protected void initControlActions() {
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colCustomerName, ReceivableVM::getCustomerName);
        TableViewUtils.setColumnValue(colInvoiceNumber, ReceivableVM::getInvoiceNumber);
        TableViewUtils.setColumnValue(colRemarks, ReceivableVM::getRemarks);
        TableViewUtils.initTableColumn(
                colPaymentAmount,
                new NumberCellFactory<>(locale),
                ReceivableVM::getPaymentAmount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colInvoiceDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivableVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colPaymentDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivableVM::getPaymentDate);
        TableViewUtils.initTableColumn(
                colDueDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivableVM::getPaymentDueDate);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ReceivableVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ReceivableVM::getUpdatedAt);
        tblReceivables.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_NO_DATA)));
        tblReceivables.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblReceivables.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTablePayables();
            }
        });
        tblReceivables.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTablePayables();
            }
        });
    }

    @Override
    protected void initControlValues() {
        searchPayables();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchPayables() {
        tblReceivables.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_LOADING_DATA)));
        tblReceivables.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> receivableService.searchReceivables()).thenAccept(payables -> Platform.runLater(() -> {
            if (payables.isEmpty()) {
                tblReceivables.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_NO_DATA)));
                lblRows.setText("0");
            }
            tblReceivables.setItems(FXCollections.observableList(payables));
            TableViewUtils.sortDescending(tblReceivables, colUpdatedAt);
            lblRows.setText(payables.size() + "");
        }));
    }

    private void handleActionTablePayables() {
        // TODO
    }

}

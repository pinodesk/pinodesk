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
import pinus.desktop.service.ReceivableService;
import pinus.desktop.util.SpringUtils;
import pinus.desktop.viewmodel.ReceivableFilterVM;
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
    private TableColumn<ReceivableVM, BigDecimal> colAmount;

    @FXML
    private TableColumn<ReceivableVM, LocalDate> colDueDate;

    @FXML
    private TableColumn<ReceivableVM, LocalDate> colCompletionDate;

    @FXML
    private TableColumn<ReceivableVM, String> colRemarks;

    @FXML
    private TableColumn<ReceivableVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<ReceivableVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private ReceivableService receivableService;
    private ReceivableFilterVM receivableFilter;

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(receivableFilter);
        StageUtils.modal(Page.TRANSACTION_RECEIVABLE_FILTER, false, we -> {
            ReceivableFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            receivableFilter = result;
            searchReceivables();
        });
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
                colAmount,
                new NumberCellFactory<>(locale),
                ReceivableVM::getAmount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colInvoiceDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivableVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colCompletionDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivableVM::getCompletionDate);
        TableViewUtils.initTableColumn(
                colDueDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivableVM::getDueDate);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ReceivableVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ReceivableVM::getUpdatedAt);
        tblReceivables.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblReceivables.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblReceivables.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableReceivables();
            }
        });
        tblReceivables.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableReceivables();
            }
        });
    }

    @Override
    protected void initControlValues() {
        receivableFilter = new ReceivableFilterVM();
        searchReceivables();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchReceivables() {
        tblReceivables.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblReceivables.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> receivableService.searchReceivables(receivableFilter))
                .thenAccept(payables -> Platform.runLater(() -> {
                    if (payables.isEmpty()) {
                        tblReceivables.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblReceivables.setItems(FXCollections.observableList(payables));
                    TableViewUtils.sortDescending(tblReceivables, colUpdatedAt);
                    lblRows.setText(payables.size() + "");
                }));
    }

    private void handleActionTableReceivables() {
        // TODO
    }

}

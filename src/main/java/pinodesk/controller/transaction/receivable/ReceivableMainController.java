package pinodesk.controller.transaction.receivable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import com.pinodesk.pandora.factory.LocalDateCellFactory;
import com.pinodesk.pandora.factory.LocalDateTimeCellFactory;
import com.pinodesk.pandora.factory.NumberCellFactory;
import com.pinodesk.pandora.utility.EventUtils;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.pandora.utility.TableViewUtils;
import com.pinodesk.toolbox.data.StringNumberUtils;
import com.pinodesk.toolbox.future.AsyncUtils;
import static pinodesk.constant.CommonConstants.DECIMAL_SCALE;

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
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.Page;
import pinodesk.constant.StyleConstants;
import pinodesk.controller.BaseController;
import pinodesk.service.ReceivableService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.ReceivableFilterVM;
import pinodesk.viewmodel.ReceivableVM;

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
    protected void initServices() {
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
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
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
                .thenAccept(receivables -> Platform.runLater(() -> {
                    if (receivables.isEmpty()) {
                        tblReceivables.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblReceivables.setItems(FXCollections.observableList(receivables));
                    TableViewUtils.sortDescending(tblReceivables, colUpdatedAt);
                    lblRows.setText(StringNumberUtils.format(receivables.size(), resources.getLocale()));
                }));
    }

    private void handleActionTableReceivables() {
        if (TableViewUtils.hasItemSelected(tblReceivables)) {
            setPageData(TableViewUtils.getSelectedItem(tblReceivables));
            StageUtils.modal(Page.TRANSACTION_RECEIVABLE_EDIT, false, event -> {
                // Remove the last data from the stack and ignore (if not used) to avoid such
                // this issue https://gitlab.com/pinodesk/pinodesk/-/issues/52
                getPageData();
                searchReceivables();
            });
        }
    }

}

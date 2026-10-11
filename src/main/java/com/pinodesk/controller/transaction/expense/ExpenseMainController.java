package com.pinodesk.controller.transaction.expense;

import static com.pinodesk.constant.CommonConstants.DECIMAL_SCALE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.Page;
import com.pinodesk.constant.StyleConstants;
import com.pinodesk.controller.BaseController;
import com.pinodesk.pandora.factory.LocalDateCellFactory;
import com.pinodesk.pandora.factory.LocalDateTimeCellFactory;
import com.pinodesk.pandora.factory.NumberCellFactory;
import com.pinodesk.pandora.utility.AlertResult;
import com.pinodesk.pandora.utility.EventUtils;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.pandora.utility.TableViewUtils;
import com.pinodesk.service.ExpenseService;
import com.pinodesk.toolbox.data.StringNumberUtils;
import com.pinodesk.toolbox.future.AsyncUtils;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ExpenseFilterVM;
import com.pinodesk.viewmodel.ExpenseVM;

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

public class ExpenseMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<ExpenseVM> tblExpense;

    @FXML
    private TableColumn<ExpenseVM, String> colTitle;

    @FXML
    private TableColumn<ExpenseVM, String> colExpenseCategory;

    @FXML
    private TableColumn<ExpenseVM, BigDecimal> colNominal;

    @FXML
    private TableColumn<ExpenseVM, LocalDate> colExpenseDate;

    @FXML
    private TableColumn<ExpenseVM, String> colPaymentMethod;

    @FXML
    private TableColumn<ExpenseVM, String> colNotes;

    @FXML
    private TableColumn<ExpenseVM, String> colUser;

    @FXML
    private TableColumn<ExpenseVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<ExpenseVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private ExpenseService expenseService;
    private ExpenseFilterVM expenseFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.TRANSACTION_EXPENSE_ADD, true, we -> {
            if (getPageData() != null) {
                searchExpenses();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(expenseFilter);
        StageUtils.modal(Page.TRANSACTION_EXPENSE_FILTER, false, we -> {
            ExpenseFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            expenseFilter = result;
            searchExpenses();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<ExpenseVM> items = tblExpense.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_EXPENSES);
            if (result.isConfirmed()) {
                expenseService.removeExpenses(items.stream().map(ExpenseVM::getId).toList());
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_EXPENSES);
                searchExpenses();
            }
        }
    }

    @Override
    protected void initServices() {
        expenseService = SpringUtils.getBean(ExpenseService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.TRANSACTION_EXPENSES, btnAdd, btnRemove);
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colTitle, ExpenseVM::getTitle);
        TableViewUtils.setColumnValue(colExpenseCategory, ExpenseVM::getExpenseCategoryName);
        TableViewUtils.setColumnValue(colPaymentMethod, ExpenseVM::getPaymentMethodName);
        TableViewUtils.setColumnValue(colUser, ExpenseVM::getUserFullName);
        TableViewUtils.setColumnValue(colNotes, ExpenseVM::getNotes);
        TableViewUtils.initTableColumn(
                colNominal,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                ExpenseVM::getNominal,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colExpenseDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ExpenseVM::getExpenseDate);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ExpenseVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ExpenseVM::getUpdatedAt);
        tblExpense.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblExpense.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblExpense.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableExpense();
            }
        });
        tblExpense.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableExpense();
            }
        });
    }

    @Override
    protected void initControlValues() {
        expenseFilter = getPageData();
        if (expenseFilter == null) {
            expenseFilter = new ExpenseFilterVM();
        }
        searchExpenses();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchExpenses() {
        tblExpense.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblExpense.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> expenseService.searchExpenses(expenseFilter))
                .thenAccept(expenses -> Platform.runLater(() -> {
                    if (expenses.isEmpty()) {
                        tblExpense.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblExpense.setItems(FXCollections.observableList(expenses));
                    TableViewUtils.sortDescending(tblExpense, colUpdatedAt);
                    lblRows.setText(StringNumberUtils.format(expenses.size(), resources.getLocale()));
                }));
    }

    private void handleActionTableExpense() {
        if (TableViewUtils.hasItemSelected(tblExpense)) {
            setPageData(TableViewUtils.getSelectedItem(tblExpense));
            StageUtils.modal(Page.TRANSACTION_EXPENSE_EDIT, event -> {
                getPageData();
                searchExpenses();
            });
        }
    }

}

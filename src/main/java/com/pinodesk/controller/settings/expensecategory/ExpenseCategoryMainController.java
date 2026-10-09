package com.pinodesk.controller.settings.expensecategory;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.Page;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.controller.BaseController;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.pandora.factory.LocalDateTimeCellFactory;
import com.pinodesk.pandora.utility.AlertResult;
import com.pinodesk.pandora.utility.EventUtils;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.pandora.utility.TableViewUtils;
import com.pinodesk.service.ExpenseCategoryService;
import com.pinodesk.toolbox.data.StringNumberUtils;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ExpenseCategoryFilterVM;
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

public class ExpenseCategoryMainController extends BaseController {
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnRemove;
    @FXML
    private Button btnFilter;
    @FXML
    private TableView<ExpenseCategory> tblCategories;
    @FXML
    private TableColumn<ExpenseCategory, String> colName;
    @FXML
    private TableColumn<ExpenseCategory, String> colStatus;
    @FXML
    private TableColumn<ExpenseCategory, LocalDateTime> colCreatedAt;
    @FXML
    private TableColumn<ExpenseCategory, LocalDateTime> colUpdatedAt;
    @FXML
    private Label lblRows;
    private ExpenseCategoryService service;
    private ExpenseCategoryFilterVM filter;
    private long searchRequest;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.SETTINGS_EXPENSE_CATEGORY_ADD, false, e -> {
            if (getPageData() != null)
                searchCategories();
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(filter);
        StageUtils.modal(Page.SETTINGS_EXPENSE_CATEGORY_FILTER, false, e -> {
            ExpenseCategoryFilterVM result = getPageData();
            if (result != null) {
                filter = result;
                searchCategories();
            }
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<ExpenseCategory> selected = tblCategories.getSelectionModel().getSelectedItems();
        if (!selected.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_EXPENSE_CATEGORIES);
            if (result.isConfirmed()) {
                service.remove(selected.stream().map(ExpenseCategory::getId).toList());
                searchCategories();
            }
        }
    }

    @Override
    protected void initServices() {
        service = SpringUtils.getBean(ExpenseCategoryService.class);
    }

    @Override
    protected void initControlActions() {
        btnAdd.setDisable(!com.pinodesk.util.ExpenseCategoryControls.canWrite(sessionService));
        btnRemove.setDisable(btnAdd.isDisabled());
        TableViewUtils.setColumnValue(colName, ExpenseCategory::getName);
        TableViewUtils.setColumnValue(
                colStatus,
                c -> resources
                        .getString(UserStatus.ACTIVE.toString().equals(c.getStatus()) ? "lbl_active" : "lbl_inactive"));
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ExpenseCategory::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ExpenseCategory::getUpdatedAt);
        tblCategories.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblCategories.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblCategories.setOnMouseClicked(e -> {
            if (EventUtils.isDoubleClick(e))
                handleEdit();
        });
        tblCategories.setOnKeyPressed(e -> {
            if (EventUtils.isEnter(e))
                handleEdit();
        });
    }

    @Override
    protected void initControlValues() {
        filter = new ExpenseCategoryFilterVM();
        searchCategories();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchCategories() {
        tblCategories.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        String name = filter.getName() == null ? "" : filter.getName().trim().toLowerCase(Locale.ROOT);
        UserStatus status = filter.getStatus();
        long request = ++searchRequest;
        CompletableFuture.supplyAsync(
                () -> service.findAll().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(name))
                        .filter(c -> status == null || status.toString().equals(c.getStatus())).toList())
                .whenComplete((items, error) -> Platform.runLater(() -> {
                    if (request != searchRequest)
                        return;
                    tblCategories.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    lblRows.setText("0");
                    if (error != null) {
                        handleException(error);
                        return;
                    }
                    tblCategories.setItems(FXCollections.observableArrayList(items));
                    lblRows.setText(StringNumberUtils.format(items.size(), resources.getLocale()));
                }));
    }

    private void handleEdit() {
        if (TableViewUtils.hasItemSelected(tblCategories)) {
            setPageData(TableViewUtils.getSelectedItem(tblCategories));
            StageUtils.modal(Page.SETTINGS_EXPENSE_CATEGORY_EDIT, false, e -> {
                if (getPageData() != null)
                    searchCategories();
            });
        }
    }
}

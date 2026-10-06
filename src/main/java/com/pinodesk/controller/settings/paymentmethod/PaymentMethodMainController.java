package com.pinodesk.controller.settings.paymentmethod;

import java.util.concurrent.CompletableFuture;
import java.util.Locale;
import com.pinodesk.util.PaymentMethodControls;

import java.time.LocalDateTime;

import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.Page;
import com.pinodesk.controller.BaseController;
import com.pinodesk.pandora.factory.LocalDateTimeCellFactory;
import com.pinodesk.pandora.utility.AlertResult;
import com.pinodesk.pandora.utility.EventUtils;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.pandora.utility.TableViewUtils;
import com.pinodesk.service.PaymentMethodService;
import com.pinodesk.toolbox.data.StringNumberUtils;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.PaymentMethodFilterVM;
import com.pinodesk.entity.PaymentMethod;
import com.pinodesk.constant.UserStatus;

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

public class PaymentMethodMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<PaymentMethod> tblMethods;

    @FXML
    private TableColumn<PaymentMethod, String> colName;

    @FXML
    private TableColumn<PaymentMethod, String> colCategory;

    @FXML
    private TableColumn<PaymentMethod, String> colStatus;

    @FXML
    private TableColumn<PaymentMethod, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<PaymentMethod, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private PaymentMethodService service;

    private PaymentMethodFilterVM filter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.SETTINGS_PAYMENT_METHOD_ADD, false, we -> {
            if (getPageData() != null) {
                searchMethods();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(filter);
        StageUtils.modal(Page.SETTINGS_PAYMENT_METHOD_FILTER, false, we -> {
            PaymentMethodFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            filter = result;
            searchMethods();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<PaymentMethod> items = tblMethods.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_PAYMENT_METHODS);
            if (result.isConfirmed()) {
                service.remove(items.stream().map(PaymentMethod::getId).toList());
                searchMethods();
            }
        }
    }

    @Override
    protected void initServices() {
        service = SpringUtils.getBean(PaymentMethodService.class);
    }

    @Override
    protected void initControlActions() {
        btnAdd.setDisable(!PaymentMethodControls.canWrite(sessionService));
        btnRemove.setDisable(btnAdd.isDisabled());
        TableViewUtils.setColumnValue(colName, PaymentMethod::getName);
        TableViewUtils.setColumnValue(
                colCategory,
                m -> resources.getString("payment_category_" + m.getCategory().toLowerCase(Locale.ROOT)));
        TableViewUtils.setColumnValue(
                colStatus,
                m -> resources
                        .getString(UserStatus.ACTIVE.toString().equals(m.getStatus()) ? "lbl_active" : "lbl_inactive"));

        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                PaymentMethod::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                PaymentMethod::getUpdatedAt);
        tblMethods.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblMethods.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblMethods.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleEditMethod();
            }
        });
        tblMethods.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleEditMethod();
            }
        });
    }

    @Override
    protected void initControlValues() {
        filter = new PaymentMethodFilterVM();
        searchMethods();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchMethods() {
        tblMethods.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblMethods.setItems(FXCollections.observableArrayList());
        String name = filter.getName() == null ? "" : filter.getName().trim().toLowerCase(Locale.ROOT);
        String category = filter.getCategory();
        UserStatus status = filter.getStatus();
        long request = ++searchRequest;
        CompletableFuture.supplyAsync(
                () -> service.findAll().stream().filter(m -> m.getName().toLowerCase(Locale.ROOT).contains(name))
                        .filter(m -> category == null || category.equals(m.getCategory()))
                        .filter(m -> status == null || status.toString().equals(m.getStatus())).toList())
                .whenComplete((methods, error) -> Platform.runLater(() -> {
                    if (request != searchRequest)
                        return;
                    tblMethods.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    lblRows.setText("0");
                    if (error != null) {
                        handleException(error);
                        return;
                    }
                    tblMethods.setItems(FXCollections.observableArrayList(methods));
                    lblRows.setText(StringNumberUtils.format(methods.size(), resources.getLocale()));
                }));
    }

    private long searchRequest;

    private void handleEditMethod() {
        if (TableViewUtils.hasItemSelected(tblMethods)) {
            setPageData(TableViewUtils.getSelectedItem(tblMethods));
            StageUtils.modal(Page.SETTINGS_PAYMENT_METHOD_EDIT, false, event -> {
                if (getPageData() != null) {
                    searchMethods();
                }
            });
        }
    }

}

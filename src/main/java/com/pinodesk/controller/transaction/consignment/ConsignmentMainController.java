package com.pinodesk.controller.transaction.consignment;

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
import com.pinodesk.service.ConsignmentService;
import com.pinodesk.toolbox.data.StringNumberUtils;
import com.pinodesk.toolbox.future.AsyncUtils;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ConsignmentFilterVM;
import com.pinodesk.viewmodel.ConsignmentVM;

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

public class ConsignmentMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<ConsignmentVM> tblConsignment;

    @FXML
    private TableColumn<ConsignmentVM, String> colInvoiceNumber;

    @FXML
    private TableColumn<ConsignmentVM, LocalDate> colInvoiceDate;

    @FXML
    private TableColumn<ConsignmentVM, String> colSupplierName;

    @FXML
    private TableColumn<ConsignmentVM, String> colUser;

    @FXML
    private TableColumn<ConsignmentVM, Integer> colTotalProduct;

    @FXML
    private TableColumn<ConsignmentVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<ConsignmentVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private ConsignmentService consignmentService;
    private ConsignmentFilterVM consignmentFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.TRANSACTION_CONSIGNMENT_ADD, true, we -> {
            if (getPageData() != null) {
                searchConsignments();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(consignmentFilter);
        StageUtils.modal(Page.TRANSACTION_CONSIGNMENT_FILTER, false, we -> {
            ConsignmentFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            consignmentFilter = result;
            searchConsignments();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<ConsignmentVM> items = tblConsignment.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_CONSIGNMENTS);
            if (result.isConfirmed()) {
                consignmentService.removeConsignments(items.stream().map(ConsignmentVM::getId).toList());
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_CONSIGNMENTS);
                searchConsignments();
            }
        }
    }

    @Override
    protected void initServices() {
        consignmentService = SpringUtils.getBean(ConsignmentService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.TRANSACTION_CONSIGNMENTS, btnAdd, btnRemove);
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colInvoiceNumber, ConsignmentVM::getInvoiceNumber);
        TableViewUtils.setColumnValue(colSupplierName, ConsignmentVM::getSupplierName);
        TableViewUtils.setColumnValue(colUser, ConsignmentVM::getUserFullName);
        TableViewUtils.initTableColumn(
                colTotalProduct,
                new NumberCellFactory<>(locale),
                ConsignmentVM::getTotalProduct,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colInvoiceDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ConsignmentVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ConsignmentVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ConsignmentVM::getUpdatedAt);
        tblConsignment.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblConsignment.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblConsignment.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableConsignment();
            }
        });
        tblConsignment.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableConsignment();
            }
        });
    }

    @Override
    protected void initControlValues() {
        consignmentFilter = getPageData();
        if (consignmentFilter == null) {
            consignmentFilter = new ConsignmentFilterVM();
        }
        searchConsignments();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchConsignments() {
        tblConsignment.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblConsignment.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> consignmentService.searchConsignments(consignmentFilter))
                .thenAccept(consignments -> Platform.runLater(() -> {
                    if (consignments.isEmpty()) {
                        tblConsignment.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblConsignment.setItems(FXCollections.observableList(consignments));
                    TableViewUtils.sortDescending(tblConsignment, colUpdatedAt);
                    lblRows.setText(StringNumberUtils.format(consignments.size(), resources.getLocale()));
                }));
    }

    private void handleActionTableConsignment() {
        if (TableViewUtils.hasItemSelected(tblConsignment)) {
            setPageData(TableViewUtils.getSelectedItem(tblConsignment));
            StageUtils.modal(Page.TRANSACTION_CONSIGNMENT_EDIT, event -> {
                getPageData();
                searchConsignments();
            });
        }
    }

}
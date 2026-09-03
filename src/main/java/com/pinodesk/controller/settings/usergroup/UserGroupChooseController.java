package com.pinodesk.controller.settings.usergroup;

import java.time.LocalDateTime;

import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.controller.CommonDataChooseController;
import com.pinodesk.pandora.factory.LocalDateTimeCellFactory;
import com.pinodesk.pandora.utility.EventUtils;
import com.pinodesk.pandora.utility.TableViewUtils;
import com.pinodesk.service.UserGroupService;
import com.pinodesk.toolbox.future.AsyncUtils;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.UserGroupVM;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class UserGroupChooseController extends CommonDataChooseController<UserGroupVM> {

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<UserGroupVM> tblUserGroups;

    @FXML
    private TableColumn<UserGroupVM, String> colDescription;

    @FXML
    private TableColumn<UserGroupVM, String> colName;

    @FXML
    private TableColumn<UserGroupVM, String> colStatus;

    @FXML
    private TableColumn<UserGroupVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<UserGroupVM, LocalDateTime> colUpdatedAt;

    private UserGroupService userGroupService;

    @Override
    protected void initDataChooseControlActions() {
        TableViewUtils.setColumnValue(colDescription, UserGroupVM::getDescription);
        TableViewUtils.setColumnValue(colName, UserGroupVM::getName);
        TableViewUtils.setColumnValue(colStatus, UserGroupVM::getStatus);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                UserGroupVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                UserGroupVM::getUpdatedAt);
        tblUserGroups.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblUserGroups.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblUserGroups.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                btnChoose.fire();
            }
        });
        tfSearch.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                searchSuppliers();
            }
        });
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        // Nothing to do
    }

    @Override
    protected UserGroupVM getSelectedData() {
        return tblUserGroups.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices() {
        userGroupService = SpringUtils.getBean(UserGroupService.class);
    }

    private void searchSuppliers() {
        tblUserGroups.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblUserGroups.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> userGroupService.searchUserGroupsByKeyword(tfSearch.getText()))
                .thenAccept(suppliers -> Platform.runLater(() -> {
                    if (suppliers.isEmpty()) {
                        tblUserGroups.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblUserGroups.setItems(FXCollections.observableList(suppliers));
                    TableViewUtils.sortAscending(tblUserGroups, colName);
                }));
    }

}

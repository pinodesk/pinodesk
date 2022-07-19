package pinus.desktop.controller.user;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

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
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.constant.Page;
import pinus.desktop.controller.BaseController;
import pinus.desktop.service.UserService;
import pinus.desktop.viewmodel.UserFilterVM;
import pinus.desktop.viewmodel.UserVM;

public class UserMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<UserVM> tblUsers;

    @FXML
    private TableColumn<UserVM, String> colFullName;

    @FXML
    private TableColumn<UserVM, String> colUsername;

    @FXML
    private TableColumn<UserVM, String> colUserGroup;

    @FXML
    private TableColumn<UserVM, String> colStatus;

    @FXML
    private TableColumn<UserVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<UserVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private UserService userService;

    private UserFilterVM userFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {

    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(userFilter);
        StageUtils.modal(Page.SETTINGS_USER_FILTER, false, we -> {
            UserFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            userFilter = result;
            searchUsers();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<UserVM> items = tblUsers.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_USERS);
            if (result.isConfirmed()) {
                userService.removeUsers(items.stream().map(UserVM::getId).toList());
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_USERS);
                searchUsers();
            }
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        userService = ctx.getBean(UserService.class);
    }

    @Override
    protected void initControlActions() {
        TableViewUtils.setColumnValue(colFullName, UserVM::getFullName);
        TableViewUtils.setColumnValue(colUsername, UserVM::getUsername);
        TableViewUtils.setColumnValue(colUserGroup, UserVM::getUserGroupName);
        TableViewUtils.setColumnValue(colStatus, UserVM::getStatus);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                UserVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                UserVM::getUpdatedAt);
        tblUsers.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_NO_DATA)));
        tblUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblUsers.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableUser();
            }
        });
        tblUsers.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableUser();
            }
        });
    }

    @Override
    protected void initControlValues() {
        userFilter = new UserFilterVM();
        searchUsers();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchUsers() {
        tblUsers.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_LOADING_DATA)));
        tblUsers.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> userService.searchUsersByFilter(userFilter))
                .thenAccept(users -> Platform.runLater(() -> {
                    if (users.isEmpty()) {
                        tblUsers.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                        return;
                    }
                    tblUsers.setItems(FXCollections.observableList(users));
                    TableViewUtils.sortDescending(tblUsers, colUpdatedAt);
                    lblRows.setText(users.size() + "");
                }));
    }

    private void handleActionTableUser() {
        if (TableViewUtils.hasItemSelected(tblUsers)) {
            setPageData(TableViewUtils.getSelectedItem(tblUsers));
            StageUtils.modal(Page.SETTINGS_USER_EDIT, false, event -> {
                if (getPageData() != null) {
                    searchUsers();
                }
            });
        }
    }

}

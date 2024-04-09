package pinodesk.controller.settings.user;

import java.time.LocalDateTime;

import com.mudiatech.pandora.factory.LocalDateTimeCellFactory;
import com.mudiatech.pandora.utility.AlertResult;
import com.mudiatech.pandora.utility.EventUtils;
import com.mudiatech.pandora.utility.StageUtils;
import com.mudiatech.pandora.utility.TableViewUtils;
import com.mudiatech.toolbox.data.StringNumberUtils;
import com.mudiatech.toolbox.future.AsyncUtils;

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
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.MenuCodeConstants;
import pinodesk.constant.MessageCode;
import pinodesk.constant.Page;
import pinodesk.controller.BaseController;
import pinodesk.service.UserService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.UserFilterVM;
import pinodesk.viewmodel.UserVM;

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
        StageUtils.modal(Page.SETTINGS_USER_ADD, false, we -> {
            if (getPageData() != null) {
                searchUsers();
            }
        });
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
    protected void initServices() {
        userService = SpringUtils.getBean(UserService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.SETTINGS_USERS, btnAdd, btnRemove);
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
        tblUsers.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
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
        tblUsers.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblUsers.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> userService.searchUsersByFilter(userFilter))
                .thenAccept(users -> Platform.runLater(() -> {
                    if (users.isEmpty()) {
                        tblUsers.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                        return;
                    }
                    tblUsers.setItems(FXCollections.observableList(users));
                    TableViewUtils.sortDescending(tblUsers, colUpdatedAt);
                    lblRows.setText(StringNumberUtils.format(users.size(), resources.getLocale()));
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

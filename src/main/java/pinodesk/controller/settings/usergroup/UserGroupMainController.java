package pinodesk.controller.settings.usergroup;

import java.time.LocalDateTime;

import com.gitlab.mudiasoft.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.toolbox.data.StringNumberUtils;
import com.gitlab.mudiasoft.toolbox.future.AsyncUtils;

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
import pinodesk.service.UserGroupService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.UserGroupFilterVM;
import pinodesk.viewmodel.UserGroupVM;

public class UserGroupMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

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

    @FXML
    private Label lblRows;

    private UserGroupService userGroupService;

    private UserGroupFilterVM userGroupFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.SETTINGS_USER_GROUP_ADD, we -> {
            if (getPageData() != null) {
                searchUserGroups();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(userGroupFilter);
        StageUtils.modal(Page.SETTINGS_USER_GROUP_FILTER, false, we -> {
            UserGroupFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            userGroupFilter = result;
            searchUserGroups();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<UserGroupVM> items = tblUserGroups.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_USER_GROUPS);
            if (result.isConfirmed()) {
                userGroupService.removeUserGroups(items.stream().map(UserGroupVM::getId).toList());
                searchUserGroups();
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_USER_GROUPS);
            }
        }
    }

    @Override
    protected void initServices() {
        userGroupService = SpringUtils.getBean(UserGroupService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.SETTINGS_USER_GROUPS, btnAdd, btnRemove);
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
        tblUserGroups.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblUserGroups.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableUserGroup();
            }
        });
        tblUserGroups.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableUserGroup();
            }
        });
    }

    @Override
    protected void initControlValues() {
        userGroupFilter = new UserGroupFilterVM();
        searchUserGroups();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchUserGroups() {
        tblUserGroups.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblUserGroups.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> userGroupService.searchUserGroupsByFilter(userGroupFilter))
                .thenAccept(userGroups -> Platform.runLater(() -> {
                    if (userGroups.isEmpty()) {
                        tblUserGroups.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                        return;
                    }
                    tblUserGroups.setItems(FXCollections.observableList(userGroups));
                    TableViewUtils.sortDescending(tblUserGroups, colUpdatedAt);
                    lblRows.setText(StringNumberUtils.format(userGroups.size(), resources.getLocale()));
                }));
    }

    private void handleActionTableUserGroup() {
        if (TableViewUtils.hasItemSelected(tblUserGroups)) {
            setPageData(TableViewUtils.getSelectedItem(tblUserGroups));
            StageUtils.modal(Page.SETTINGS_USER_GROUP_EDIT, event -> {
                if (getPageData() != null) {
                    searchUserGroups();
                }
            });
        }
    }

}

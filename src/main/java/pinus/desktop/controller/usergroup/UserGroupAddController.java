package pinus.desktop.controller.usergroup;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.constant.SimpleStatus;
import pinus.desktop.constant.UserGroupStatus;
import pinus.desktop.controller.CommonDataSaveController;
import pinus.desktop.service.UserGroupService;
import pinus.desktop.util.SpringUtils;
import pinus.desktop.viewmodel.UserGroupAddVM;
import pinus.desktop.viewmodel.UserGroupMenuVM;

public class UserGroupAddController extends CommonDataSaveController {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfDescription;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;

    @FXML
    private TableView<UserGroupMenuVM> tblMenus;

    @FXML
    private TableColumn<UserGroupMenuVM, String> colName;

    @FXML
    private TableColumn<UserGroupMenuVM, Boolean> colRead;

    @FXML
    private TableColumn<UserGroupMenuVM, Boolean> colWrite;

    @FXML
    private Button btnSaveAndAdd;

    private UserGroupService userGroupService;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_USER_GROUP);
            resetControls();
            initDataSaveControlValues();
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(UserGroupStatus.ACTIVE, translator.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(UserGroupStatus.INACTIVE, translator.translate(CommonLabel.LBL_INACTIVE)));
        colName.setSortable(false);
        colRead.setSortable(false);
        colWrite.setSortable(false);
        TableViewUtils.setColumnValue(
                colName,
                vm -> vm.getParentMenuId() == null ? vm.getMenuName() : "\t" + vm.getMenuName());
        colRead.setCellFactory(CheckBoxTableCell.forTableColumn(colRead));
        colRead.setCellValueFactory(data -> data.getValue().booleanReadProperty());
        colWrite.setCellFactory(CheckBoxTableCell.forTableColumn(colWrite));
        colWrite.setCellValueFactory(data -> data.getValue().booleanWriteProperty());
        tblMenus.setEditable(true);
        tblMenus.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_NO_DATA)));
    }

    @Override
    protected void initDataSaveControlValues() {
        ComboBoxUtils.selectIndex(cbStatus, 0);
        loadUserGroupMenus();
    }

    @Override
    protected Object save() {
        UserGroupAddVM userGroupAdd = new UserGroupAddVM();
        userGroupAdd.setName(tfName.getText());
        userGroupAdd.setDescription(tfDescription.getText());
        userGroupAdd.setStatus(ComboBoxUtils.getSelectedItem(cbStatus).getValue());
        userGroupAdd.setUserGroupMenus(tblMenus.getItems());
        userGroupService.createUserGroup(userGroupAdd);
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        userGroupService = SpringUtils.getBean(UserGroupService.class);
    }

    private void loadUserGroupMenus() {
        Locale locale = resources.getLocale();
        tblMenus.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_LOADING_DATA)));
        tblMenus.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> userGroupService.getUserGroupMenus(locale.getLanguage()))
                .thenAccept(menus -> Platform.runLater(() -> {
                    if (menus.isEmpty()) {
                        tblMenus.setPlaceholder(new Label(translator.translate(CommonLabel.LBL_NO_DATA)));
                        return;
                    }
                    setChangeListeners(menus);
                    tblMenus.setItems(FXCollections.observableList(menus));
                }));
    }

    private void setChangeListeners(List<UserGroupMenuVM> menus) {
        menus.forEach(menu -> {
            setReadPropertyChangeListener(menu);
            setWritePropertyChangeListener(menu);
        });
    }

    private Map<Long, ChangeListener<Boolean>> mapReadPropertyChangeListener = new HashMap<>();
    private Map<Long, ChangeListener<Boolean>> mapWritePropertyChangeListener = new HashMap<>();

    private void setReadPropertyChangeListener(UserGroupMenuVM menu) {
        ChangeListener<Boolean> listener = (o, ov, nv) -> {
            boolean isChecked = BooleanUtils.isTrue(nv);
            menu.setRead(isChecked ? SimpleStatus.YES.toString() : SimpleStatus.NO.toString());
            if (!isChecked) {
                menu.setBooleanWrite(false);
                menu.setWrite(SimpleStatus.NO.toString());
            }
            if (menu.getParentMenuId() == null) {
                handleReadPropertyParentChange(menu, isChecked);
            } else {
                handleReadPropertyChildChange(menu, isChecked);
            }
        };
        mapReadPropertyChangeListener.put(menu.getMenuId(), listener);
        menu.booleanReadProperty().addListener(listener);
    }

    private void handleReadPropertyParentChange(UserGroupMenuVM parent, boolean isChecked) {
        tblMenus.getItems().forEach(m -> {
            if (Objects.equals(m.getParentMenuId(), parent.getMenuId())) {
                ChangeListener<Boolean> rl = mapReadPropertyChangeListener.get(m.getMenuId());
                m.booleanReadProperty().removeListener(rl);
                m.setBooleanRead(isChecked);
                m.setRead(isChecked ? SimpleStatus.YES.toString() : SimpleStatus.NO.toString());
                m.booleanReadProperty().addListener(rl);
                if (!isChecked) {
                    ChangeListener<Boolean> wl = mapReadPropertyChangeListener.get(m.getMenuId());
                    m.booleanWriteProperty().removeListener(wl);
                    m.setBooleanWrite(false);
                    m.setWrite(SimpleStatus.NO.toString());
                    m.booleanWriteProperty().addListener(wl);
                }
            }
        });
    }

    private void handleReadPropertyChildChange(UserGroupMenuVM child, boolean isChecked) {
        UserGroupMenuVM parent = null;
        boolean othersUnchecked = !isChecked;
        for (UserGroupMenuVM m : tblMenus.getItems()) {
            if (Objects.equals(m.getMenuId(), child.getParentMenuId())) {
                parent = m;
                continue;
            }
            if (Objects.equals(m.getParentMenuId(), child.getParentMenuId())) {
                othersUnchecked = othersUnchecked && !m.isBooleanRead();
            }
        }
        if (parent == null) {
            return;
        }
        ChangeListener<Boolean> listener = mapReadPropertyChangeListener.get(parent.getMenuId());
        parent.booleanReadProperty().removeListener(listener);
        if (isChecked) {
            parent.setBooleanRead(true);
            parent.setRead(SimpleStatus.YES.toString());
        } else if (othersUnchecked) {
            parent.setBooleanRead(false);
            parent.setRead(SimpleStatus.NO.toString());
        }
        parent.booleanReadProperty().addListener(listener);
    }

    private void setWritePropertyChangeListener(UserGroupMenuVM menu) {
        ChangeListener<Boolean> listener = (o, ov, nv) -> {
            boolean isChecked = BooleanUtils.isTrue(nv);
            menu.setWrite(isChecked ? SimpleStatus.YES.toString() : SimpleStatus.NO.toString());
            if (isChecked) {
                menu.setBooleanRead(true);
                menu.setRead(SimpleStatus.YES.toString());
            }
            if (menu.getParentMenuId() == null) {
                handleWritePropertyParentChange(menu, isChecked);
            } else {
                handleWritePropertyChildChange(menu, isChecked);
            }
        };
        mapWritePropertyChangeListener.put(menu.getMenuId(), listener);
        menu.booleanWriteProperty().addListener(listener);
    }

    private void handleWritePropertyChildChange(UserGroupMenuVM child, boolean isChecked) {
        UserGroupMenuVM parent = null;
        boolean othersUnchecked = !isChecked;
        for (UserGroupMenuVM m : tblMenus.getItems()) {
            if (Objects.equals(m.getMenuId(), child.getParentMenuId())) {
                parent = m;
                continue;
            }
            if (Objects.equals(m.getParentMenuId(), child.getParentMenuId())) {
                othersUnchecked = othersUnchecked && !m.isBooleanWrite();
            }
        }
        if (parent == null) {
            return;
        }
        ChangeListener<Boolean> listener = mapWritePropertyChangeListener.get(parent.getMenuId());
        parent.booleanWriteProperty().removeListener(listener);
        if (isChecked) {
            parent.setBooleanWrite(true);
            parent.setWrite(SimpleStatus.YES.toString());
        } else if (othersUnchecked) {
            parent.setBooleanWrite(false);
            parent.setWrite(SimpleStatus.NO.toString());
        }
        parent.booleanWriteProperty().addListener(listener);
    }

    private void handleWritePropertyParentChange(UserGroupMenuVM parent, boolean isChecked) {
        tblMenus.getItems().forEach(m -> {
            if (Objects.equals(m.getParentMenuId(), parent.getMenuId())) {
                ChangeListener<Boolean> wl = mapWritePropertyChangeListener.get(m.getMenuId());
                m.booleanWriteProperty().removeListener(wl);
                m.setBooleanWrite(isChecked);
                m.setWrite(isChecked ? SimpleStatus.YES.toString() : SimpleStatus.NO.toString());
                m.booleanWriteProperty().addListener(wl);
                if (isChecked) {
                    ChangeListener<Boolean> rl = mapReadPropertyChangeListener.get(m.getMenuId());
                    m.booleanReadProperty().removeListener(rl);
                    m.setBooleanRead(true);
                    m.setRead(SimpleStatus.YES.toString());
                    m.booleanReadProperty().addListener(rl);
                }
            }
        });
    }

    private void resetControls() {
        TextFieldUtils.setTextEmpty(tfName, tfDescription);
        tblMenus.getItems().clear();
    }

}

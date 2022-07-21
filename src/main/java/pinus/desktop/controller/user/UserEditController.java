package pinus.desktop.controller.user;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.constant.UserStatus;
import pinus.desktop.controller.CommonDataSaveController;
import pinus.desktop.service.UserGroupService;
import pinus.desktop.service.UserService;
import pinus.desktop.viewmodel.ChooseResultVM;
import pinus.desktop.viewmodel.UserEditVM;
import pinus.desktop.viewmodel.UserGroupVM;
import pinus.desktop.viewmodel.UserVM;

public class UserEditController extends CommonDataSaveController {

    @FXML
    private TextField tfFullName;

    @FXML
    private TextField tfUsername;

    @FXML
    private TextField tfPassword;

    @FXML
    private TextField tfPasswordConfirmation;

    @FXML
    private TextField tfUserGroup;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;

    @FXML
    private Button btnRemove;

    private UserService userService;

    private UserGroupService userGroupService;

    private UserVM currentUser;

    private UserGroupVM selectedUserGroup;

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_USER);
        if (result.isConfirmed()) {
            userService.removeUsers(Arrays.asList(currentUser.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_USER);
            setPageData(Boolean.TRUE);
            close();
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(UserStatus.ACTIVE, translator.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(UserStatus.INACTIVE, translator.translate(CommonLabel.LBL_INACTIVE)));
        setUserGroupChooser(tfUserGroup, this::handleSelectedUserGroup, tfPassword);
    }

    @Override
    protected void initDataSaveControlValues() {
        currentUser = getPageData();
        tfFullName.setText(currentUser.getFullName());
        tfUsername.setText(currentUser.getUsername());
        ComboBoxUtils.select(
                cbStatus,
                () -> cbStatus.getItems().stream()
                        .filter(vm -> currentUser.getStatus().equals(vm.getValue().toString())).findAny()
                        .orElseThrow());
        selectedUserGroup = userGroupService.getUserGroupById(currentUser.getUserGroupId());
        tfUserGroup.setText(selectedUserGroup.getName());
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfFullName, MessageCode.ERROR_EMPTY_NAME);
        validator.validateBlank(tfUsername, MessageCode.ERROR_EMPTY_USERNAME);
        validator.validateCustom(() -> {
            String password = tfPassword.getText();
            String passwordConfirmation = tfPasswordConfirmation.getText();
            return !StringUtils.isAllBlank(password, passwordConfirmation)
                    && !StringUtils.equals(password, passwordConfirmation);
        }, MessageCode.ERROR_MISMATCH_PASSWORD_CONFIRMATION);
        validator.validateCustom(() -> selectedUserGroup == null, MessageCode.ERROR_EMPTY_USER_GROUP);
    }

    @Override
    protected Object save() {
        UserEditVM userEdit = new UserEditVM();
        userEdit.setFullName(tfFullName.getText());
        userEdit.setUsername(tfUsername.getText());
        userEdit.setPassword(StringUtils.defaultIfBlank(tfPassword.getText(), null));
        userEdit.setUserGroupId(selectedUserGroup.getId());
        userEdit.setStatus(ComboBoxUtils.getSelectedItem(cbStatus).getValue());
        return userService.updateUser(userEdit, currentUser.getId());
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        userService = ctx.getBean(UserService.class);
        userGroupService = ctx.getBean(UserGroupService.class);
    }

    public void handleSelectedUserGroup(ChooseResultVM<UserGroupVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(userGroup -> {
            selectedUserGroup = userGroup;
            tfUserGroup.setText(userGroup.getName());
        }, () -> {
            selectedUserGroup = null;
            tfUserGroup.setText("");
        });
    }

}

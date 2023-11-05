package pospino.desktop.controller.settings.user;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.mudiasoft.pandora.constant.KeyConstants;
import com.gitlab.mudiasoft.pandora.model.SimpleComboBoxModel;
import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.UserStatus;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.service.UserService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.UserAddVM;
import pospino.desktop.viewmodel.UserGroupVM;

public class UserAddController extends CommonDataSaveController {

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
    private Button btnSaveAndAdd;

    private UserGroupVM selectedUserGroup;

    private UserService userService;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_USER);
            resetControls();
            initDataSaveControlValues();
        }
    }

    @Override
    protected void initServices() {
        userService = SpringUtils.getBean(UserService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(UserStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(UserStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE)));
        setUserGroupChooser(tfUserGroup, this::handleSelectedUserGroup, tfPassword);
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyConstants.CTRL_SHIFT_S.match(event)) {
                btnSaveAndAdd.fire();
                return;
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        ComboBoxUtils.selectIndex(cbStatus, 0);
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfFullName, MessageCode.ERROR_EMPTY_FULL_NAME);
        validator.validateBlank(tfUsername, MessageCode.ERROR_EMPTY_USERNAME);
        validator.validateBlank(tfPassword, MessageCode.ERROR_EMPTY_PASSWORD);
        validator.validateBlank(tfPasswordConfirmation, MessageCode.ERROR_EMPTY_PASSWORD_CONFIRMATION);
        validator.validateCustom(
                () -> !StringUtils.equals(tfPassword.getText(), tfPasswordConfirmation.getText()),
                MessageCode.ERROR_MISMATCH_PASSWORD_CONFIRMATION);
        validator.validateCustom(() -> selectedUserGroup == null, MessageCode.ERROR_EMPTY_USER_GROUP);
    }

    @Override
    protected Object save() {
        UserAddVM userAdd = new UserAddVM();
        userAdd.setFullName(tfFullName.getText());
        userAdd.setUsername(tfUsername.getText());
        userAdd.setPassword(tfPassword.getText());
        userAdd.setUserGroupId(selectedUserGroup.getId());
        userAdd.setStatus(ComboBoxUtils.getSelectedItem(cbStatus).getValue());
        return userService.createUser(userAdd);
    }

    private void resetControls() {
        TextFieldUtils.setTextEmpty(tfFullName, tfUserGroup, tfUsername, tfPassword, tfPasswordConfirmation);
        ComboBoxUtils.selectIndex(cbStatus, 0);
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

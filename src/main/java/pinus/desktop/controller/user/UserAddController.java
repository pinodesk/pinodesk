package pinus.desktop.controller.user;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.controller.CommonDataSaveController;
import pinus.desktop.service.UserService;
import pinus.desktop.viewmodel.ChooseResultVM;
import pinus.desktop.viewmodel.UserAddVM;
import pinus.desktop.viewmodel.UserGroupVM;

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
    protected void initServices(ApplicationContext ctx) {
        userService = ctx.getBean(UserService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
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
        // Nothing
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfFullName, MessageCode.ERROR_EMPTY_NAME);
    }

    @Override
    protected Object save() {
        UserAddVM userAdd = new UserAddVM();
        userAdd.setFullName(tfFullName.getText());
        userAdd.setUsername(tfUsername.getText());
        userAdd.setPassword(tfPassword.getText());
        userAdd.setUserGroupId(selectedUserGroup.getId());
        return userService.createUser(userAdd);
    }

    private void resetControls() {
        TextFieldUtils.setTextEmpty(tfFullName, tfUserGroup, tfUsername, tfPassword, tfPasswordConfirmation);
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

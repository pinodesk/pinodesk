package pospino.desktop.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.SimpleStatus;
import pospino.desktop.constant.UserStatus;
import pospino.desktop.exception.DefaultRuntimeException;
import pospino.desktop.service.ConfigurationService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.UserAddVM;

@Slf4j
public class InitialSetupController extends CommonDataSaveController {

    @FXML
    private TextField tfStoreName;

    @FXML
    private TextField tfStoreAddress;

    @FXML
    private TextField tfFullName;

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private PasswordField pfPasswordConfirmation;

    private ConfigurationService configurationService;

    @Override
    protected void onActionBtnCancel(ActionEvent event) {
        super.onActionBtnCancel(event);
        log.info("Initial setup was cancelled, exiting application.");
        System.exit(0);
    }

    @Override
    protected void onActionBtnSave(ActionEvent event) {
        CompletableFuture.runAsync(this::processDataSave).whenComplete((result, ex) -> {
            if (ex != null) {
                throw new DefaultRuntimeException(ex);
            }
            if (isLastDataSaved()) {
                Platform.runLater(() -> {
                    close();
                    StageUtils.open(Page.LOGIN, false);
                });
            }
        });
    }

    @Override
    protected void initDataSaveControlActions() {
        // Nothing to init
    }

    @Override
    protected void initDataSaveControlValues() {
        // Nothing to init
    }

    @Override
    protected Object save() {
        Map<String, String> map = new HashMap<>();
        map.put(ConfigurationConstants.STORE_NAME, tfStoreName.getText());
        map.put(ConfigurationConstants.STORE_ADDRESS, tfStoreAddress.getText());
        map.put(ConfigurationConstants.INITIAL_SETUP_DONE, SimpleStatus.YES.toString());
        UserAddVM userAdd = new UserAddVM();
        userAdd.setFullName(tfFullName.getText());
        userAdd.setPassword(pfPassword.getText());
        userAdd.setStatus(UserStatus.ACTIVE);
        userAdd.setUserGroupId(CommonConstants.USER_GROUP_ID_ADMINISTRATOR);
        userAdd.setUsername(tfUsername.getText());
        configurationService.saveIntialSetup(map, userAdd);
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfStoreName, MessageCode.ERROR_EMPTY_STORE_NAME);
        validator.validateBlank(tfFullName, MessageCode.ERROR_EMPTY_FULL_NAME);
        validator.validateBlank(tfUsername, MessageCode.ERROR_EMPTY_USERNAME);
        validator.validateBlank(pfPassword, MessageCode.ERROR_EMPTY_PASSWORD);
        validator.validateBlank(pfPasswordConfirmation, MessageCode.ERROR_EMPTY_PASSWORD_CONFIRMATION);
        validator.validateCustom(
                () -> !StringUtils.equals(pfPassword.getText(), pfPasswordConfirmation.getText()),
                MessageCode.ERROR_MISMATCH_PASSWORD_CONFIRMATION);
    }

    @Override
    protected void initServices() {
        configurationService = SpringUtils.getBean(ConfigurationService.class);
    }

}

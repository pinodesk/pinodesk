package pinodesk.controller;

import com.mudiatech.pandora.utility.ControlValidator;
import com.mudiatech.pandora.utility.StageUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import pinodesk.constant.MessageCode;
import pinodesk.constant.Page;

@Slf4j
public class LoginController extends CommonDataSaveController {

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField pfPassword;

    @Override
    protected void onActionBtnCancel(ActionEvent event) {
        super.onActionBtnCancel(event);
        log.info("Login cancelled, exiting application.");
        System.exit(0);
    }

    @Override
    protected void onActionBtnSave(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            Platform.runLater(() -> {
                close();
                StageUtils.open(Page.MAIN);
            });
        }
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
        sessionService.login(tfUsername.getText(), pfPassword.getText());
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfUsername, MessageCode.ERROR_EMPTY_USERNAME);
        validator.validateBlank(pfPassword, MessageCode.ERROR_EMPTY_PASSWORD);
    }

    @Override
    protected void initServices() {
        // Nothing to init
    }

}

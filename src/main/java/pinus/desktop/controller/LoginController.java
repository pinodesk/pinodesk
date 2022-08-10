package pinus.desktop.controller;

import java.io.IOException;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import pinus.desktop.Pinus;
import pinus.desktop.constant.MessageCode;

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
            close();
            try {
                Pinus.loadMainPage();
            } catch (IOException e) {
                log.error("Load main page error!", e);
            }
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
        sessionService.get().login(tfUsername.getText(), pfPassword.getText());
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfUsername, MessageCode.ERROR_EMPTY_USERNAME);
        validator.validateBlank(pfPassword, MessageCode.ERROR_EMPTY_PASSWORD);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // Nothing to init
    }

}

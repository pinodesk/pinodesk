package pinus.desktop.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.constant.Page;
import pinus.desktop.exception.DefaultRuntimeException;
import pinus.desktop.service.LoginService;
import pinus.desktop.util.SpringUtils;

@Slf4j
public class LoginController extends CommonDataSaveController {

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField pfPassword;

    private LoginService loginService;

    @Override
    protected void onActionBtnCancel(ActionEvent event) {
        super.onActionBtnCancel(event);
        log.info("Login cancelled, exiting application.");
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
                    StageUtils.open(Page.MAIN, false);
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
        loginService.login(tfUsername.getText(), pfPassword.getText());
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfUsername, MessageCode.ERROR_EMPTY_USERNAME);
        validator.validateBlank(pfPassword, MessageCode.ERROR_EMPTY_PASSWORD);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        loginService = SpringUtils.getBean(LoginService.class);
    }

}

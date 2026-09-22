package com.pinodesk.controller;

import java.util.Map;

import com.pinodesk.apimodel.RequestInstallationCodeResponse;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.Page;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.service.InstallationService;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.util.TaskUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterInstallationController extends CommonDataSaveController {

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfInstallationCode;

    @FXML
    private Label lblInstallationDesc;

    @FXML
    private Button btnRequestCode;

    @FXML
    private Button btnSkipForNow;

    private InstallationService installationService;

    private String installationRequestId;

    @FXML
    protected void onActionBtnRequestCode(ActionEvent event) {
        String email = tfEmail.getText();
        if (email == null || email.trim().isEmpty()) {
            Platform.runLater(() -> displayError(MessageCode.ERROR_EMPTY_EMAIL));
            return;
        }
        Stage loading = displayLoading();
        TaskUtils.runTask("Request installation code", () -> {
            requestInstallationCode(email);
            Platform.runLater(() -> {
                loading.hide();
                if (installationRequestId != null) {
                    displayInfo(MessageCode.SUCCESS_REQUEST_INSTALLATION_CODE);
                    tfInstallationCode.requestFocus();
                }
            });
        }, throwable -> Platform.runLater(() -> {
            loading.hide();
            handleException(throwable);
        }));
    }

    @FXML
    protected void onActionBtnSkipForNow(ActionEvent event) {
        log.info("Installation registration skipped for now by user.");
        configurationService
                .updateConfiguration(Map.of(ConfigurationConstants.REGISTRATION_SKIPPED, SimpleStatus.YES.toString()));
        String initialSetupDone = configurationService.getConfiguration(ConfigurationConstants.INITIAL_SETUP_DONE);
        boolean isInitialSetupDone = SimpleStatus.YES.toString().equals(initialSetupDone);
        closeAndOpenNextPage(isInitialSetupDone);
    }

    @Override
    protected void onActionBtnCancel(ActionEvent event) {
        super.onActionBtnCancel(event);
        log.info("Installation registration was cancelled, exiting application.");
        System.exit(0);
    }

    @Override
    protected void onActionBtnSave(ActionEvent event) {
        if (installationRequestId == null) {
            Platform.runLater(() -> displayError(MessageCode.ERROR_EMPTY_INSTALLATION_REQUEST_ID));
            return;
        }
        Stage loading = displayLoading();
        TaskUtils.runTask("Register installation", () -> {
            processDataSave();
            if (isLastDataSaved()) {
                String initialSetupDone = configurationService
                        .getConfiguration(ConfigurationConstants.INITIAL_SETUP_DONE);
                boolean isInitialSetupDone = SimpleStatus.YES.toString().equals(initialSetupDone);
                Platform.runLater(() -> {
                    displayInfo(MessageCode.SUCCESS_INSTALLATION_REGISTRATION);
                    closeAndOpenNextPage(isInitialSetupDone);
                });
            }
            Platform.runLater(loading::hide);
        }, throwable -> Platform.runLater(() -> {
            loading.hide();
            handleException(throwable);
        }));
    }

    @Override
    protected void initDataSaveControlActions() {
        btnRequestCode.setOnAction(this::onActionBtnRequestCode);
    }

    @Override
    protected void initDataSaveControlValues() {
        // Nothing to init
    }

    @Override
    protected Object save() {
        installationService
                .registerInstallation(tfEmail.getText(), installationRequestId, tfInstallationCode.getText());
        return true;
    }

    private void requestInstallationCode(String email) {
        RequestInstallationCodeResponse response = installationService.requestInstallationCode(email);
        if (response != null && response.getInstallationRequestId() != null) {
            installationRequestId = response.getInstallationRequestId();
            log.info("Installation code requested successfully, request ID: {}", installationRequestId);
        }
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfEmail, MessageCode.ERROR_EMPTY_EMAIL);
        validator.validateBlank(tfInstallationCode, MessageCode.ERROR_EMPTY_INSTALLATION_CODE);
    }

    @Override
    protected void initServices() {
        installationService = SpringUtils.getBean(InstallationService.class);
    }

    private void closeAndOpenNextPage(boolean isInitialSetupDone) {
        close();
        if (!isInitialSetupDone) {
            StageUtils.open(Page.INITIAL_SETUP, false);
            return;
        }
        sessionService.activateLastSession();
        if (!sessionService.isCurrentSessionActive()) {
            StageUtils.open(Page.LOGIN, false);
            return;
        }
        StageUtils.open(Page.MAIN);
    }

}
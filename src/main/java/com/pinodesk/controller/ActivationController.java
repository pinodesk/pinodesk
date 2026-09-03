package com.pinodesk.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import com.pinodesk.apimodel.ActivateReleaseRequest;
import com.pinodesk.apimodel.ActivateReleaseResponse;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.Page;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.pandora.utility.AlertResult;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.properties.ApplicationProperties;
import com.pinodesk.service.api.PinodeskApiService;
import com.pinodesk.toolbox.jackson.JSON;
import com.pinodesk.util.DeviceUtils;
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
public class ActivationController extends CommonDataSaveController {

    @FXML
    private TextField tfActivationCode;

    @FXML
    private Label lblTrialPeriodEnds;

    @FXML
    private Button btnActivateLater;

    private PinodeskApiService pinodeskApiService;

    private ApplicationProperties applicationProperties;

    private Map<String, String> configurationMap;

    @FXML
    protected void onActionBtnActivateLater(ActionEvent event) {
        String strTrialPeriodDays = configurationMap.get(ConfigurationConstants.TRIAL_PERIOD_DAYS);
        String message = String.format(t.translate(MessageCode.CONFIRMATION_ACTIVATE_LATER), strTrialPeriodDays);
        AlertResult result = displayConfirmation(message);
        if (result.isConfirmed()) {
            TaskUtils.runTask("Activate later", () -> {
                configurationService.updateConfiguration(
                        Map.of(ConfigurationConstants.ACTIVATE_LATER, SimpleStatus.YES.toString()));
                log.debug("activate later already set to yes");
                String initialSetupDone = configurationService
                        .getConfiguration(ConfigurationConstants.INITIAL_SETUP_DONE);
                boolean isInitialSetupDone = SimpleStatus.YES.toString().equals(initialSetupDone);
                Platform.runLater(() -> {
                    closeAndOpenNextPage(isInitialSetupDone);
                });
            });
        }
    }

    @Override
    protected void onActionBtnCancel(ActionEvent event) {
        super.onActionBtnCancel(event);
        log.info("Activation was cancelled, exiting application.");
        System.exit(0);
    }

    @Override
    protected void onActionBtnSave(ActionEvent event) {
        Stage loading = displayLoading();
        TaskUtils.runTask("Submit activation", () -> {
            processDataSave();
            if (isLastDataSaved()) {
                String initialSetupDone = configurationService
                        .getConfiguration(ConfigurationConstants.INITIAL_SETUP_DONE);
                boolean isInitialSetupDone = SimpleStatus.YES.toString().equals(initialSetupDone);
                Platform.runLater(() -> {
                    displayInfo(MessageCode.SUCCESS_ACTIVATION);
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
        String strTrialPeriodDays = configurationMap.get(ConfigurationConstants.TRIAL_PERIOD_DAYS);
        String strInstallDatetime = configurationMap.get(ConfigurationConstants.INSTALL_DATETIME);
        LocalDate today = LocalDate.now();
        LocalDateTime installDatetime = ZonedDateTime.parse(strInstallDatetime).toLocalDateTime();
        int trialPeriodDays = Integer.parseInt(strTrialPeriodDays);
        LocalDate endTrialDate = installDatetime.plus(trialPeriodDays, ChronoUnit.DAYS).toLocalDate();
        if (today.isAfter(endTrialDate)) {
            lblTrialPeriodEnds.setVisible(true);
            setVisibleInLayout(false, btnActivateLater);
        }
    }

    @Override
    protected void initDataSaveControlValues() {
        // Nothing to init
    }

    @Override
    protected Object save() {
        ActivateReleaseRequest req = new ActivateReleaseRequest();
        req.setActivationCode(tfActivationCode.getText());
        req.setReleasePlatform(applicationProperties.getReleasePlatform());
        req.setReleaseVersion(applicationProperties.getAppVersion());
        req.setDeviceSignature(DeviceUtils.getDeviceSignature());
        req.setDeviceManufacturer(defaultNullUnknown(DeviceUtils.getDeviceManufacturer()));
        req.setDeviceModel(defaultNullUnknown(DeviceUtils.getDeviceModel()));
        req.setOsName(defaultNullUnknown(DeviceUtils.getOsName()));
        req.setOsVersion(defaultNullUnknown(DeviceUtils.getOsVersion()));
        req.setOsFamily(defaultNullUnknown(DeviceUtils.getOsFamily()));
        req.setOsArch(defaultNullUnknown(DeviceUtils.getOsArch()));
        req.setOsBitness(DeviceUtils.getOsBitness());
        req.setCpuName(defaultNullUnknown(DeviceUtils.getCpuName()));
        req.setCpuVendor(defaultNullUnknown(DeviceUtils.getCpuVendor()));
        req.setCpuFamily(defaultNullUnknown(DeviceUtils.getCpuFamily()));
        req.setRamSize(DeviceUtils.getRamSize());
        req.setStorageSize(DeviceUtils.getStorageSize());
        ActivateReleaseResponse response = pinodeskApiService.activateRelease(req);
        Map<String, String> map = new HashMap<>();
        map.put(ConfigurationConstants.ACTIVATION_DATA, JSON.stringify(response));
        configurationService.updateConfiguration(map);
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfActivationCode, MessageCode.ERROR_EMPTY_ACTIVATION_CODE);
    }

    @Override
    protected void initServices() {
        pinodeskApiService = SpringUtils.getBean(PinodeskApiService.class);
        applicationProperties = SpringUtils.getBean(ApplicationProperties.class);
        configurationMap = configurationService.getConfigurationMap();
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

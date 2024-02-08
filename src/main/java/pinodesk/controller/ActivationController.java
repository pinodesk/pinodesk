package pinodesk.controller;

import java.util.HashMap;
import java.util.Map;

import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.toolbox.jackson.JSON;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pinodesk.apimodel.ActivateReleaseRequest;
import pinodesk.apimodel.ActivateReleaseResponse;
import pinodesk.constant.ConfigurationConstants;
import pinodesk.constant.MessageCode;
import pinodesk.constant.Page;
import pinodesk.constant.SimpleStatus;
import pinodesk.properties.ApplicationProperties;
import pinodesk.service.api.PinodeskApiService;
import pinodesk.util.DeviceUtils;
import pinodesk.util.SpringUtils;
import pinodesk.util.TaskUtils;

@Slf4j
public class ActivationController extends CommonDataSaveController {

    @FXML
    private TextField tfActivationCode;

    private PinodeskApiService pinodeskApiService;

    private ApplicationProperties applicationProperties;

    @Override
    protected void onActionBtnCancel(ActionEvent event) {
        super.onActionBtnCancel(event);
        log.info("Activation cancelled, exiting application.");
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
        // Nothing to init
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
    }

}

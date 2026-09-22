package com.pinodesk.controller;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.pinodesk.PinodeskConfig;
import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.constant.Page;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.service.ConfigurationService;
import com.pinodesk.service.InstallationService;
import com.pinodesk.service.SessionService;
import com.pinodesk.util.SpringUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplashController {

    @FXML
    private AnchorPane contentPane;

    private boolean isInstallationRegistrationRequired(Map<String, String> configurationMap) {
        InstallationService installationService = SpringUtils.getBean(InstallationService.class);
        if (installationService.isRegistered()) {
            return false;
        }
        String skipped = configurationMap.get(ConfigurationConstants.REGISTRATION_SKIPPED);
        return !SimpleStatus.YES.toString().equals(skipped);
    }

    @FXML
    void initialize() {
        CompletableFuture.runAsync(() -> SpringUtils.init(PinodeskConfig.class)).thenRun(() -> {
            ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
            Map<String, String> configurationMap = configurationService.getConfigurationMap();
            if (isInstallationRegistrationRequired(configurationMap)) {
                Platform.runLater(() -> {
                    contentPane.getScene().getWindow().hide();
                    StageUtils.open(Page.REGISTER_INSTALLATION, false);
                });
                return;
            }
            String initialSetupDone = configurationMap.get(ConfigurationConstants.INITIAL_SETUP_DONE);
            boolean isInitialSetupDone = SimpleStatus.YES.toString().equals(initialSetupDone);
            if (!isInitialSetupDone) {
                String language = Locale.getDefault().getLanguage();
                if (CommonConstants.LANGUAGE_CODE_INDONESIA.equals(language)) {
                    configurationService.updateConfiguration(Map.of(ConfigurationConstants.LANGUAGE, language));
                }
            }
            Platform.runLater(() -> {
                contentPane.getScene().getWindow().hide();
                if (!isInitialSetupDone) {
                    StageUtils.open(Page.INITIAL_SETUP, false);
                    return;
                }
                SessionService sessionService = SpringUtils.getBean(SessionService.class);
                sessionService.activateLastSession();
                if (!sessionService.isCurrentSessionActive()) {
                    StageUtils.open(Page.LOGIN, false);
                    return;
                }
                StageUtils.open(Page.MAIN);
            });
        }).exceptionally(e -> {
            log.error("An error occurred in splash screen!", e);
            System.exit(0);
            return null;
        });
    }

}

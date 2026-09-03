package com.pinodesk.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import com.pinodesk.PinodeskConfig;
import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.constant.Page;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.service.ConfigurationService;
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

    private boolean isActivationRequired(Map<String, String> configurationMap) {
        String activateLater = configurationMap.get(ConfigurationConstants.ACTIVATE_LATER);
        String strTrialPeriodDays = configurationMap.get(ConfigurationConstants.TRIAL_PERIOD_DAYS);
        String strInstallDatetime = configurationMap.get(ConfigurationConstants.INSTALL_DATETIME);
        String activationData = configurationMap.get(ConfigurationConstants.ACTIVATION_DATA);
        LocalDate today = LocalDate.now();
        LocalDateTime installDatetime = ZonedDateTime.parse(strInstallDatetime).toLocalDateTime();
        log.debug("Install date time: {}", installDatetime);
        int trialPeriodDays = Integer.parseInt(strTrialPeriodDays);
        log.debug("Trial period in days: {}", trialPeriodDays);
        LocalDate endTrialDate = installDatetime.plus(trialPeriodDays, ChronoUnit.DAYS).toLocalDate();
        log.debug("End trial date: {}", endTrialDate);
        log.debug("Today's date: {}", today);
        if (StringUtils.isBlank(activationData)) {
            if (SimpleStatus.YES.toString().equals(activateLater)) {
                return today.isAfter(endTrialDate);
            }
            return true;
        }
        return false;
    }

    @FXML
    void initialize() {
        CompletableFuture.runAsync(() -> SpringUtils.init(PinodeskConfig.class)).thenRun(() -> {
            ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
            Map<String, String> configurationMap = configurationService.getConfigurationMap();
            if (isActivationRequired(configurationMap)) {
                Platform.runLater(() -> {
                    contentPane.getScene().getWindow().hide();
                    StageUtils.open(Page.ACTIVATION, false);
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

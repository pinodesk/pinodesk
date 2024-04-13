package pinodesk.controller;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import com.mudiatech.pandora.utility.StageUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import pinodesk.PinodeskConfig;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.ConfigurationConstants;
import pinodesk.constant.Page;
import pinodesk.constant.SimpleStatus;
import pinodesk.service.ConfigurationService;
import pinodesk.service.SessionService;
import pinodesk.util.SpringUtils;

@Slf4j
public class SplashController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    void initialize() {
        CompletableFuture.runAsync(() -> SpringUtils.init(PinodeskConfig.class)).thenRun(() -> {
            ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
            String activationData = configurationService.getConfiguration(ConfigurationConstants.ACTIVATION_DATA);
            if (StringUtils.isBlank(activationData)) {
                Platform.runLater(() -> {
                    contentPane.getScene().getWindow().hide();
                    StageUtils.open(Page.ACTIVATION, false);
                });
                return;
            }
            String initialSetupDone = configurationService.getConfiguration(ConfigurationConstants.INITIAL_SETUP_DONE);
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

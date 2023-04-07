package pospino.desktop.controller;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.gitlab.mudiasoft.pandora.utility.StageUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import pospino.desktop.PospinoConfig;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.SimpleStatus;
import pospino.desktop.service.ConfigurationService;
import pospino.desktop.service.SessionService;
import pospino.desktop.util.SpringUtils;

@Slf4j
public class SplashController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    void initialize() {
        CompletableFuture.runAsync(() -> SpringUtils.init(PospinoConfig.class)).thenRun(() -> {
            ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
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

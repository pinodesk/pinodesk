package pinus.desktop.controller;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import pinus.desktop.Pinus;
import pinus.desktop.PinusConfig;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.Page;
import pinus.desktop.constant.SimpleStatus;
import pinus.desktop.service.ConfigurationService;
import pinus.desktop.service.LoginService;
import pinus.desktop.util.SpringUtils;

@Slf4j
public class SplashController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    void initialize() {
        CompletableFuture.runAsync(() -> SpringUtils.init(PinusConfig.class)).thenRun(() -> {
            ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
            String initialSetupDone = configurationService.getConfiguration(ConfigurationConstants.INITIAL_SETUP_DONE);
            Platform.runLater(() -> {
                try {
                    contentPane.getScene().getWindow().hide();
                    if (!SimpleStatus.YES.toString().equals(initialSetupDone)) {
                        StageUtils.open(Page.INITIAL_SETUP, false);
                        return;
                    }
                    LoginService loginService = SpringUtils.getBean(LoginService.class);
                    if (!loginService.loginCheck()) {
                        StageUtils.open(Page.LOGIN, false);
                        return;
                    }
                    Pinus.loadMainPage();
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            });
        }).exceptionally(e -> {
            log.error("An error occurred in splash screen!", e);
            System.exit(0);
            return null;
        });
    }

}

package pinus.desktop.controller;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.gitlab.muhammadkholidb.pandora.utility.PageLoader;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import pinus.desktop.Pinus;
import pinus.desktop.PinusConfig;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.service.ConfigurationService;
import pinus.desktop.util.SpringUtils;

@Slf4j
public class SplashController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    void initialize() {
        CompletableFuture.runAsync(() -> {
            SpringUtils.init(PinusConfig.class);
            PageLoader.reset();
            PageLoader.init(CommonConstants.PAGE_TEMPLATE_DIR, () -> {
                ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
                String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
                return ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, new Locale(languageCode));
            });
        }).thenRun(() -> Platform.runLater(() -> {
            try {
                contentPane.getScene().getWindow().hide();
                Pinus.loadMainPage();
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        })).exceptionally(e -> {
            log.error("An error occurred in splash screen!", e);
            System.exit(0);
            return null;
        });
    }

}

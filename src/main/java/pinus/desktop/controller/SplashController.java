package pinus.desktop.controller;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import pinus.desktop.Pinus;
import pinus.desktop.PinusConfig;
import pinus.desktop.util.SpringUtils;

@Slf4j
public class SplashController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    void initialize() {
        CompletableFuture.runAsync(() -> SpringUtils.init(PinusConfig.class)).thenRun(() -> Platform.runLater(() -> {
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

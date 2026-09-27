package com.pinodesk.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.util.ListResourceBundle;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.testfx.api.FxRobot;

import com.pinodesk.JavaFXTestBase;
import com.pinodesk.pandora.utility.Translator;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

class BaseControllerAlertTest extends JavaFXTestBase {

    @Test
    void alertsShouldSupportEveryTypeWithoutNullHeaderOrIconFailure(FxRobot robot) {
        robot.interact(() -> {
            BaseController controller = mock(BaseController.class, CALLS_REAL_METHODS);
            controller.t = new Translator(new ListResourceBundle() {
                @Override
                protected Object[][] getContents() {
                    return new Object[][] {
                            { "BTN_OK", "OK" },
                            { "BTN_YES", "Yes" },
                            { "BTN_NO", "No" },
                            { "LBL_INFORMATION", "Information" },
                            { "LBL_ERROR", "Error" },
                            { "LBL_CONFIRMATION", "Confirmation" } };
                }
            });
            for (AlertType type : AlertType.values()) {
                DialogPane pane = new DialogPane();
                Stage stage = new Stage();
                stage.setScene(new Scene(pane));
                try (MockedConstruction<Alert> alerts = mockConstruction(Alert.class, (alert, context) -> {
                    assertEquals(type, context.arguments().get(0));
                    when(alert.getDialogPane()).thenReturn(pane);
                    when(alert.showAndWait()).thenAnswer(invocation -> Optional.of(pane.getButtonTypes().get(0)));
                })) {
                    if (type == AlertType.CONFIRMATION) {
                        assertTrue(controller.displayAlert(type, "Message").isYes());
                        assertEquals(2, pane.getButtonTypes().size());
                        assertEquals(ButtonData.NO, pane.getButtonTypes().get(1).getButtonData());
                    } else {
                        assertTrue(controller.displayAlert(type, "Message").isOk());
                        assertEquals(1, pane.getButtonTypes().size());
                    }
                    assertEquals(1, alerts.constructed().size());
                    if (type == AlertType.WARNING || type == AlertType.NONE) {
                        assertNull(pane.getGraphic());
                    } else {
                        assertNotNull(pane.getGraphic());
                        ImageView icon = (ImageView) pane.getGraphic();
                        assertEquals(48, icon.getFitHeight());
                        assertEquals(48, icon.getFitWidth());
                    }
                } finally {
                    stage.close();
                }
            }
        });
    }
}

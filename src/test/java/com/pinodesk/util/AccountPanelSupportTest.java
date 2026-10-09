package com.pinodesk.util;

import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.junit.jupiter.api.Test;

import com.pinodesk.JavaFXTestBase;

class AccountPanelSupportTest extends JavaFXTestBase {

    @Test
    void installBindsProperties() {
        Pane root = new Pane();
        ToggleButton trigger = new ToggleButton();
        Pane panel = new VBox();
        Label user = new Label("Test User");
        Pane focusTarget = new Pane();

        AccountPanelSupport.install(root, trigger, panel, user, focusTarget);

        assertEquals("Test User", trigger.getAccessibleText());
        assertEquals("Test User", trigger.getTooltip().getText());

        trigger.setSelected(true);
        assertTrue(panel.isVisible());
        assertTrue(panel.isManaged());

        trigger.setSelected(false);
        assertFalse(panel.isVisible());
        assertFalse(panel.isManaged());
    }

    @Test
    void installRequestsFocusWhenSelected() {
        Pane root = new Pane();
        ToggleButton trigger = new ToggleButton();
        Pane panel = new VBox();
        Label user = new Label("Test User");
        Pane focusTarget = new Pane();

        AccountPanelSupport.install(root, trigger, panel, user, focusTarget);

        boolean[] focusRequested = { false };
        focusTarget.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                focusRequested[0] = true;
            }
        });

        trigger.setSelected(true);
        assertTrue(panel.isVisible());
    }

    @Test
    void installUpdatesTooltipWhenUserLabelChanges() {
        Pane root = new Pane();
        ToggleButton trigger = new ToggleButton();
        Pane panel = new VBox();
        Label user = new Label("Test User");
        Pane focusTarget = new Pane();

        AccountPanelSupport.install(root, trigger, panel, user, focusTarget);

        user.setText("New User");
        assertEquals("New User", trigger.getAccessibleText());
        assertEquals("New User", trigger.getTooltip().getText());
    }
}

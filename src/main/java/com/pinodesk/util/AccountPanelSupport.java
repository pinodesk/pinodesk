package com.pinodesk.util;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/** Shared, non-modal account panel behavior for desktop and cashier shells. */
public final class AccountPanelSupport {
    private AccountPanelSupport() {
    }

    public static void install(Pane root, ToggleButton trigger, Pane panel, Label user, Node focusTarget) {
        trigger.accessibleTextProperty().bind(user.textProperty());
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(user.textProperty());
        trigger.setTooltip(tooltip);
        panel.visibleProperty().bind(trigger.selectedProperty());
        panel.managedProperty().bind(trigger.selectedProperty());
        trigger.selectedProperty().addListener((observable, oldValue, selected) -> {
            if (selected) {
                focusTarget.requestFocus();
            }
        });
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (trigger.isSelected() && event.getTarget() instanceof Node target && !isInside(target, panel)
                    && !isInside(target, trigger)) {
                trigger.setSelected(false);
            }
        });
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (trigger.isSelected() && event.getCode() == KeyCode.ESCAPE) {
                trigger.setSelected(false);
                trigger.requestFocus();
                event.consume();
            }
        });
    }

    private static boolean isInside(Node node, Node container) {
        for (Node current = node; current != null; current = current.getParent()) {
            if (current == container) {
                return true;
            }
        }
        return false;
    }
}

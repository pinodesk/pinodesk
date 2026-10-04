package com.pinodesk.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import com.pinodesk.JavaFXTestBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

class MainControllerLayoutTest extends JavaFXTestBase {

    @Test
    void shellLoadsInBothLanguagesAndCollapsePreservesContentAndAccess(FxRobot robot) {
        robot.interact(() -> {
            for (String language : new String[] { "en", "id" }) {
                try {
                    MainController controller = spy(new MainController());
                    // Load the real shell without starting Spring or a database session.
                    doNothing().when(controller).initialize();
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/assets/templates/main.fxml"),
                            ResourceBundle.getBundle("pinodesk.lang", Locale.forLanguageTag(language)));
                    loader.setControllerFactory(type -> controller);
                    AnchorPane root = loader.load();
                    controller.initControlActions();
                    Scene scene = new Scene(root, 1024, 640);
                    root.applyCss();
                    root.layout();
                    VBox menu = (VBox) loader.getNamespace().get("vboxMenu");
                    AnchorPane content = (AnchorPane) loader.getNamespace().get("contentPane");
                    ScrollPane scroll = (ScrollPane) loader.getNamespace().get("menuScrollPane");
                    Button toggle = (Button) loader.getNamespace().get("btnToggleSidebar");
                    var greeting = content.getChildren().get(0);
                    savePreview(root, language + "-expanded");
                    var buttons = menu.getChildren().stream().filter(Button.class::isInstance).map(Button.class::cast)
                            .toList();
                    assertEquals(15, buttons.size());
                    for (Button button : buttons) {
                        assertNotNull(button.getGraphic());
                        assertNotNull(button.getOnAction());
                        assertEquals(button.getText(), button.getTooltip().getText());
                    }
                    // Simulate the existing permission filter removing an inaccessible menu.
                    Button removed = buttons.get(1);
                    menu.getChildren().remove(removed);
                    buttons.get(0).getStyleClass().add("btn-primary-active");
                    for (int i = 0; i < 3; i++) {
                        toggle.fire();
                        root.applyCss();
                        root.layout();
                        assertEquals(ContentDisplay.GRAPHIC_ONLY, toggle.getContentDisplay());
                        assertEquals(76.0, scroll.getWidth());
                        assertEquals(scroll.getWidth(), toggle.getWidth());
                        assertEquals(76.0, AnchorPane.getLeftAnchor(content));
                        assertEquals(ContentDisplay.GRAPHIC_ONLY, buttons.get(0).getContentDisplay());
                        assertEquals(controller.resources.getString("btn_expand_sidebar"), toggle.getAccessibleText());
                        if (i == 0) {
                            savePreview(root, language + "-collapsed");
                        }
                        toggle.fire();
                        root.applyCss();
                        root.layout();
                        assertEquals(ContentDisplay.LEFT, toggle.getContentDisplay());
                        assertEquals(controller.resources.getString("btn_collapse_sidebar"), toggle.getText());
                        assertEquals(250.0, scroll.getWidth());
                        assertEquals(scroll.getWidth(), toggle.getWidth());
                        assertEquals(ContentDisplay.LEFT, buttons.get(0).getContentDisplay());
                        assertSame(greeting, content.getChildren().get(0));
                        assertFalse(menu.getChildren().contains(removed));
                        assertTrue(buttons.get(0).getStyleClass().contains("btn-primary-active"));
                    }
                    assertEquals(ScrollPane.ScrollBarPolicy.NEVER, scroll.getVbarPolicy());
                    assertTrue(menu.getHeight() > scroll.getViewportBounds().getHeight());
                    assertEquals(1024, scene.getWidth());
                    ToggleButton profile = (ToggleButton) loader.getNamespace().get("profileMenu");
                    Button logout = (Button) loader.getNamespace().get("btnLogout");
                    VBox account = (VBox) loader.getNamespace().get("accountPanel");
                    assertFalse(account.isVisible());
                    profile.fire();
                    root.applyCss();
                    root.layout();
                    assertTrue(account.isVisible());
                    assertTrue(account.isManaged());
                    assertEquals(48.0, profile.getWidth());
                    assertEquals(profile.getWidth(), profile.getHeight());
                    assertEquals(
                            account.getWidth() - account.getInsets().getLeft() - account.getInsets().getRight(),
                            logout.getWidth());
                    assertEquals(buttons.get(0).getHeight(), logout.getHeight());
                    assertEquals(logout.getText(), ((javafx.scene.text.Text) logout.lookup(".text")).getText());
                    assertSame(account, logout.getParent());
                    savePreview(root, language + "-account-panel");
                    root.fireEvent(
                            new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false));
                    assertFalse(account.isVisible());
                    profile.fire();
                    assertTrue(account.isVisible());
                    root.fireEvent(
                            new MouseEvent(
                                    MouseEvent.MOUSE_PRESSED,
                                    0,
                                    0,
                                    0,
                                    0,
                                    MouseButton.PRIMARY,
                                    1,
                                    false,
                                    false,
                                    false,
                                    false,
                                    true,
                                    false,
                                    false,
                                    false,
                                    false,
                                    true,
                                    null));
                    assertFalse(account.isVisible());
                    assertEquals(controller.resources.getString("btn_logout"), logout.getText());
                    assertSame(loader.getNamespace().get("sidebarPane"), toggle.getParent().getParent());
                    doNothing().when(controller).onActionBtnLogout(any());
                    // Preserve the existing logout action without ending a real session.
                    logout.fire();
                    verify(controller).onActionBtnLogout(any());
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
            }
        });
    }

    private void savePreview(javafx.scene.Parent root, String name) throws java.io.IOException {
        String directory = System.getProperty("pinodesk.previewDir");
        if (directory == null) {
            return;
        }
        var snapshot = root.snapshot(null, null);
        var image = new java.awt.image.BufferedImage(
                (int) snapshot.getWidth(),
                (int) snapshot.getHeight(),
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        javax.imageio.ImageIO.write(image, "png", new java.io.File(directory, name + ".png"));
    }
}

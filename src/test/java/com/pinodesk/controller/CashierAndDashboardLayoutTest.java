package com.pinodesk.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import com.pinodesk.JavaFXTestBase;
import com.pinodesk.controller.transaction.sale.CashierController;
import com.pinodesk.util.DashboardTableScrollSupport;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

class CashierAndDashboardLayoutTest extends JavaFXTestBase {
    public static class LayoutCashierController extends CashierController {
        @FXML
        void initialize() {
            t = new com.pinodesk.pandora.utility.Translator(resources);
            initControlActions();
        }
    }

    @Test
    void cashierShellLoadsAndProfileDoesNotChangeCart(FxRobot robot) {
        robot.interact(() -> {
            for (String language : List.of("en", "id")) {
                try {
                    CashierController controller = new LayoutCashierController();
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/assets/templates/transaction/sale/cashier/main.fxml"),
                            ResourceBundle.getBundle("pinodesk.lang", Locale.forLanguageTag(language)));
                    loader.setControllerFactory(type -> controller);
                    AnchorPane root = loader.load();
                    new Scene(root, 1024, 640);
                    root.applyCss();
                    root.layout();
                    ToggleButton profile = (ToggleButton) loader.getNamespace().get("profileMenu");
                    VBox panel = (VBox) loader.getNamespace().get("accountPanel");
                    TableView<?> cart = (TableView<?>) loader.getNamespace().get("tblSaleProducts");
                    var items = cart.getItems();
                    profile.fire();
                    root.applyCss();
                    root.layout();
                    assertTrue(panel.isVisible());
                    assertEquals(48, profile.getWidth());
                    assertEquals(profile.getWidth(), profile.getHeight());
                    root.fireEvent(
                            new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false));
                    assertFalse(panel.isVisible());
                    assertSame(items, cart.getItems());
                    for (String id : List.of("btnAddProduct", "btnCustomer", "btnPay", "btnCancel")) {
                        assertNotNull(((Button) loader.getNamespace().get(id)).getOnAction());
                    }
                    savePreview(root, "cashier-" + language);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
            }
        });
    }

    @Test
    void dashboardFxmlInstallsScrollBehaviorOnAllSixTables(FxRobot robot) {
        robot.interact(() -> {
            for (String language : List.of("en", "id")) {
                try {
                    DashboardController controller = spy(new DashboardController());
                    doNothing().when(controller).initialize();
                    var service = mock(com.pinodesk.service.DashboardService.class);
                    when(service.getYears()).thenReturn(List.of(2026));
                    org.springframework.test.util.ReflectionTestUtils.setField(controller, "dashboardService", service);
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/assets/templates/dashboard.fxml"),
                            ResourceBundle.getBundle("pinodesk.lang", Locale.forLanguageTag(language)));
                    loader.setControllerFactory(type -> controller);
                    VBox root = loader.load();
                    controller.initControlActions();
                    new Scene(root, 1000, 640);
                    root.applyCss();
                    root.layout();
                    var tables = loader.getNamespace().values().stream().filter(TableView.class::isInstance)
                            .map(value -> (TableView<?>) value).toList();
                    assertEquals(6, tables.size());
                    var active = javafx.css.PseudoClass.getPseudoClass("scroll-active");
                    for (TableView<?> table : tables) {
                        assertTrue(table.getStyleClass().contains("dashboard-scroll-table"));
                        click(table);
                        assertTrue(table.getPseudoClassStates().contains(active));
                        assertEquals(1, tables.stream().filter(t -> t.getPseudoClassStates().contains(active)).count());
                    }
                    ScrollPane page = (ScrollPane) loader.getNamespace().get("dashboardScrollPane");
                    page.setVvalue(1);
                    root.layout();
                    savePreview(root, "dashboard-" + language);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
            }
        });
    }

    @Test
    void dashboardTablesRouteWheelToPageUntilClickedAndReleaseAtEdges(FxRobot robot) {
        robot.interact(() -> {
            TableView<String> table = new TableView<>(
                    FXCollections.observableArrayList(IntStream.range(0, 200).mapToObj(i -> "Row " + i).toList()));
            TableColumn<String, String> column = new TableColumn<>("Product");
            column.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()));
            table.getColumns().add(column);
            table.setMinHeight(180);
            table.setPrefHeight(180);
            table.setMaxHeight(180);
            Region space = new Region();
            space.setMinHeight(1400);
            VBox content = new VBox(table, space);
            ScrollPane page = new ScrollPane(content);
            page.setFitToWidth(true);
            StackPane root = new StackPane(page);
            new Scene(root, 600, 400);
            DashboardTableScrollSupport.install(page, List.of(table));
            root.applyCss();
            root.layout();
            Node flow = table.lookup(".virtual-flow");
            ScrollBar bar = table.lookupAll(".scroll-bar").stream().filter(ScrollBar.class::isInstance)
                    .map(ScrollBar.class::cast).filter(b -> b.getOrientation() == Orientation.VERTICAL).findFirst()
                    .orElseThrow();
            wheel(flow, -60);
            assertTrue(page.getVvalue() > 0, "Inactive table must scroll the page");
            assertEquals(0, bar.getValue(), "Inactive table must keep its row position");
            click(table);
            double pagePosition = page.getVvalue();
            wheel(flow, -60);
            assertTrue(bar.getValue() > 0, "Click activates table scrolling");
            assertEquals(pagePosition, page.getVvalue(), 0.00001);
            bar.setValue(bar.getMax());
            wheel(flow, -60);
            assertTrue(page.getVvalue() > pagePosition, "Bottom edge hands scroll back to page");
            pagePosition = page.getVvalue();
            bar.setValue(bar.getMin());
            wheel(flow, 60);
            assertTrue(page.getVvalue() < pagePosition, "Top edge hands scroll back to page");
            page.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false));
            assertFalse(table.getPseudoClassStates().contains(javafx.css.PseudoClass.getPseudoClass("scroll-active")));
            pagePosition = page.getVvalue();
            wheel(flow, -60);
            assertTrue(page.getVvalue() > pagePosition);
            click(table);
            table.fireEvent(
                    new MouseEvent(
                            MouseEvent.MOUSE_EXITED,
                            0,
                            0,
                            0,
                            0,
                            MouseButton.NONE,
                            0,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            null));
            assertFalse(table.getPseudoClassStates().contains(javafx.css.PseudoClass.getPseudoClass("scroll-active")));
        });
    }

    private static void click(Node node) {
        node.fireEvent(
                new MouseEvent(
                        MouseEvent.MOUSE_PRESSED,
                        5,
                        5,
                        5,
                        5,
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
    }

    private static void wheel(Node node, double delta) {
        node.fireEvent(
                new ScrollEvent(
                        ScrollEvent.SCROLL,
                        5,
                        5,
                        5,
                        5,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        0,
                        delta,
                        0,
                        delta,
                        ScrollEvent.HorizontalTextScrollUnits.NONE,
                        0,
                        ScrollEvent.VerticalTextScrollUnits.NONE,
                        0,
                        0,
                        null));
    }

    private static void savePreview(javafx.scene.Parent root, String name) throws java.io.IOException {
        String directory = System.getProperty("pinodesk.previewDir");
        if (directory == null)
            return;
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

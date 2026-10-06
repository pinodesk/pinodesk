package com.pinodesk.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import com.pinodesk.JavaFXTestBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;

class PaymentMethodLayoutTest extends JavaFXTestBase {
    public static class Main extends com.pinodesk.controller.settings.paymentmethod.PaymentMethodMainController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    public static class Form extends com.pinodesk.controller.settings.paymentmethod.PaymentMethodFormController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    public static class Filter extends com.pinodesk.controller.settings.paymentmethod.PaymentMethodFilterController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    public static class Add extends com.pinodesk.controller.transaction.sale.SaleAddController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    public static class Edit extends com.pinodesk.controller.transaction.sale.SaleEditController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    public static class Pay extends com.pinodesk.controller.transaction.sale.CashierPayController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    public static class SaleFilter extends com.pinodesk.controller.transaction.sale.SaleFilterController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    @Test
    void paymentFormsLoadInBothLanguagesWithoutOverlappingSellingMode(FxRobot robot) {
        robot.interact(() -> {
            for (String lang : List.of("en", "id")) {
                for (String path : List.of(
                        "settings/payment-method/main",
                        "settings/payment-method/add",
                        "settings/payment-method/edit",
                        "settings/payment-method/filter",
                        "transaction/sale/add",
                        "transaction/sale/edit",
                        "transaction/sale/filter",
                        "transaction/sale/cashier/pay")) {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/assets/templates/" + path + ".fxml"),
                                ResourceBundle.getBundle("pinodesk.lang", Locale.forLanguageTag(lang)));
                        loader.setControllerFactory(type -> switch (type.getSimpleName()) {
                            case "PaymentMethodMainController" -> new Main();
                            case "PaymentMethodFormController" -> new Form();
                            case "PaymentMethodFilterController" -> new Filter();
                            case "SaleAddController" -> new Add();
                            case "SaleEditController" -> new Edit();
                            case "CashierPayController" -> new Pay();
                            case "SaleFilterController" -> new SaleFilter();
                            default -> throw new IllegalStateException(type.getName());
                        });
                        Parent root = loader.load();
                        new Scene(root);
                        root.applyCss();
                        root.resize(root.prefWidth(-1), root.prefHeight(root.prefWidth(-1)));
                        root.layout();
                        if (path.startsWith("transaction")) {
                            if (path.contains("cashier")) {
                                FlowPane methods = (FlowPane) loader.getNamespace().get("paymentMethods");
                                assertNotNull(methods);
                            } else {
                                ComboBox<?> method = (ComboBox<?>) loader.getNamespace().get("cbPaymentMethod");
                                assertNotNull(method);
                                assertTrue(method.getWidth() > 100, path + " width=" + method.getWidth());
                                if (method.getParent() instanceof GridPane) {
                                    assertEquals(1, GridPane.getColumnIndex(method.getParent()));
                                }
                            }
                        }
                        String folder = System.getProperty("pinodesk.previewDir");
                        if (folder != null) {
                            java.nio.file.Path output = java.nio.file.Path
                                    .of(folder, path.replace('/', '-') + "-" + lang + ".png");
                            java.nio.file.Files.createDirectories(output.getParent());
                            var snapshot = root.snapshot(null, null);
                            var image = new java.awt.image.BufferedImage(
                                    (int) snapshot.getWidth(),
                                    (int) snapshot.getHeight(),
                                    java.awt.image.BufferedImage.TYPE_INT_ARGB);
                            for (int y = 0; y < image.getHeight(); y++)
                                for (int x = 0; x < image.getWidth(); x++)
                                    image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
                            javax.imageio.ImageIO.write(image, "png", output.toFile());
                        }
                    } catch (Exception ex) {
                        throw new AssertionError(path + " " + lang, ex);
                    }
                }
            }
        });
    }
}

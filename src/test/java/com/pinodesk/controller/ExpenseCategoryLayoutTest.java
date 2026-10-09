package com.pinodesk.controller;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import com.pinodesk.JavaFXTestBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

class ExpenseCategoryLayoutTest extends JavaFXTestBase {
    public static class Main extends com.pinodesk.controller.settings.expensecategory.ExpenseCategoryMainController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    public static class Form extends com.pinodesk.controller.settings.expensecategory.ExpenseCategoryFormController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    public static class Filter
            extends com.pinodesk.controller.settings.expensecategory.ExpenseCategoryFilterController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    @Test
    void expenseCategoryFormsLoadInBothLanguages(FxRobot robot) {
        robot.interact(() -> {
            for (String lang : List.of("en", "id")) {
                for (String path : List.of(
                        "settings/expense-category/main",
                        "settings/expense-category/add",
                        "settings/expense-category/edit",
                        "settings/expense-category/filter")) {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/assets/templates/" + path + ".fxml"),
                                ResourceBundle.getBundle("pinodesk.lang", Locale.forLanguageTag(lang)));
                        loader.setControllerFactory(type -> switch (type.getSimpleName()) {
                            case "ExpenseCategoryMainController" -> new Main();
                            case "ExpenseCategoryFormController" -> new Form();
                            case "ExpenseCategoryFilterController" -> new Filter();
                            default -> throw new IllegalStateException(type.getName());
                        });
                        Parent root = loader.load();
                        new Scene(root);
                        root.applyCss();
                        root.resize(root.prefWidth(-1), root.prefHeight(root.prefWidth(-1)));
                        root.layout();
                    } catch (Exception ex) {
                        throw new AssertionError(path + " " + lang, ex);
                    }
                }
            }
        });
    }
}

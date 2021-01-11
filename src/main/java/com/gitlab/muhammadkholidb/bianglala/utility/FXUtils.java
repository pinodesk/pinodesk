package com.gitlab.muhammadkholidb.bianglala.utility;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;

import com.gitlab.muhammadkholidb.bianglala.Bianglala;
import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.Page;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXUtils {

    private FXUtils() {
    }

    public static void show(Page page, boolean resizeable) throws IOException {
        Pane container = PageLoader.load(page);
        Scene scene = new Scene(container);
        Stage stage = new Stage();
        Arrays.stream(Bianglala.ICON_PATHS)
                .forEach(path -> stage.getIcons().add(new Image(FXUtils.class.getResourceAsStream(path))));
        stage.setResizable(resizeable);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(CommonConstants.APP_TITLE);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public static void show(Page page) throws IOException {
        show(page, true);
    }

    public static Optional<ButtonType> showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Bianglala");
        alert.setContentText(message);
        return alert.showAndWait();
    }

    public static Optional<ButtonType> showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bianglala");
        alert.setContentText(message);
        return alert.showAndWait();
    }

    // From https://code.makery.ch/blog/javafx-dialogs-official/
    public static void showErrorDialog(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(StringUtils.defaultIfBlank(ex.getMessage(), ex.toString()));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Stack trace:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

}

package com.gitlab.muhammadkholidb.bianglala.utility;

import java.io.IOException;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogBox {

    private static Object result;

    public static Object show(String title, String viewName) throws IOException {
        VBox page = ViewLoader.load(viewName);
        Scene scene = new Scene(page);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.showAndWait();
        return result;
    }

    public static Object show(String title, String viewName, boolean resizable) throws IOException {
        VBox page = ViewLoader.load(viewName);
        Scene scene = new Scene(page);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(scene);
        if (!resizable) {
            stage.sizeToScene();
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        }
        stage.showAndWait();
        return result;
    }

}

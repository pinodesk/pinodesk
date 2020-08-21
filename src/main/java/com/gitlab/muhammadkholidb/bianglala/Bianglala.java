package com.gitlab.muhammadkholidb.bianglala;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.utility.SpringFXMLLoader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Bianglala extends Application {

    private static final SpringFXMLLoader loader = new SpringFXMLLoader();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(CommonConstants.APP_TITLE);
        AnchorPane page = (AnchorPane) loader.load(getClass().getResource("Cashier.fxml"));
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}

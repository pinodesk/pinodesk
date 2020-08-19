package com.gitlab.muhammadkholidb.bianglala;

import java.io.IOException;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Bianglala extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(CommonConstants.APP_TITLE);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Cashier.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

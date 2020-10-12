package com.gitlab.muhammadkholidb.bianglala;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.ViewConstants;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.ConfigurationHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.ViewLoader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Bianglala extends Application {

    private void initAll() {
        ApplicationContextHolder.init();
        ConfigurationHolder.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initAll();
        AnchorPane page = ViewLoader.load(ViewConstants.MAIN);
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setTitle(CommonConstants.APP_TITLE);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/images/icon.png")));
    }

    public static void main(String[] args) {
        launch(args);
    }

}

package com.gitlab.muhammadkholidb.bianglala;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.Page;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.ConfigurationHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.PageLoader;
import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Bianglala extends Application {

    public static String[] ICON_PATHS = new String[]{
        "/assets/images/bianglala-icon-128.png",
        "/assets/images/bianglala-icon-64.png",
        "/assets/images/bianglala-icon-32.png"
    };

    @Override
    public void init() throws Exception {
        super.init(); 
        ApplicationContextHolder.init();
        ConfigurationHolder.init();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane page = PageLoader.load(Page.MAIN);
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setTitle(CommonConstants.APP_TITLE);
        Arrays.stream(ICON_PATHS).forEach(path -> {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(path)));
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}

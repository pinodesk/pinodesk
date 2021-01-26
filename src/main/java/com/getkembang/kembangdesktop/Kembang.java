package com.getkembang.kembangdesktop;

import java.util.Arrays;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.utility.ApplicationContextHolder;
import com.getkembang.kembangdesktop.utility.PageLoader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Kembang extends Application {

    public static final String[] ICON_PATHS = new String[] { 
            "/assets/images/kembang-sq-128.png",
            "/assets/images/kembang-sq-64.png", 
            "/assets/images/kembang-sq-32.png" };

    @Override
    public void init() throws Exception {
        super.init();
        ApplicationContextHolder.init();
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
        Arrays.stream(ICON_PATHS)
                .forEach(path -> primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(path))));
    }

    public static void main(String[] args) {
        launch(args);
    }

}

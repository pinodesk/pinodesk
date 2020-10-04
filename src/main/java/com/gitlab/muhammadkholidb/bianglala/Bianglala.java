package com.gitlab.muhammadkholidb.bianglala;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.ViewConstants;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.ViewLoader;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bianglala extends Application {

    private void initSpring() {
        ApplicationContextHolder
                .init(new AnnotationConfigApplicationContext(SpringConfig.class));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initSpring();
        primaryStage.setTitle(CommonConstants.APP_TITLE);
        AnchorPane page = ViewLoader.load(ViewConstants.MAIN);
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }

}

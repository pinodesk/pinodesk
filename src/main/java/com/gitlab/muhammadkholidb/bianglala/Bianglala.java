package com.gitlab.muhammadkholidb.bianglala;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.ViewConstants;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.ViewLoader;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.config.JdbcTemplateHelperConfig;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Bianglala extends Application {

    private void initSpring() {
        ApplicationContextHolder
                .init(new AnnotationConfigApplicationContext(SpringConfig.class, JdbcTemplateHelperConfig.class));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initSpring();
        primaryStage.setTitle(CommonConstants.APP_TITLE);
        AnchorPane page = (AnchorPane) ViewLoader.load(ViewConstants.CASHIER);
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

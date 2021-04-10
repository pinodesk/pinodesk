package com.getkembang.kembangdesktop;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.service.ConfigurationService;
import com.getkembang.kembangdesktop.utility.ApplicationContextHolder;
import com.gitlab.muhammadkholidb.dior.utility.PageLoader;

import org.apache.commons.lang3.StringUtils;

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
        PageLoader.init(CommonConstants.PAGE_TEMPLATE_DIR, this::getDefaultResourceBundle);
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

    private ResourceBundle getDefaultResourceBundle() {
        ConfigurationService configurationService = ApplicationContextHolder.getApplicationContext()
                .getBean(ConfigurationService.class);
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        Locale locale = StringUtils.isBlank(languageCode) ? CommonConstants.ENGLISH : new Locale(languageCode);
        return ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, locale);
    }

}

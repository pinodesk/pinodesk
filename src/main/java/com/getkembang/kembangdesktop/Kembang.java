package com.getkembang.kembangdesktop;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.service.ConfigurationService;
import com.getkembang.kembangdesktop.utility.SpringUtils;
import com.gitlab.muhammadkholidb.pandora.utility.PageContext;
import com.gitlab.muhammadkholidb.pandora.utility.PageLoader;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Kembang extends Application {

    // @formatter:off
    public static final String[] ICON_PATHS = new String[] { 
            "/assets/images/kembang-sq-128.png",
            "/assets/images/kembang-sq-64.png", 
            "/assets/images/kembang-sq-32.png" };
    // @formatter:on

    public static Stage primaryStage;

    @Override
    public void init() throws Exception {
        super.init();
        SpringUtils.init(KembangConfig.class);
        PageLoader.init(CommonConstants.PAGE_TEMPLATE_DIR, this::getDefaultResourceBundle);
        StageUtils.init(CommonConstants.APP_TITLE, ICON_PATHS);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setPrimaryStage(primaryStage);
        loadMainPage();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void loadMainPage() throws IOException {
        loadMainPage(null, null, false);
    }

    private static void loadMainPage(Double height, Double width, boolean maximized) throws IOException {
        PageContext pageContext = PageLoader.load(Page.MAIN);
        Scene scene = new Scene(pageContext.getRoot());
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        if (height != null) {
            primaryStage.setHeight(height);
        }
        if (width != null) {
            primaryStage.setWidth(width);
        }
        primaryStage.setMaximized(maximized);
        primaryStage.setTitle(CommonConstants.APP_TITLE);
        for (String path : ICON_PATHS) {
            primaryStage.getIcons().add(new Image(Kembang.class.getResourceAsStream(path)));
        }
        primaryStage.show();
    }

    private ResourceBundle getDefaultResourceBundle() {
        ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        Locale locale = StringUtils.isBlank(languageCode) ? CommonConstants.ENGLISH : new Locale(languageCode);
        return ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, locale);
    }

    private static void setPrimaryStage(Stage primaryStage) {
        Kembang.primaryStage = primaryStage;
    }

    public static void reload() throws IOException {
        loadMainPage(primaryStage.getHeight(), primaryStage.getWidth(), primaryStage.isMaximized());
    }

}

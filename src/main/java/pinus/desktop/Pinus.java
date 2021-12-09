package pinus.desktop;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.pandora.utility.PageContext;
import com.gitlab.muhammadkholidb.pandora.utility.PageLoader;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.Page;
import pinus.desktop.service.ConfigurationService;
import pinus.desktop.utility.SpringUtils;

public class Pinus extends Application {

    public static Stage primaryStage;

    @Override
    public void init() throws Exception {
        super.init();
        SpringUtils.init(PinusConfig.class);
        PageLoader.init(CommonConstants.PAGE_TEMPLATE_DIR, this::getDefaultResourceBundle);
        StageUtils.init(CommonConstants.APP_TITLE, new String[] {});
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
        primaryStage.show();
    }

    private ResourceBundle getDefaultResourceBundle() {
        ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, new Locale(languageCode));
    }

    private static void setPrimaryStage(Stage primaryStage) {
        Pinus.primaryStage = primaryStage;
    }

    public static void reload() throws IOException {
        loadMainPage(primaryStage.getHeight(), primaryStage.getWidth(), primaryStage.isMaximized());
    }

}

package tosca.desktop;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import tosca.desktop.constant.CommonConstants;
import tosca.desktop.constant.ConfigurationConstants;
import tosca.desktop.constant.Page;
import tosca.desktop.service.ConfigurationService;
import tosca.desktop.utility.SpringUtils;
import com.gitlab.muhammadkholidb.pandora.utility.PageContext;
import com.gitlab.muhammadkholidb.pandora.utility.PageLoader;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Tosca extends Application {

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
        SpringUtils.init(ToscaConfig.class);
        PageLoader.init(CommonConstants.PAGE_TEMPLATE_DIR, this::getDefaultResourceBundle);
        StageUtils.init(CommonConstants.APP_TITLE, ICON_PATHS);
        printSystemProperties();
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
            primaryStage.getIcons().add(new Image(Tosca.class.getResourceAsStream(path)));
        }
        primaryStage.show();
    }

    private ResourceBundle getDefaultResourceBundle() {
        ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, new Locale(languageCode));
    }

    private static void setPrimaryStage(Stage primaryStage) {
        Tosca.primaryStage = primaryStage;
    }

    public static void reload() throws IOException {
        loadMainPage(primaryStage.getHeight(), primaryStage.getWidth(), primaryStage.isMaximized());
    }

    private void printSystemProperties() {
        System.out.println("System properties:");
        for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
            System.out.printf("%40s: %s\n", entry.getKey(), entry.getValue());
        }
        System.out.println();
        System.out.println("System environment:");
        for(Entry<String, String> entry : System.getenv().entrySet()) {
            System.out.printf("%40s: %s\n", entry.getKey(), entry.getValue());
        }
    }

}

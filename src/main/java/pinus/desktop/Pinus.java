package pinus.desktop;

import java.io.IOException;

import com.gitlab.muhammadkholidb.pandora.utility.PageContext;
import com.gitlab.muhammadkholidb.pandora.utility.PageLoader;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.Page;
import pinus.desktop.util.ResourceBundleUtils;

public class Pinus extends Application {

    private static Stage primaryStage;

    @Override
    public void init() throws Exception {
        super.init();
        PageLoader.init(CommonConstants.PAGE_TEMPLATE_DIR, ResourceBundleUtils::getDefaultResourceBundle);
        StageUtils.init(CommonConstants.APP_TITLE, new String[] {});
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setPrimaryStage(primaryStage);
        StageUtils.modal(Page.SPLASH, StageStyle.UNDECORATED);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void loadMainPage() throws IOException {
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

    private static void setPrimaryStage(Stage primaryStage) {
        Pinus.primaryStage = primaryStage;
    }

    public static void reload() throws IOException {
        loadMainPage(primaryStage.getHeight(), primaryStage.getWidth(), primaryStage.isMaximized());
    }

}

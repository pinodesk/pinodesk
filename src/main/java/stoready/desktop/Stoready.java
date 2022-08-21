package stoready.desktop;

import com.gitlab.muhammadkholidb.pandora.utility.PageLoader;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.Page;
import stoready.desktop.util.ResourceBundleUtils;

public class Stoready extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        PageLoader.init(CommonConstants.PAGE_TEMPLATE_DIR, ResourceBundleUtils::getDefaultResourceBundle);
        StageUtils.init(CommonConstants.APP_TITLE, CommonConstants.APP_ICON_PATHS);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageUtils.modal(Page.SPLASH, StageStyle.UNDECORATED);
    }

    public static void main(String[] args) {
        launch(args);
    }

}

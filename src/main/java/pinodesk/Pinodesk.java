package pinodesk;

import com.gitlab.mudiasoft.pandora.utility.PageLoader;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.Page;
import pinodesk.util.ResourceBundleUtils;

public class Pinodesk extends Application {

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

package pinodesk;

import java.io.OutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;

import com.pinodesk.pandora.utility.PageLoader;
import com.pinodesk.pandora.utility.StageUtils;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.Page;
import pinodesk.util.ResourceBundleUtils;

@Slf4j
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
        handleStdout();
        launch(args);
    }

    private static void handleStdout() {
        System.setErr(new PrintStream(new LoggingOutputStream(log)));
    }

    private static class LoggingOutputStream extends OutputStream {
        private final Logger logger;
        private final StringBuilder buffer = new StringBuilder();

        public LoggingOutputStream(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void write(int b) {
            if (b == '\n') {
                logger.debug(buffer.toString());
                buffer.setLength(0);
            } else {
                buffer.append((char) b);
            }
        }
    }
}

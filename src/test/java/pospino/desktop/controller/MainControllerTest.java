package pospino.desktop.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import com.gitlab.mudiasoft.pandora.utility.PageLoader;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;

import javafx.stage.Stage;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.SimpleStatus;
import pospino.desktop.constant.UserStatus;
import pospino.desktop.service.ConfigurationService;
import pospino.desktop.service.SessionService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.UserAddVM;

@ExtendWith(ApplicationExtension.class)
public class MainControllerTest {

    @Start
    void start(Stage stage) {
        SpringUtils.init(ControllerTestConfig.class);
        initialSetupAndLogin();
        ResourceBundle rb = ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, Locale.ENGLISH);
        PageLoader.init(CommonConstants.PAGE_TEMPLATE_DIR, () -> rb);
        StageUtils.init(CommonConstants.APP_TITLE, CommonConstants.APP_ICON_PATHS);
        StageUtils.open(Page.MAIN);
    }

    private void initialSetupAndLogin() {
        ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
        SessionService sessionService = SpringUtils.getBean(SessionService.class);

        Map<String, String> map = new HashMap<>();
        map.put(ConfigurationConstants.STORE_NAME, "Downy Store");
        map.put(ConfigurationConstants.STORE_ADDRESS, "Downy Street");
        map.put(ConfigurationConstants.INITIAL_SETUP_DONE, SimpleStatus.YES.toString());
        UserAddVM userAdd = new UserAddVM();
        userAdd.setFullName("Downy");
        userAdd.setPassword("123456");
        userAdd.setStatus(UserStatus.ACTIVE);
        userAdd.setUserGroupId(CommonConstants.USER_GROUP_ID_ADMINISTRATOR);
        userAdd.setUsername("downy");
        configurationService.saveIntialSetup(map, userAdd);

        sessionService.login("downy", "123456");
    }

    @Test
    void testMainPage(FxRobot robot) {
        FxAssert.verifyThat("#lblStoreName", LabeledMatchers.hasText("Downy Store"));
        FxAssert.verifyThat("#lblUser", LabeledMatchers.hasText("Downy"));
        FxAssert.verifyThat("#lblUserGroup", LabeledMatchers.hasText("Administrator"));
    }

}

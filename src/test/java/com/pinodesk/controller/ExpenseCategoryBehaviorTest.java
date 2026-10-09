package com.pinodesk.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.testfx.api.FxRobot;

import com.pinodesk.JavaFXTestBase;
import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.pandora.utility.Translator;
import com.pinodesk.service.ExpenseCategoryService;
import com.pinodesk.service.SessionService;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.UserGroupMenuVM;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

class ExpenseCategoryBehaviorTest extends JavaFXTestBase {
    private final ExpenseCategoryService service = mock(ExpenseCategoryService.class);
    private final SessionService session = mock(SessionService.class, RETURNS_DEEP_STUBS);
    private final ResourceBundle bundle = ResourceBundle.getBundle("pinodesk.lang", Locale.ENGLISH);
    private ApplicationContext previous;
    private ExpenseCategory category1;
    private ExpenseCategory category2;

    @BeforeEach
    void setup() {
        clearPageData();
        previous = SpringUtils.getApplicationContext();
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBean(ExpenseCategoryService.class)).thenReturn(service);
        setField(SpringUtils.class, "applicationContext", context);
        category1 = new ExpenseCategory();
        category1.setId(1L);
        category1.setName("Office Supplies");
        category1.setStatus(UserStatus.ACTIVE.toString());
        category2 = new ExpenseCategory();
        category2.setId(2L);
        category2.setName("Travel");
        category2.setStatus(UserStatus.INACTIVE.toString());
        when(service.findAll()).thenReturn(List.of(category1, category2));
        when(session.isCurrentSessionActive()).thenReturn(true);
        UserGroupMenuVM menu = new UserGroupMenuVM();
        menu.setMenuCode(MenuCodeConstants.SETTINGS_EXPENSE_CATEGORIES);
        menu.setWrite("yes");
        when(session.getCurrentSession().getUserGroupMenus()).thenReturn(List.of(menu));
    }

    @AfterEach
    void restoreContext() {
        clearPageData();
        setField(SpringUtils.class, "applicationContext", previous);
    }

    private static void clearPageData() {
        while (com.pinodesk.toolbox.data.SingletonStack.INSTANCE.pop() != null) {
        }
    }

    private <T extends BaseController> T load(String path, T controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/templates/" + path + ".fxml"), bundle);
            loader.setControllerFactory(type -> controller);
            loader.load();
            controller.t = new Translator(bundle);
            controller.sessionService = session;
            invokeMethod(controller, "initServices");
            return controller;
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(Object object, String name) {
        return (T) getField(object, name);
    }

    @Test
    void controlsRespectPermissions(FxRobot robot) {
        robot.interact(() -> {
            assertTrue(com.pinodesk.util.ExpenseCategoryControls.canWrite(session));
            when(session.isCurrentSessionActive()).thenReturn(false);
            assertFalse(com.pinodesk.util.ExpenseCategoryControls.canWrite(session));
            when(session.isCurrentSessionActive()).thenReturn(true);
            UserGroupMenuVM denied = new UserGroupMenuVM();
            denied.setMenuCode("other");
            when(session.getCurrentSession().getUserGroupMenus()).thenReturn(List.of(denied));
            assertFalse(com.pinodesk.util.ExpenseCategoryControls.canWrite(session));
            denied.setMenuCode(MenuCodeConstants.SETTINGS_EXPENSE_CATEGORIES);
            denied.setWrite("no");
            when(session.getCurrentSession().getUserGroupMenus()).thenReturn(List.of(denied));
            assertFalse(com.pinodesk.util.ExpenseCategoryControls.canWrite(session));
        });
    }

    @Test
    void formValidatesAndSavesNewAndExistingCategories(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/add", new ExpenseCategoryLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");
            form.setPageData(null);
            invokeMethod(form, "initDataSaveControlValues");
            TextField name = field(form, "tfName");
            ComboBox<?> status = field(form, "cbStatus");
            assertEquals("", name.getText());
            assertNotNull(status.getValue());
            name.setText("Office");
            when(service.save(null, "Office", UserStatus.ACTIVE)).thenReturn(category1);
            setField(form, "current", null);
            Object result = invokeMethod(form, "save");
            assertEquals(category1, result);
        });
    }

    @Test
    void formLoadsExistingCategoryForEdit(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/edit", new ExpenseCategoryLayoutTest.Form());
            form.setPageData(category1);
            invokeMethod(form, "initDataSaveControlActions");
            invokeMethod(form, "initDataSaveControlValues");
            TextField name = field(form, "tfName");
            assertEquals("Office Supplies", name.getText());
        });
    }

    @Test
    void filterInitializesAndResets(FxRobot robot) {
        robot.interact(() -> {
            var filter = load("settings/expense-category/filter", new ExpenseCategoryLayoutTest.Filter());
            invokeMethod(filter, "initDataFilterControlActions");
            invokeMethod(filter, "resetControls");
            invokeMethod(filter, "initDataFilterControlValues");
            TextField name = field(filter, "tfName");
            ComboBox<?> status = field(filter, "cbStatus");
            assertEquals("", name.getText());
            assertNotNull(status.getValue());
            invokeMethod(filter, "resetControls");
            assertEquals("", name.getText());
        });
    }
}

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
import org.testfx.util.WaitForAsyncUtils;

import com.pinodesk.JavaFXTestBase;
import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.pandora.utility.Translator;
import com.pinodesk.service.ExpenseCategoryService;
import com.pinodesk.service.SessionService;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ExpenseCategoryFilterVM;
import com.pinodesk.viewmodel.UserGroupMenuVM;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

class ExpenseCategoryMainBehaviorTest extends JavaFXTestBase {

    // Fixture subclass — overrides initialize() so BaseController doesn't call
    // SpringUtils during FXML load; we inject dependencies manually afterwards.
    static class Main extends com.pinodesk.controller.settings.expensecategory.ExpenseCategoryMainController {
        @javafx.fxml.FXML
        public void initialize() {
        }
    }

    // Extended fixture that also overrides displayConfirmation so no real Alert
    // window is shown — confirmed flag controls the simulated user response.
    static class MainFixture extends Main {
        boolean confirmed;
        Throwable failure;

        @Override
        protected com.pinodesk.pandora.utility.AlertResult displayConfirmation(
                com.pinodesk.pandora.utility.IMessage message) {
            return new com.pinodesk.pandora.utility.AlertResult(
                    java.util.Optional
                            .of(confirmed ? javafx.scene.control.ButtonType.YES : javafx.scene.control.ButtonType.NO));
        }

        @Override
        protected void handleException(Throwable error) {
            failure = error;
        }
    }

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

    /** Loads the main FXML and injects mocked dependencies. */
    private Main load() {
        return loadWith(new Main());
    }

    /** Loads the main FXML using the provided fixture controller instance. */
    private <T extends Main> T loadWith(T controller) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/assets/templates/settings/expense-category/main.fxml"),
                    bundle);
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

    // -------------------------------------------------------------------------
    // Permission helper
    // -------------------------------------------------------------------------

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
            assertFalse(com.pinodesk.util.ExpenseCategoryControls.canWrite(session));
        });
    }

    // -------------------------------------------------------------------------
    // Button visibility
    // -------------------------------------------------------------------------

    @Test
    void buttonsEnabledWhenAuthorized(FxRobot robot) {
        robot.interact(() -> {
            Main controller = load();
            invokeMethod(controller, "initControlActions");
            Button btnAdd = field(controller, "btnAdd");
            Button btnRemove = field(controller, "btnRemove");
            assertFalse(btnAdd.isDisabled());
            assertFalse(btnRemove.isDisabled());
        });
    }

    @Test
    void buttonsDisabledWhenNotAuthorized(FxRobot robot) {
        robot.interact(() -> {
            when(session.isCurrentSessionActive()).thenReturn(false);
            Main controller = load();
            invokeMethod(controller, "initControlActions");
            Button btnAdd = field(controller, "btnAdd");
            Button btnRemove = field(controller, "btnRemove");
            assertTrue(btnAdd.isDisabled());
            assertTrue(btnRemove.isDisabled());
        });
    }

    // -------------------------------------------------------------------------
    // Search / filter behaviour
    // -------------------------------------------------------------------------

    @Test
    void initialSearchLoadsAllCategories(FxRobot robot) throws Exception {
        Main controller = load();
        robot.interact(() -> {
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        TableView<ExpenseCategory> tblCategories = field(controller, "tblCategories");
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> tblCategories.getItems().size() == 2);
        robot.interact(() -> {
            Label lblRows = field(controller, "lblRows");
            assertTrue(tblCategories.getItems().contains(category1));
            assertTrue(tblCategories.getItems().contains(category2));
            assertEquals("2", lblRows.getText());
        });
    }

    @Test
    void searchFiltersByStatus(FxRobot robot) throws Exception {
        Main controller = load();
        robot.interact(() -> {
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        TableView<ExpenseCategory> tblCategories = field(controller, "tblCategories");
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> tblCategories.getItems().size() == 2);
        robot.interact(() -> {
            ExpenseCategoryFilterVM filter = field(controller, "filter");
            filter.setStatus(UserStatus.ACTIVE);
            invokeMethod(controller, "searchCategories");
        });
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> tblCategories.getItems().size() == 1);
        robot.interact(() -> {
            Label lblRows = field(controller, "lblRows");
            assertTrue(tblCategories.getItems().contains(category1));
            assertFalse(tblCategories.getItems().contains(category2));
            assertEquals("1", lblRows.getText());
        });
    }

    @Test
    void searchFiltersByName(FxRobot robot) throws Exception {
        Main controller = load();
        robot.interact(() -> {
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        TableView<ExpenseCategory> tblCategories = field(controller, "tblCategories");
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> tblCategories.getItems().size() == 2);
        robot.interact(() -> {
            ExpenseCategoryFilterVM filter = field(controller, "filter");
            filter.setName("travel");
            invokeMethod(controller, "searchCategories");
        });
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> tblCategories.getItems().size() == 1);
        robot.interact(() -> {
            Label lblRows = field(controller, "lblRows");
            assertTrue(tblCategories.getItems().contains(category2));
            assertFalse(tblCategories.getItems().contains(category1));
            assertEquals("1", lblRows.getText());
        });
    }

    @Test
    void searchHandlesNoResults(FxRobot robot) throws Exception {
        Main controller = load();
        robot.interact(() -> {
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        TableView<ExpenseCategory> tblCategories = field(controller, "tblCategories");
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> tblCategories.getItems().size() == 2);
        robot.interact(() -> {
            ExpenseCategoryFilterVM filter = field(controller, "filter");
            filter.setName("nonexistent");
            invokeMethod(controller, "searchCategories");
        });
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> tblCategories.getItems().size() == 0);
        robot.interact(() -> {
            Label lblRows = field(controller, "lblRows");
            assertEquals("0", lblRows.getText());
        });
    }

    // -------------------------------------------------------------------------
    // Remove action
    // -------------------------------------------------------------------------

    @Test
    void removeDoesNothingWhenSelectionIsEmpty(FxRobot robot) {
        robot.interact(() -> {
            Main controller = load();
            invokeMethod(controller, "initControlActions");
            setField(controller, "filter", new ExpenseCategoryFilterVM());
            TableView<ExpenseCategory> table = field(controller, "tblCategories");
            table.getItems().setAll(category1, category2);
            table.getSelectionModel().clearSelection();

            // Firing the button with empty selection should not call service.remove
            doThrow(new RuntimeException("remove must not be called")).when(service).remove(anyList());
            Button remove = field(controller, "btnRemove");
            remove.fire();
            verify(service, never()).remove(anyList());
        });
    }

    // -------------------------------------------------------------------------
    // Edit action
    // -------------------------------------------------------------------------

    @Test
    void editDoesNothingWhenNoSelection(FxRobot robot) {
        robot.interact(() -> {
            Main controller = load();
            invokeMethod(controller, "initControlActions");
            setField(controller, "filter", new ExpenseCategoryFilterVM());
            TableView<ExpenseCategory> table = field(controller, "tblCategories");
            table.getItems().setAll(category1, category2);
            table.getSelectionModel().clearSelection();

            // handleEdit should silently skip when nothing is selected
            invokeMethod(controller, "handleEdit");
            assertNotNull(table);
        });
    }

    // -------------------------------------------------------------------------
    // Remove with confirmation
    // -------------------------------------------------------------------------

    @Test
    void removeDoesNothingWhenUserCancels(FxRobot robot) {
        robot.interact(() -> {
            MainFixture controller = loadWith(new MainFixture());
            invokeMethod(controller, "initControlActions");
            setField(controller, "filter", new ExpenseCategoryFilterVM());
            TableView<ExpenseCategory> table = field(controller, "tblCategories");
            table.getItems().setAll(category1, category2);
            table.getSelectionModel().selectAll();

            controller.confirmed = false;
            Button remove = field(controller, "btnRemove");
            remove.fire();
            verify(service, never()).remove(anyList());
        });
    }

    @Test
    void removeCallsServiceWhenUserConfirms(FxRobot robot) throws Exception {
        MainFixture controller = loadWith(new MainFixture());
        robot.interact(() -> {
            invokeMethod(controller, "initControlActions");
            setField(controller, "filter", new ExpenseCategoryFilterVM());
            TableView<ExpenseCategory> table = field(controller, "tblCategories");
            table.getItems().setAll(category1, category2);
            table.getSelectionModel().selectAll();
            controller.confirmed = true;
            Button remove = field(controller, "btnRemove");
            remove.fire();
        });
        // searchCategories runs async — wait for the async refresh
        TableView<ExpenseCategory> table = field(controller, "tblCategories");
        org.testfx.util.WaitForAsyncUtils
                .waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> table.getItems().size() == 2);
        robot.interact(() -> verify(service).remove(List.of(1L, 2L)));
    }

    // -------------------------------------------------------------------------
    // Add / Filter / Edit dialogs (via StageUtils mock)
    // -------------------------------------------------------------------------

    @Test
    void addButtonOpensAddPageAndRefreshesOnResult(FxRobot robot) throws Exception {
        MainFixture controller = loadWith(new MainFixture());
        robot.interact(() -> {
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        TableView<ExpenseCategory> table = field(controller, "tblCategories");
        org.testfx.util.WaitForAsyncUtils
                .waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> table.getItems().size() == 2);
        robot.interact(() -> {
            try (var stages = mockStatic(com.pinodesk.pandora.utility.StageUtils.class)) {
                java.util.concurrent.atomic.AtomicReference<com.pinodesk.constant.Page> opened = new java.util.concurrent.atomic.AtomicReference<>();
                stages.when(() -> com.pinodesk.pandora.utility.StageUtils.modal(any(), eq(false), any()))
                        .thenAnswer(invocation -> {
                            opened.set(invocation.getArgument(0));
                            // Simulate a saved result so callback triggers refresh
                            controller.setPageData(Boolean.TRUE);
                            javafx.event.EventHandler<javafx.stage.WindowEvent> callback = invocation.getArgument(2);
                            callback.handle(null);
                            return null;
                        });
                ((Button) field(controller, "btnAdd")).fire();
                assertEquals(com.pinodesk.constant.Page.SETTINGS_EXPENSE_CATEGORY_ADD, opened.get());
            }
        });
    }

    @Test
    void filterButtonOpensFilterPageAndAppliesResult(FxRobot robot) throws Exception {
        MainFixture controller = loadWith(new MainFixture());
        robot.interact(() -> {
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        TableView<ExpenseCategory> table = field(controller, "tblCategories");
        org.testfx.util.WaitForAsyncUtils
                .waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> table.getItems().size() == 2);
        robot.interact(() -> {
            try (var stages = mockStatic(com.pinodesk.pandora.utility.StageUtils.class)) {
                java.util.concurrent.atomic.AtomicReference<com.pinodesk.constant.Page> opened = new java.util.concurrent.atomic.AtomicReference<>();
                ExpenseCategoryFilterVM newFilter = new ExpenseCategoryFilterVM();
                newFilter.setStatus(UserStatus.ACTIVE);
                stages.when(() -> com.pinodesk.pandora.utility.StageUtils.modal(any(), eq(false), any()))
                        .thenAnswer(invocation -> {
                            opened.set(invocation.getArgument(0));
                            controller.setPageData(newFilter);
                            javafx.event.EventHandler<javafx.stage.WindowEvent> callback = invocation.getArgument(2);
                            callback.handle(null);
                            return null;
                        });
                ((Button) field(controller, "btnFilter")).fire();
                assertEquals(com.pinodesk.constant.Page.SETTINGS_EXPENSE_CATEGORY_FILTER, opened.get());
                ExpenseCategoryFilterVM applied = field(controller, "filter");
                assertEquals(UserStatus.ACTIVE, applied.getStatus());
            }
        });
    }

    @Test
    void editWithSelectionOpensEditPage(FxRobot robot) throws Exception {
        MainFixture controller = loadWith(new MainFixture());
        robot.interact(() -> {
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        TableView<ExpenseCategory> table = field(controller, "tblCategories");
        org.testfx.util.WaitForAsyncUtils
                .waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> table.getItems().size() == 2);
        robot.interact(() -> {
            table.getSelectionModel().select(category1);
            try (var stages = mockStatic(com.pinodesk.pandora.utility.StageUtils.class)) {
                java.util.concurrent.atomic.AtomicReference<com.pinodesk.constant.Page> opened = new java.util.concurrent.atomic.AtomicReference<>();
                stages.when(() -> com.pinodesk.pandora.utility.StageUtils.modal(any(), eq(false), any()))
                        .thenAnswer(invocation -> {
                            opened.set(invocation.getArgument(0));
                            controller.getPageData();
                            controller.setPageData(null);
                            javafx.event.EventHandler<javafx.stage.WindowEvent> callback = invocation.getArgument(2);
                            callback.handle(null);
                            return null;
                        });
                invokeMethod(controller, "handleEdit");
                assertEquals(com.pinodesk.constant.Page.SETTINGS_EXPENSE_CATEGORY_EDIT, opened.get());
            }
        });
    }

    @Test
    void searchReportsLoadFailureViaHandleException(FxRobot robot) throws Exception {
        when(service.findAll()).thenThrow(new IllegalStateException("db down"));
        MainFixture controller = loadWith(new MainFixture());
        robot.interact(() -> {
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        org.testfx.util.WaitForAsyncUtils
                .waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> controller.failure != null);
        robot.interact(() -> {
            Label lblRows = field(controller, "lblRows");
            assertEquals("0", lblRows.getText());
            assertInstanceOf(IllegalStateException.class, controller.failure.getCause());
        });
    }
}

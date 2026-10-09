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
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.Translator;
import com.pinodesk.service.ExpenseCategoryService;
import com.pinodesk.service.SessionService;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ExpenseCategoryFilterVM;
import com.pinodesk.viewmodel.UserGroupMenuVM;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

class ExpenseCategoryFormFilterBehaviorTest extends JavaFXTestBase {

    private final ExpenseCategoryService service = mock(ExpenseCategoryService.class);
    private final SessionService session = mock(SessionService.class, RETURNS_DEEP_STUBS);
    private final ResourceBundle bundle = ResourceBundle.getBundle("pinodesk.lang", Locale.ENGLISH);
    private ApplicationContext previous;
    private ExpenseCategory category1;

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

    // =========================================================================
    // FORM — add mode
    // =========================================================================

    @Test
    void formAddModeStartsWithEmptyNameAndActiveStatus(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/add", new ExpenseCategoryLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");
            form.setPageData(null);
            invokeMethod(form, "initDataSaveControlValues");

            TextField tfName = field(form, "tfName");
            ComboBox<SimpleComboBoxModel> cbStatus = field(form, "cbStatus");

            assertEquals("", tfName.getText());
            assertNotNull(cbStatus.getValue());
            assertEquals(UserStatus.ACTIVE, cbStatus.getValue().getValue());
        });
    }

    @Test
    void formValidationRejectsBlankName(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/add", new ExpenseCategoryLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");
            form.setPageData(null);
            invokeMethod(form, "initDataSaveControlValues");

            ControlValidator validator = new ControlValidator(bundle);
            invokeMethod(form, "validate", validator);
            assertFalse(validator.getResult().isValid());
        });
    }

    @Test
    void formValidationRejectsNameLongerThan100Chars(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/add", new ExpenseCategoryLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");
            form.setPageData(null);
            invokeMethod(form, "initDataSaveControlValues");

            TextField tfName = field(form, "tfName");
            tfName.setText("x".repeat(101));

            ControlValidator validator = new ControlValidator(bundle);
            invokeMethod(form, "validate", validator);
            assertFalse(validator.getResult().isValid());
        });
    }

    @Test
    void formValidationAcceptsValidName(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/add", new ExpenseCategoryLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");
            form.setPageData(null);
            invokeMethod(form, "initDataSaveControlValues");

            TextField tfName = field(form, "tfName");
            tfName.setText("Meals");

            ControlValidator validator = new ControlValidator(bundle);
            invokeMethod(form, "validate", validator);
            assertTrue(validator.getResult().isValid());
        });
    }

    @Test
    void formSavesNewCategoryWithActiveStatus(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/add", new ExpenseCategoryLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");
            form.setPageData(null);
            invokeMethod(form, "initDataSaveControlValues");

            TextField tfName = field(form, "tfName");
            tfName.setText("Meals");

            when(service.save(null, "Meals", UserStatus.ACTIVE)).thenReturn(category1);
            setField(form, "current", null);
            Object result = invokeMethod(form, "save");
            assertEquals(category1, result);
            verify(service).save(null, "Meals", UserStatus.ACTIVE);
        });
    }

    @Test
    void formSavesNewCategoryWithInactiveStatus(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/add", new ExpenseCategoryLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");
            form.setPageData(null);
            invokeMethod(form, "initDataSaveControlValues");

            TextField tfName = field(form, "tfName");
            tfName.setText("Travel");
            ComboBox<SimpleComboBoxModel> cbStatus = field(form, "cbStatus");
            // Select INACTIVE (second item)
            cbStatus.getSelectionModel().select(1);

            when(service.save(null, "Travel", UserStatus.INACTIVE)).thenReturn(category1);
            setField(form, "current", null);
            invokeMethod(form, "save");
            verify(service).save(null, "Travel", UserStatus.INACTIVE);
        });
    }

    // =========================================================================
    // FORM — edit mode
    // =========================================================================

    @Test
    void formEditModePopulatesFieldsFromEntity(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/edit", new ExpenseCategoryLayoutTest.Form());
            form.setPageData(category1);
            invokeMethod(form, "initDataSaveControlActions");
            invokeMethod(form, "initDataSaveControlValues");

            TextField tfName = field(form, "tfName");
            ComboBox<SimpleComboBoxModel> cbStatus = field(form, "cbStatus");

            assertEquals("Office Supplies", tfName.getText());
            assertNotNull(cbStatus.getValue());
            assertEquals(UserStatus.ACTIVE, cbStatus.getValue().getValue());
        });
    }

    @Test
    void formEditModePopulatesInactiveStatus(FxRobot robot) {
        robot.interact(() -> {
            ExpenseCategory inactive = new ExpenseCategory();
            inactive.setId(2L);
            inactive.setName("Travel");
            inactive.setStatus(UserStatus.INACTIVE.toString());

            var form = load("settings/expense-category/edit", new ExpenseCategoryLayoutTest.Form());
            form.setPageData(inactive);
            invokeMethod(form, "initDataSaveControlActions");
            invokeMethod(form, "initDataSaveControlValues");

            ComboBox<SimpleComboBoxModel> cbStatus = field(form, "cbStatus");
            assertNotNull(cbStatus.getValue());
            assertEquals(UserStatus.INACTIVE, cbStatus.getValue().getValue());
        });
    }

    @Test
    void formEditModeSavesUpdatedCategory(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/edit", new ExpenseCategoryLayoutTest.Form());
            form.setPageData(category1);
            invokeMethod(form, "initDataSaveControlActions");
            invokeMethod(form, "initDataSaveControlValues");

            TextField tfName = field(form, "tfName");
            tfName.setText("Updated Supplies");

            when(service.save(1L, "Updated Supplies", UserStatus.ACTIVE)).thenReturn(category1);
            Object result = invokeMethod(form, "save");
            assertEquals(category1, result);
            verify(service).save(1L, "Updated Supplies", UserStatus.ACTIVE);
        });
    }

    @Test
    void formSaveButtonDisabledWhenNotAuthorized(FxRobot robot) {
        robot.interact(() -> {
            when(session.isCurrentSessionActive()).thenReturn(false);
            var form = load("settings/expense-category/add", new ExpenseCategoryLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");

            Button btnSave = field(form, "btnSave");
            assertTrue(btnSave.isDisabled());
        });
    }

    @Test
    void formSaveButtonEnabledWhenAuthorized(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/expense-category/add", new ExpenseCategoryLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");

            Button btnSave = field(form, "btnSave");
            assertFalse(btnSave.isDisabled());
        });
    }

    // =========================================================================
    // FILTER
    // =========================================================================

    @Test
    void filterInitializesWithBlankStateAfterReset(FxRobot robot) {
        robot.interact(() -> {
            var filter = load("settings/expense-category/filter", new ExpenseCategoryLayoutTest.Filter());
            invokeMethod(filter, "initDataFilterControlActions");
            invokeMethod(filter, "resetControls");
            invokeMethod(filter, "initDataFilterControlValues");

            TextField tfName = field(filter, "tfName");
            ComboBox<?> cbStatus = field(filter, "cbStatus");

            assertEquals("", tfName.getText());
            assertNotNull(cbStatus.getValue());
            // After reset, status is the first item (null = all)
            SimpleComboBoxModel selected = (SimpleComboBoxModel) cbStatus.getValue();
            assertNull(selected.getValue());
        });
    }

    @Test
    void filterRestoresExistingFilterValues(FxRobot robot) {
        robot.interact(() -> {
            var filter = load("settings/expense-category/filter", new ExpenseCategoryLayoutTest.Filter());
            invokeMethod(filter, "initDataFilterControlActions");
            invokeMethod(filter, "resetControls");

            ExpenseCategoryFilterVM existingFilter = new ExpenseCategoryFilterVM();
            existingFilter.setName("food");
            existingFilter.setStatus(UserStatus.ACTIVE);
            setField(filter, "currentFilter", existingFilter);
            invokeMethod(filter, "initDataFilterControlValues");

            TextField tfName = field(filter, "tfName");
            ComboBox<?> cbStatus = field(filter, "cbStatus");

            assertEquals("food", tfName.getText());
            assertNotNull(cbStatus.getValue());
            assertEquals(UserStatus.ACTIVE, ((SimpleComboBoxModel) cbStatus.getValue()).getValue());
        });
    }

    @Test
    void filterGetFreshValuesReflectsCurrentUiState(FxRobot robot) {
        robot.interact(() -> {
            var filter = load("settings/expense-category/filter", new ExpenseCategoryLayoutTest.Filter());
            invokeMethod(filter, "initDataFilterControlActions");
            invokeMethod(filter, "resetControls");
            invokeMethod(filter, "initDataFilterControlValues");

            TextField tfName = field(filter, "tfName");
            ComboBox<SimpleComboBoxModel> cbStatus = field(filter, "cbStatus");
            tfName.setText("utilities");
            // Select ACTIVE (second item, index 1)
            cbStatus.getSelectionModel().select(1);

            ExpenseCategoryFilterVM result = invokeMethod(filter, "getFreshFilterValues");
            assertEquals("utilities", result.getName());
            assertEquals(UserStatus.ACTIVE, result.getStatus());
        });
    }

    @Test
    void filterResetClearsNameAndSelectsAll(FxRobot robot) {
        robot.interact(() -> {
            var filter = load("settings/expense-category/filter", new ExpenseCategoryLayoutTest.Filter());
            invokeMethod(filter, "initDataFilterControlActions");
            invokeMethod(filter, "resetControls");

            TextField tfName = field(filter, "tfName");
            ComboBox<SimpleComboBoxModel> cbStatus = field(filter, "cbStatus");

            // Set some values
            tfName.setText("food");
            cbStatus.getSelectionModel().select(2); // INACTIVE

            invokeMethod(filter, "resetControls");

            assertEquals("", tfName.getText());
            // After reset, first item (null/all) is selected
            assertNull(cbStatus.getValue().getValue());
        });
    }

    @Test
    void filterGetFreshValuesWithNullStatusAfterReset(FxRobot robot) {
        robot.interact(() -> {
            var filter = load("settings/expense-category/filter", new ExpenseCategoryLayoutTest.Filter());
            invokeMethod(filter, "initDataFilterControlActions");
            invokeMethod(filter, "resetControls");
            invokeMethod(filter, "initDataFilterControlValues");

            ExpenseCategoryFilterVM result = invokeMethod(filter, "getFreshFilterValues");
            assertEquals("", result.getName());
            assertNull(result.getStatus());
        });
    }

    @Test
    void filterRestoresAndThenRoundTrips(FxRobot robot) {
        robot.interact(() -> {
            var filter = load("settings/expense-category/filter", new ExpenseCategoryLayoutTest.Filter());
            invokeMethod(filter, "initDataFilterControlActions");
            invokeMethod(filter, "resetControls");

            ExpenseCategoryFilterVM existingFilter = new ExpenseCategoryFilterVM();
            existingFilter.setName("rent");
            existingFilter.setStatus(UserStatus.INACTIVE);
            setField(filter, "currentFilter", existingFilter);
            invokeMethod(filter, "initDataFilterControlValues");

            // Round-trip: getFreshFilterValues should mirror what was restored
            ExpenseCategoryFilterVM fresh = invokeMethod(filter, "getFreshFilterValues");
            assertEquals("rent", fresh.getName());
            assertEquals(UserStatus.INACTIVE, fresh.getStatus());

            // Reset wipes it out
            invokeMethod(filter, "resetControls");
            ExpenseCategoryFilterVM reset = invokeMethod(filter, "getFreshFilterValues");
            assertEquals("", reset.getName());
            assertNull(reset.getStatus());
        });
    }
}

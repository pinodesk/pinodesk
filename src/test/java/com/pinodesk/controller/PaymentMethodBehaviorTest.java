package com.pinodesk.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;
import com.pinodesk.JavaFXTestBase;
import com.pinodesk.constant.*;
import com.pinodesk.entity.PaymentMethod;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.Translator;
import com.pinodesk.service.*;
import com.pinodesk.util.PaymentMethodControls;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

class PaymentMethodBehaviorTest extends JavaFXTestBase {
    private final PaymentMethodService service = mock(PaymentMethodService.class);
    private final SessionService session = mock(SessionService.class, RETURNS_DEEP_STUBS);
    private final SaleService sales = mock(SaleService.class);
    private final ResourceBundle bundle = ResourceBundle.getBundle("pinodesk.lang", Locale.ENGLISH);
    private ApplicationContext previous;
    private PaymentMethod cash;
    private PaymentMethod transfer;

    @BeforeEach
    void setup() {
        clearPageData();
        previous = SpringUtils.getApplicationContext();
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBean(PaymentMethodService.class)).thenReturn(service);
        when(context.getBean(SaleService.class)).thenReturn(sales);
        setField(SpringUtils.class, "applicationContext", context);
        cash = method(1L, "Cash", "CASH");
        cash.setDefaultMethod(true);
        transfer = method(2L, "Transfer BCA", "TRANSFER");
        when(service.findActive()).thenReturn(List.of(cash, transfer));
        when(service.findAll()).thenReturn(List.of(cash, transfer));
        when(session.isCurrentSessionActive()).thenReturn(true);
        UserGroupMenuVM menu = new UserGroupMenuVM();
        menu.setMenuCode(MenuCodeConstants.SETTINGS_PAYMENT_METHODS);
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
            // Dialog results are shared application state; isolate each test.
        }
    }

    private static PaymentMethod method(Long id, String name, String category) {
        PaymentMethod method = new PaymentMethod();
        method.setId(id);
        method.setName(name);
        method.setCategory(category);
        method.setStatus("active");
        return method;
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
    void choicesRespectPermissionsStatusAndSelection(FxRobot robot) {
        robot.interact(() -> {
            assertTrue(PaymentMethodControls.canWrite(session));
            when(session.isCurrentSessionActive()).thenReturn(false);
            assertFalse(PaymentMethodControls.canWrite(session));
            when(session.isCurrentSessionActive()).thenReturn(true);
            UserGroupMenuVM denied = new UserGroupMenuVM();
            denied.setMenuCode("other");
            when(session.getCurrentSession().getUserGroupMenus()).thenReturn(List.of(denied));
            assertFalse(PaymentMethodControls.canWrite(session));
            denied.setMenuCode(MenuCodeConstants.SETTINGS_PAYMENT_METHODS);
            assertFalse(PaymentMethodControls.canWrite(session));
            ComboBox<PaymentMethod> box = new ComboBox<>();
            PaymentMethodControls.initialize(box, bundle, null);
            assertSame(cash, box.getValue());
            assertEquals("", box.getConverter().toString(null));
            assertEquals("Cash", box.getConverter().toString(cash));
            assertNull(box.getConverter().fromString("unknown"));
            PaymentMethodControls.initialize(box, bundle, transfer.getId());
            assertSame(transfer, box.getValue());
            PaymentMethodControls.initialize(box, bundle, null, true);
            assertNull(box.getValue());
            PaymentMethodControls.initialize(box, bundle, transfer.getId(), true);
            assertSame(transfer, box.getValue());
            PaymentMethod inactive = method(3L, "Inactive", "QRIS");
            inactive.setStatus("inactive");
            FlowPane pane = new FlowPane();
            ToggleGroup group = PaymentMethodControls.initializeChoices(pane, List.of(cash, transfer, inactive));
            assertEquals(2, pane.getChildren().size());
            assertNull(PaymentMethodControls.selectedMethod(group));
            ((ToggleButton) pane.getChildren().get(1)).fire();
            assertSame(transfer, PaymentMethodControls.selectedMethod(group));
            ((ToggleButton) pane.getChildren().get(1)).fire();
            assertNull(PaymentMethodControls.selectedMethod(group));
        });
    }

    @Test
    void formValidatesAndSavesNewAndExistingMethods(FxRobot robot) {
        robot.interact(() -> {
            var form = load("settings/payment-method/add", new PaymentMethodLayoutTest.Form());
            invokeMethod(form, "initDataSaveControlActions");
            form.setPageData(null);
            invokeMethod(form, "initDataSaveControlValues");
            TextField name = field(form, "tfName");
            ComboBox<PaymentMethodCategory> category = field(form, "cbCategory");
            assertEquals("", category.getConverter().toString(null));
            assertEquals("Transfer", category.getConverter().toString(PaymentMethodCategory.TRANSFER));
            assertNull(category.getConverter().fromString("Transfer"));
            ControlValidator invalid = new ControlValidator(bundle);
            invokeMethod(form, "validate", invalid);
            assertFalse(invalid.getResult().isValid());
            name.setText("x".repeat(101));
            invalid = new ControlValidator(bundle);
            invokeMethod(form, "validate", invalid);
            assertFalse(invalid.getResult().isValid());
            name.setText("New QRIS");
            category.setValue(null);
            invalid = new ControlValidator(bundle);
            invokeMethod(form, "validate", invalid);
            assertFalse(invalid.getResult().isValid());
            category.setValue(PaymentMethodCategory.QRIS);
            ControlValidator valid = new ControlValidator(bundle);
            invokeMethod(form, "validate", valid);
            assertTrue(valid.getResult().isValid());
            invokeMethod(form, "save");
            verify(service).save(null, "New QRIS", PaymentMethodCategory.QRIS, UserStatus.ACTIVE);
            form.setPageData(cash);
            invokeMethod(form, "initDataSaveControlValues");
            assertTrue(category.isDisabled());
            assertEquals("Cash", name.getText());
            transfer.setStatus("inactive");
            form.setPageData(transfer);
            invokeMethod(form, "initDataSaveControlValues");
            assertFalse(category.isDisabled());
            invokeMethod(form, "save");
            verify(service).save(2L, "Transfer BCA", PaymentMethodCategory.TRANSFER, UserStatus.INACTIVE);
        });
    }

    @Test
    void filterRestoresAndResetsAllFields(FxRobot robot) {
        robot.interact(() -> {
            var controller = load("settings/payment-method/filter", new PaymentMethodLayoutTest.Filter());
            invokeMethod(controller, "initDataFilterControlActions");
            invokeMethod(controller, "resetControls");
            invokeMethod(controller, "initDataFilterControlValues");
            PaymentMethodFilterVM filter = new PaymentMethodFilterVM();
            filter.setName("BCA");
            filter.setCategory("TRANSFER");
            filter.setStatus(UserStatus.INACTIVE);
            setField(controller, "currentFilter", filter);
            invokeMethod(controller, "initDataFilterControlValues");
            assertEquals(filter, invokeMethod(controller, "getFreshFilterValues"));
            invokeMethod(controller, "resetControls");
            PaymentMethodFilterVM reset = invokeMethod(controller, "getFreshFilterValues");
            assertEquals("", reset.getName());
            assertNull(reset.getCategory());
            assertNull(reset.getStatus());
        });
    }

    @Test
    void cashierHandlesCashTransferAndNoSelection(FxRobot robot) {
        robot.interact(() -> {
            var controller = load("transaction/sale/cashier/pay", new PaymentMethodLayoutTest.Pay());
            invokeMethod(controller, "updatePaymentAmount");
            invokeMethod(controller, "initDataSaveControlActions");
            SaleDataVM data = new SaleDataVM();
            data.setCustomer(Optional.empty());
            data.setTotalSale(new BigDecimal("1050.0000"));
            data.setTotalProduct(1);
            data.setSellingMode(SellingMode.GENERAL);
            data.setSaleProducts(List.of());
            controller.setPageData(data);
            invokeMethod(controller, "initDataSaveControlValues");
            TextField amount = field(controller, "tfPaymentAmount");
            assertEquals("1050", amount.getText());
            assertTrue(amount.isEditable());
            amount.setText("2000");
            assertEquals(0, new BigDecimal("950").compareTo(field(controller, "changeAmount")));
            FlowPane methods = field(controller, "paymentMethods");
            ((ToggleButton) methods.getChildren().get(1)).fire();
            assertFalse(amount.isEditable());
            assertEquals("1050", amount.getText());
            invokeMethod(controller, "save");
            var capture = org.mockito.ArgumentCaptor.forClass(SaleAddVM.class);
            verify(sales).createSaleCashier(capture.capture());
            assertEquals(2L, capture.getValue().getPaymentMethodId());
            ComboBox<?> status = field(controller, "cbPaymentStatus");
            status.getSelectionModel().select(1);
            assertNull(PaymentMethodControls.selectedMethod(field(controller, "paymentMethodGroup")));
            DatePicker dueDate = field(controller, "dpDueDate");
            dueDate.setValue(LocalDate.now().plusDays(1));
            invokeMethod(controller, "save");
            verify(sales, times(2)).createSaleCashier(capture.capture());
            assertNull(capture.getValue().getPaymentMethodId());
            status.getSelectionModel().select(0);
            assertNull(dueDate.getValue());
            assertTrue(amount.isEditable());
            ((ToggleButton) methods.getChildren().get(0)).fire();
            invokeMethod(controller, "updatePaymentAmount");
            assertNull(PaymentMethodControls.selectedMethod(field(controller, "paymentMethodGroup")));
            ToggleGroup group = field(controller, "paymentMethodGroup");
            try (var selection = mockStatic(PaymentMethodControls.class, CALLS_REAL_METHODS)) {
                selection.when(() -> PaymentMethodControls.selectedMethod(group)).thenReturn(transfer, null);
                invokeMethod(controller, "updatePaymentAmount");
                assertFalse(amount.isEditable());
                assertEquals("1050", amount.getText());
                selection.verify(() -> PaymentMethodControls.selectedMethod(group), times(1));
            }
        });
    }

    @Test
    void listFiltersMethodsAndDisplaysTranslatedColumns(FxRobot robot) throws Exception {
        var controller = new PaymentMethodLayoutTest.Main();
        robot.interact(() -> {
            load("settings/payment-method/main", controller);
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        TableView<PaymentMethod> table = field(controller, "tblMethods");
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> table.getItems().size() == 2);
        robot.interact(() -> {
            TableColumn<PaymentMethod, String> name = field(controller, "colName");
            TableColumn<PaymentMethod, String> category = field(controller, "colCategory");
            TableColumn<PaymentMethod, String> status = field(controller, "colStatus");
            assertEquals("Cash", name.getCellData(cash));
            assertEquals("Transfer", category.getCellData(transfer));
            assertEquals("Active", status.getCellData(cash));
            transfer.setStatus("inactive");
            assertEquals("Inactive", status.getCellData(transfer));
            PaymentMethodFilterVM filter = field(controller, "filter");
            filter.setName(" bCa ");
            filter.setCategory("TRANSFER");
            filter.setStatus(UserStatus.INACTIVE);
            invokeMethod(controller, "searchMethods");
        });
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> table.getItems().size() == 1);
        robot.interact(() -> assertSame(transfer, table.getItems().get(0)));
    }

    @Test
    void saleFilterRestoresPaymentAndRemovesHiddenDoctor(FxRobot robot) {
        robot.interact(() -> {
            var controller = load("transaction/sale/filter", new PaymentMethodLayoutTest.SaleFilter());
            ConfigurationService config = mock(ConfigurationService.class);
            controller.configurationService = config;
            when(config.getConfiguration(ConfigurationConstants.PHARMACY_FEATURES_ENABLED)).thenReturn("yes");
            invokeMethod(controller, "initDataFilterControlActions");
            invokeMethod(controller, "resetControls");
            invokeMethod(controller, "initDataFilterControlValues");
            SaleFilterVM filter = new SaleFilterVM();
            filter.setPaymentMethodId(2L);
            filter.setDoctorId(10L);
            filter.setDoctorName("Doctor");
            setField(controller, "currentFilter", filter);
            invokeMethod(controller, "initDataFilterControlValues");
            SaleFilterVM result = invokeMethod(controller, "getFreshFilterValues");
            assertEquals(2L, result.getPaymentMethodId());
            assertEquals(10L, result.getDoctorId());
            when(config.getConfiguration(ConfigurationConstants.PHARMACY_FEATURES_ENABLED)).thenReturn("no");
            invokeMethod(controller, "initDataFilterControlActions");
            invokeMethod(controller, "initDataFilterControlValues");
            javafx.scene.layout.VBox doctor = field(controller, "vboxDoctor");
            assertFalse(doctor.isManaged());
            result = invokeMethod(controller, "getFreshFilterValues");
            assertNull(result.getDoctorId());
            invokeMethod(controller, "resetControls");
            result = invokeMethod(controller, "getFreshFilterValues");
            assertNull(result.getPaymentMethodId());
        });
    }

    public static class MainFixture extends PaymentMethodLayoutTest.Main {
        boolean confirmed;
        Throwable failure;

        @Override
        protected com.pinodesk.pandora.utility.AlertResult displayConfirmation(
                com.pinodesk.pandora.utility.IMessage message) {
            return new com.pinodesk.pandora.utility.AlertResult(
                    Optional.of(confirmed ? ButtonType.YES : ButtonType.NO));
        }

        @Override
        protected void handleException(Throwable error) {
            failure = error;
        }
    }

    @Test
    void listOpensDialogsAndRequiresConfirmationBeforeDeleting(FxRobot robot) throws Exception {
        MainFixture controller = new MainFixture();
        robot.interact(() -> {
            load("settings/payment-method/main", controller);
            invokeMethod(controller, "initControlActions");
            setField(controller, "filter", new PaymentMethodFilterVM());
            TableView<PaymentMethod> table = field(controller, "tblMethods");
            table.getItems().setAll(cash, transfer);
            Button remove = field(controller, "btnRemove");
            remove.fire();
            verify(service, never()).remove(anyList());
            table.getSelectionModel().select(transfer);
            remove.fire();
            verify(service, never()).remove(anyList());
            controller.confirmed = true;
            remove.fire();
            verify(service).remove(List.of(2L));
            try (var stages = mockStatic(com.pinodesk.pandora.utility.StageUtils.class)) {
                java.util.concurrent.atomic.AtomicReference<Page> opened = new java.util.concurrent.atomic.AtomicReference<>();
                stages.when(() -> com.pinodesk.pandora.utility.StageUtils.modal(any(), eq(false), any()))
                        .thenAnswer(invocation -> {
                            Page page = invocation.getArgument(0);
                            opened.set(page);
                            if (page != Page.SETTINGS_PAYMENT_METHOD_ADD) {
                                controller.getPageData();
                            }
                            controller.setPageData(null);
                            javafx.event.EventHandler<javafx.stage.WindowEvent> callback = invocation.getArgument(2);
                            callback.handle(null);
                            return null;
                        });
                ((Button) field(controller, "btnAdd")).fire();
                assertEquals(Page.SETTINGS_PAYMENT_METHOD_ADD, opened.get());
                ((Button) field(controller, "btnFilter")).fire();
                assertEquals(Page.SETTINGS_PAYMENT_METHOD_FILTER, opened.get());
                invokeMethod(controller, "handleEditMethod");
                table.getItems().setAll(cash, transfer);
                table.getSelectionModel().select(transfer);
                invokeMethod(controller, "handleEditMethod");
                assertEquals(Page.SETTINGS_PAYMENT_METHOD_EDIT, opened.get());
                stages.when(() -> com.pinodesk.pandora.utility.StageUtils.modal(any(), eq(false), any()))
                        .thenAnswer(invocation -> {
                            Page page = invocation.getArgument(0);
                            if (page != Page.SETTINGS_PAYMENT_METHOD_ADD) {
                                controller.getPageData();
                            }
                            controller.setPageData(
                                    page == Page.SETTINGS_PAYMENT_METHOD_FILTER ?
                                            new PaymentMethodFilterVM() : Boolean.TRUE);
                            javafx.event.EventHandler<javafx.stage.WindowEvent> callback = invocation.getArgument(2);
                            callback.handle(null);
                            return null;
                        });
                ((Button) field(controller, "btnAdd")).fire();
                ((Button) field(controller, "btnFilter")).fire();
                table.getItems().setAll(transfer);
                table.getSelectionModel().selectFirst();
                invokeMethod(controller, "handleEditMethod");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void listReportsLoadFailureWithoutLeavingLoadingState(FxRobot robot) throws Exception {
        MainFixture controller = new MainFixture();
        when(service.findAll()).thenThrow(new IllegalStateException("Database unavailable"));
        robot.interact(() -> {
            load("settings/payment-method/main", controller);
            invokeMethod(controller, "initControlActions");
            invokeMethod(controller, "initControlValues");
        });
        WaitForAsyncUtils.waitFor(5, java.util.concurrent.TimeUnit.SECONDS, () -> controller.failure != null);
        robot.interact(() -> {
            assertEquals("0", ((Label) field(controller, "lblRows")).getText());
            assertTrue(((TableView<?>) field(controller, "tblMethods")).getItems().isEmpty());
        });
    }
}

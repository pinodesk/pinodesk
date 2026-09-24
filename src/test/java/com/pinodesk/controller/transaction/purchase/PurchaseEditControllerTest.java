package com.pinodesk.controller.transaction.purchase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.pinodesk.JavaFXTestBase;
import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.DiscountType;
import com.pinodesk.constant.PaymentStatus;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.AlertResult;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.IMessage;
import com.pinodesk.pandora.utility.Translator;
import com.pinodesk.pandora.utility.ValidationResult;
import com.pinodesk.service.PurchaseService;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ChooseResultVM;
import com.pinodesk.viewmodel.ProductVM;
import com.pinodesk.viewmodel.PurchaseEditVM;
import com.pinodesk.viewmodel.PurchaseProductVM;
import com.pinodesk.viewmodel.PurchaseVM;
import com.pinodesk.viewmodel.SupplierVM;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

class PurchaseEditControllerTest extends JavaFXTestBase {

    static class TestablePurchaseEditController extends PurchaseEditController {
        AlertResult confirmationResult = new AlertResult(Optional.of(ButtonType.YES));
        AlertResult infoResult = new AlertResult(Optional.of(ButtonType.OK));
        boolean closed = false;
        Object pageDataToReturn = null;

        @Override
        protected AlertResult displayConfirmation(IMessage messageCode) {
            return confirmationResult;
        }

        @Override
        protected AlertResult displayInfo(IMessage messageCode) {
            return infoResult;
        }

        @Override
        public void close() {
            this.closed = true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object getPageData() {
            return pageDataToReturn;
        }

        @Override
        public boolean isPharmacyFeatureEnabled() {
            return false;
        }
    }

    private TestablePurchaseEditController controller;
    private PurchaseService mockPurchaseService;
    private MockedStatic<SpringUtils> mockedSpringUtils;

    // FXML Controls
    private ScrollPane scrollPanePurchaseEdit;
    private Button btnRemove;
    private TextField tfSupplier;
    private Button btnNewSupplier;
    private TextField tfInvoiceNumber;
    private DatePicker dpInvoiceDate;
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;
    private VBox vboxDueDate;
    private DatePicker dpDueDate;
    private TextField tfTax;
    private TextField tfProduct;
    private Button btnNewProduct;
    private TextField tfProductCategory;
    private VBox vboxUnit;
    private TextField tfProductUnit;
    private TextField tfProductQuantity;
    private TextField tfBuyingPrice;
    private TextField tfGeneralSellingPrice;
    private VBox vboxPrescriptionSellingPrice;
    private TextField tfPrescriptionSellingPrice;
    private TextField tfBatchNumber;
    private DatePicker dpExpiredDate;
    private ComboBox<SimpleComboBoxModel> cbDiscountType;
    private Label lblDiscountPercent;
    private TextField tfBuyingPriceDiscount;
    private TextField tfAdditionalDiscount;
    private TextField tfDiscountAmount;
    private Button btnAddProduct;
    private TableView<PurchaseProductVM> tblPurchaseProduct;
    private TableColumn<PurchaseProductVM, BigDecimal> colPrescriptionSellingPrice;
    private Label lblTotalProduct;
    private Label lblTotalPrice;
    private Label lblTotalDiscount;
    private Label lblTotalPriceDiscount;
    private Label lblTax;
    private Label lblTotalPayment;
    private VBox vboxDiscountAmount;
    private VBox vboxBuyingPriceDiscount;

    private ResourceBundle mockResources;
    private Translator mockTranslator;

    @BeforeEach
    void setUp() throws Exception {
        controller = new TestablePurchaseEditController();
        mockPurchaseService = mock(PurchaseService.class);

        mockedSpringUtils = Mockito.mockStatic(SpringUtils.class);
        mockedSpringUtils.when(() -> SpringUtils.getBean(PurchaseService.class)).thenReturn(mockPurchaseService);

        mockResources = mock(ResourceBundle.class);
        mockTranslator = mock(Translator.class);
        when(mockResources.getLocale()).thenReturn(Locale.US);
        when(mockTranslator.translate(any(String.class))).thenAnswer(inv -> inv.getArgument(0));

        // Create FXML controls
        scrollPanePurchaseEdit = new ScrollPane();
        btnRemove = new Button();
        tfSupplier = new TextField();
        btnNewSupplier = new Button();
        tfInvoiceNumber = new TextField();
        dpInvoiceDate = new DatePicker();
        cbPaymentStatus = new ComboBox<>();
        vboxDueDate = new VBox();
        dpDueDate = new DatePicker();
        tfTax = new TextField();
        tfProduct = new TextField();
        btnNewProduct = new Button();
        tfProductCategory = new TextField();
        vboxUnit = new VBox();
        tfProductUnit = new TextField();
        tfProductQuantity = new TextField();
        tfBuyingPrice = new TextField();
        tfGeneralSellingPrice = new TextField();
        vboxPrescriptionSellingPrice = new VBox();
        tfPrescriptionSellingPrice = new TextField();
        tfBatchNumber = new TextField();
        dpExpiredDate = new DatePicker();
        cbDiscountType = new ComboBox<>();
        lblDiscountPercent = new Label();
        tfBuyingPriceDiscount = new TextField();
        tfAdditionalDiscount = new TextField();
        tfDiscountAmount = new TextField();
        btnAddProduct = new Button();
        tblPurchaseProduct = new TableView<>(FXCollections.observableArrayList());
        colPrescriptionSellingPrice = new TableColumn<>();
        tblPurchaseProduct.getColumns().add(colPrescriptionSellingPrice);
        lblTotalProduct = new Label("0");
        lblTotalPrice = new Label("0");
        lblTotalDiscount = new Label("0");
        lblTotalPriceDiscount = new Label("0");
        lblTax = new Label("0");
        lblTotalPayment = new Label("0");
        vboxDiscountAmount = new VBox();
        vboxBuyingPriceDiscount = new VBox();

        // Initialize combo boxes
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(PaymentStatus.PAID, "Paid"),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, "Unpaid"));
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);

        ComboBoxUtils.initSimple(
                cbDiscountType,
                new SimpleComboBoxModel(null, "No Discount"),
                new SimpleComboBoxModel(DiscountType.PERCENTAGE, "Percentage"),
                new SimpleComboBoxModel(DiscountType.FIXED_AMOUNT, "Fixed Amount"));
        ComboBoxUtils.selectIndex(cbDiscountType, 0);

        // Inject FXML fields via reflection
        injectField(controller, "scrollPanePurchaseEdit", scrollPanePurchaseEdit);
        injectField(controller, "btnRemove", btnRemove);
        injectField(controller, "tfSupplier", tfSupplier);
        injectField(controller, "btnNewSupplier", btnNewSupplier);
        injectField(controller, "tfInvoiceNumber", tfInvoiceNumber);
        injectField(controller, "dpInvoiceDate", dpInvoiceDate);
        injectField(controller, "cbPaymentStatus", cbPaymentStatus);
        injectField(controller, "vboxDueDate", vboxDueDate);
        injectField(controller, "dpDueDate", dpDueDate);
        injectField(controller, "tfTax", tfTax);
        injectField(controller, "tfProduct", tfProduct);
        injectField(controller, "btnNewProduct", btnNewProduct);
        injectField(controller, "tfProductCategory", tfProductCategory);
        injectField(controller, "vboxUnit", vboxUnit);
        injectField(controller, "tfProductUnit", tfProductUnit);
        injectField(controller, "tfProductQuantity", tfProductQuantity);
        injectField(controller, "tfBuyingPrice", tfBuyingPrice);
        injectField(controller, "tfGeneralSellingPrice", tfGeneralSellingPrice);
        injectField(controller, "vboxPrescriptionSellingPrice", vboxPrescriptionSellingPrice);
        injectField(controller, "tfPrescriptionSellingPrice", tfPrescriptionSellingPrice);
        injectField(controller, "tfBatchNumber", tfBatchNumber);
        injectField(controller, "dpExpiredDate", dpExpiredDate);
        injectField(controller, "cbDiscountType", cbDiscountType);
        injectField(controller, "lblDiscountPercent", lblDiscountPercent);
        injectField(controller, "tfBuyingPriceDiscount", tfBuyingPriceDiscount);
        injectField(controller, "tfAdditionalDiscount", tfAdditionalDiscount);
        injectField(controller, "tfDiscountAmount", tfDiscountAmount);
        injectField(controller, "btnAddProduct", btnAddProduct);
        injectField(controller, "tblPurchaseProduct", tblPurchaseProduct);
        injectField(controller, "colPrescriptionSellingPrice", colPrescriptionSellingPrice);
        injectField(controller, "lblTotalProduct", lblTotalProduct);
        injectField(controller, "lblTotalPrice", lblTotalPrice);
        injectField(controller, "lblTotalDiscount", lblTotalDiscount);
        injectField(controller, "lblTotalPriceDiscount", lblTotalPriceDiscount);
        injectField(controller, "lblTax", lblTax);
        injectField(controller, "lblTotalPayment", lblTotalPayment);
        injectField(controller, "vboxDiscountAmount", vboxDiscountAmount);
        injectField(controller, "vboxBuyingPriceDiscount", vboxBuyingPriceDiscount);

        // Inject superclass protected fields
        injectSuperField(controller, "resources", mockResources);
        injectSuperField(controller, "t", mockTranslator);
        injectField(controller, "purchaseService", mockPurchaseService);
    }

    @AfterEach
    void tearDown() {
        if (mockedSpringUtils != null) {
            mockedSpringUtils.close();
        }
    }

    // ==========================================
    // Tests for validateAddProduct
    // (Focusing on NPE fix and discount validation)
    // ==========================================

    @Test
    void validateAddProduct_withNullBuyingPriceAndFixedAmountDiscount_shouldNotThrowNPE() throws Exception {
        setSelectedProduct("000000001");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        tfDiscountAmount.setText("10.00");
        tfBuyingPrice.setText(""); // null buying price
        tfProductQuantity.setText("5");

        ValidationResult result = invokeValidateAddProduct(true, false, null);

        assertThat(result.isValid(), is(false));
    }

    @Test
    void validateAddProduct_withNullDiscountAmountAndFixedAmountDiscount_shouldNotThrowNPE() throws Exception {
        setSelectedProduct("000000001");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        tfDiscountAmount.setText(""); // null discount amount
        tfBuyingPrice.setText("100.00");
        tfProductQuantity.setText("5");

        ValidationResult result = invokeValidateAddProduct(true, false, null);

        assertThat(result.isValid(), is(true));
    }

    @Test
    void validateAddProduct_withBothNullDiscountAndBuyingPrice_shouldNotThrowNPE() throws Exception {
        setSelectedProduct("000000001");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        tfDiscountAmount.setText("");
        tfBuyingPrice.setText("");
        tfProductQuantity.setText("5");

        ValidationResult result = invokeValidateAddProduct(true, false, null);

        assertThat(result.isValid(), is(false));
    }

    @Test
    void validateAddProduct_fixedAmountDiscountGreaterThanBuyingPrice_shouldReturnError() throws Exception {
        setSelectedProduct("000000001");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        tfDiscountAmount.setText("150.00");
        tfBuyingPrice.setText("100.00");
        tfProductQuantity.setText("5");

        ValidationResult result = invokeValidateAddProduct(true, false, null);

        assertThat(result.isValid(), is(false));
        assertThat(result.getMessages(), is(not(empty())));
    }

    @Test
    void validateAddProduct_fixedAmountDiscountLessThanBuyingPrice_shouldPass() throws Exception {
        setSelectedProduct("000000001");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        tfDiscountAmount.setText("50.00");
        tfBuyingPrice.setText("100.00");
        tfProductQuantity.setText("5");

        ValidationResult result = invokeValidateAddProduct(true, false, null);

        assertThat(result.isValid(), is(true));
    }

    @Test
    void validateAddProduct_fixedAmountDiscountEqualToBuyingPrice_shouldPass() throws Exception {
        setSelectedProduct("000000001");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        tfDiscountAmount.setText("100.00");
        tfBuyingPrice.setText("100.00");
        tfProductQuantity.setText("5");

        ValidationResult result = invokeValidateAddProduct(true, false, null);

        assertThat(result.isValid(), is(true));
    }

    @Test
    void validateAddProduct_withNoProductSelected_shouldReturnError() throws Exception {
        ValidationResult result = invokeValidateAddProduct(false, false, null);
        assertThat(result.isValid(), is(false));
        assertThat(result.getMessages(), is(not(empty())));
    }

    @Test
    void validateAddProduct_withEmptyQuantity_shouldReturnError() throws Exception {
        setSelectedProduct("000000001");
        tfProductQuantity.setText("");
        tfBuyingPrice.setText("100");
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validateAddProduct_withZeroQuantity_shouldReturnError() throws Exception {
        setSelectedProduct("000000001");
        tfProductQuantity.setText("0");
        tfBuyingPrice.setText("100");
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validateAddProduct_withEmptyBuyingPrice_shouldReturnError() throws Exception {
        setSelectedProduct("000000001");
        tfProductQuantity.setText("10");
        tfBuyingPrice.setText("");
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validateAddProduct_withValidInputs_shouldPass() throws Exception {
        tfProductQuantity.setText("10");
        tfBuyingPrice.setText("100");
        tfGeneralSellingPrice.setText("");
        tfPrescriptionSellingPrice.setText("");
        tfDiscountAmount.setText("");
        setSelectedProduct("000000001");
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result.isValid(), is(true));
    }

    @Test
    void validateAddProduct_withInvalidGeneralSellingPrice_shouldReturnError() throws Exception {
        tfProductQuantity.setText("10");
        tfBuyingPrice.setText("100");
        tfGeneralSellingPrice.setText("-5");
        tfPrescriptionSellingPrice.setText("");
        tfDiscountAmount.setText("");
        setSelectedProduct("000000001");
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validateAddProduct_withCustomPackageCategory_shouldReturnError() throws Exception {
        tfProductQuantity.setText("10");
        tfBuyingPrice.setText("100");
        tfGeneralSellingPrice.setText("");
        tfPrescriptionSellingPrice.setText("");
        tfDiscountAmount.setText("");
        setSelectedProduct(CommonConstants.PRODUCT_CATEGORY_CODE_CUSTOM_PACKAGE);
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validateAddProduct_withPercentageDiscount_validDiscount_shouldPass() throws Exception {
        setSelectedProduct("000000001");
        ComboBoxUtils.selectIndex(cbDiscountType, 1); // PERCENTAGE
        tfDiscountAmount.setText("20");
        tfBuyingPrice.setText("100");
        tfProductQuantity.setText("5");
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result.isValid(), is(true));
    }

    // ==========================================
    // Tests for calculateBuyingPriceDiscount
    // ==========================================

    @Test
    void calculateBuyingPriceDiscount_withBlankDiscountAmount_shouldClearField() throws Exception {
        tfBuyingPrice.setText("100");
        tfDiscountAmount.setText("");
        invokeCalculateBuyingPriceDiscount();
        assertThat(tfBuyingPriceDiscount.getText(), is(equalTo("")));
    }

    @Test
    void calculateBuyingPriceDiscount_withBlankBuyingPrice_shouldClearField() throws Exception {
        tfBuyingPrice.setText("");
        tfDiscountAmount.setText("10");
        invokeCalculateBuyingPriceDiscount();
        assertThat(tfBuyingPriceDiscount.getText(), is(equalTo("")));
    }

    @Test
    void calculateBuyingPriceDiscount_withPercentageDiscount_shouldCalculateCorrectly() throws Exception {
        ComboBoxUtils.selectIndex(cbDiscountType, 1); // PERCENTAGE
        tfBuyingPrice.setText("100.00");
        tfDiscountAmount.setText("20");
        invokeCalculateBuyingPriceDiscount();
        assertThat(Double.parseDouble(tfBuyingPriceDiscount.getText()), comparesEqualTo(80.0));
    }

    @Test
    void calculateBuyingPriceDiscount_withFixedAmountDiscount_shouldCalculateCorrectly() throws Exception {
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        tfBuyingPrice.setText("100.00");
        tfDiscountAmount.setText("15.00");
        invokeCalculateBuyingPriceDiscount();
        assertThat(Double.parseDouble(tfBuyingPriceDiscount.getText()), comparesEqualTo(85.0));
    }

    // ==========================================
    // Tests for calculatePurchaseSummary
    // ==========================================

    @Test
    void calculatePurchaseSummary_withEmptyProducts_shouldSetZeroes() throws Exception {
        tblPurchaseProduct.getItems().clear();
        invokeCalculatePurchaseSummary();

        assertThat(lblTotalProduct.getText(), is(equalTo("0")));
        assertThat(lblTotalPrice.getText(), is(equalTo("0")));
        assertThat(lblTotalDiscount.getText(), is(equalTo("0")));
        assertThat(lblTotalPayment.getText(), is(equalTo("0")));
    }

    @Test
    void calculatePurchaseSummary_withProducts_shouldCalculateTotals() throws Exception {
        PurchaseProductVM p1 = createPurchaseProduct(
                1L,
                "Product A",
                2,
                new BigDecimal("50.00"),
                null,
                new BigDecimal("100.00"),
                new BigDecimal("20.00"));
        p1.setBuyingPriceDiscount(new BigDecimal("40.00"));

        PurchaseProductVM p2 = createPurchaseProduct(
                2L,
                "Product B",
                3,
                new BigDecimal("100.00"),
                null,
                new BigDecimal("300.00"),
                BigDecimal.ZERO);

        tblPurchaseProduct.getItems().addAll(p1, p2);
        tfAdditionalDiscount.setText("10");
        tfTax.setText("5");

        invokeCalculatePurchaseSummary();

        Integer totalProduct = getField(controller, "totalProduct");
        BigDecimal totalPrice = getField(controller, "totalPrice");
        BigDecimal totalDiscount = getField(controller, "totalDiscount");
        BigDecimal totalPayment = getField(controller, "totalPayment");

        assertThat(totalProduct, is(equalTo(5)));
        assertThat(totalPrice, comparesEqualTo(new BigDecimal("400.00")));
        assertThat(totalDiscount, comparesEqualTo(new BigDecimal("30.00")));
        // payment = 400 - 30 + 5 = 375
        assertThat(totalPayment, comparesEqualTo(new BigDecimal("375.00")));
    }

    @Test
    void calculatePurchaseSummary_withTaxAndDiscount_shouldCalculateCorrectly() throws Exception {
        PurchaseProductVM product = createPurchaseProduct(
                1L,
                "Product A",
                5,
                new BigDecimal("200"),
                null,
                new BigDecimal("1000"),
                new BigDecimal("50"));
        tblPurchaseProduct.getItems().add(product);
        tfAdditionalDiscount.setText("100");
        tfTax.setText("50");
        invokeCalculatePurchaseSummary();

        Integer totalProduct = getField(controller, "totalProduct");
        BigDecimal totalPrice = getField(controller, "totalPrice");
        BigDecimal totalDiscount = getField(controller, "totalDiscount");
        BigDecimal totalPayment = getField(controller, "totalPayment");

        assertThat(totalProduct, is(5));
        assertThat(totalPrice, comparesEqualTo(new BigDecimal("1000")));
        assertThat(totalDiscount, comparesEqualTo(new BigDecimal("150")));
        assertThat(totalPayment, comparesEqualTo(new BigDecimal("900")));
    }

    // ==========================================
    // Tests for validate (Form level)
    // ==========================================

    @Test
    void validate_withAllValidFields_shouldPass() throws Exception {
        SupplierVM supplier = new SupplierVM();
        supplier.setId(1L);
        supplier.setName("Supplier A");
        injectField(controller, "selectedSupplier", supplier);
        tfSupplier.setText("Supplier A");

        tfInvoiceNumber.setText("INV-2026-001");
        dpInvoiceDate.setValue(LocalDate.now());
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0); // PAID

        PurchaseProductVM product = new PurchaseProductVM();
        tblPurchaseProduct.getItems().add(product);

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(true));
    }

    @Test
    void validate_withMissingSupplier_shouldReturnError() throws Exception {
        injectField(controller, "selectedSupplier", null);
        tfSupplier.setText("");
        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.now());

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validate_withEmptyInvoiceNumber_shouldReturnError() throws Exception {
        SupplierVM supplier = new SupplierVM();
        supplier.setId(1L);
        supplier.setName("Supplier A");
        injectField(controller, "selectedSupplier", supplier);
        tfSupplier.setText("Supplier A");

        tfInvoiceNumber.setText("");
        dpInvoiceDate.setValue(LocalDate.now());

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validate_withNullInvoiceDate_shouldReturnError() throws Exception {
        SupplierVM supplier = new SupplierVM();
        supplier.setId(1L);
        supplier.setName("Supplier A");
        injectField(controller, "selectedSupplier", supplier);
        tfSupplier.setText("Supplier A");

        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(null);

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validate_withUnpaidStatusAndNullDueDate_shouldReturnError() throws Exception {
        SupplierVM supplier = new SupplierVM();
        supplier.setId(1L);
        supplier.setName("Supplier A");
        injectField(controller, "selectedSupplier", supplier);
        tfSupplier.setText("Supplier A");

        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.now());
        ComboBoxUtils.selectIndex(cbPaymentStatus, 1); // UNPAID
        dpDueDate.setValue(null);

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validate_withDueDateBeforeInvoiceDate_shouldReturnError() throws Exception {
        SupplierVM supplier = new SupplierVM();
        supplier.setId(1L);
        supplier.setName("Supplier A");
        injectField(controller, "selectedSupplier", supplier);
        tfSupplier.setText("Supplier A");

        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.of(2026, 5, 10));
        ComboBoxUtils.selectIndex(cbPaymentStatus, 1); // UNPAID
        dpDueDate.setValue(LocalDate.of(2026, 5, 5));

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validate_withEmptyProducts_shouldReturnError() throws Exception {
        SupplierVM supplier = new SupplierVM();
        supplier.setId(1L);
        supplier.setName("Supplier A");
        injectField(controller, "selectedSupplier", supplier);
        tfSupplier.setText("Supplier A");

        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.now());
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0); // PAID
        tblPurchaseProduct.getItems().clear();

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    // ==========================================
    // Tests for handleSelectedProduct & handleSelectedSupplier
    // ==========================================

    @Test
    void handleSelectedProduct_withEmptyResult_shouldDoNothing() throws Exception {
        Method method = PurchaseEditController.class.getDeclaredMethod("handleSelectedProduct", ChooseResultVM.class);
        method.setAccessible(true);
        method.invoke(controller, new ChooseResultVM<>(false, Optional.empty()));

        ProductVM selectedProduct = getField(controller, "selectedProduct");
        assertThat(selectedProduct, is(nullValue()));
    }

    @Test
    void handleSelectedProduct_withProduct_shouldUpdateUIFields() throws Exception {
        ProductVM product = new ProductVM();
        product.setId(1L);
        product.setName("Paracetamol");
        product.setCategoryCode("000000001");
        product.setCategoryName("Medicine");
        product.setUnitLabel("Pcs");
        product.setGeneralSellingPrice(new BigDecimal("15000.00"));

        Method method = PurchaseEditController.class.getDeclaredMethod("handleSelectedProduct", ChooseResultVM.class);
        method.setAccessible(true);
        method.invoke(controller, new ChooseResultVM<>(false, Optional.of(product)));

        ProductVM selected = getField(controller, "selectedProduct");
        assertThat(selected, is(equalTo(product)));
        assertThat(tfProduct.getText(), is(equalTo("Paracetamol")));
        assertThat(tfProductCategory.getText(), is(equalTo("Medicine")));
        assertThat(tfProductUnit.getText(), is(equalTo("Pcs")));
        assertThat(tfGeneralSellingPrice.getText(), is(equalTo("15000.0")));
    }

    @Test
    void handleSelectedProduct_withDrugsCategory_shouldEnablePrescriptionSellingPrice() throws Exception {
        ProductVM product = new ProductVM();
        product.setId(2L);
        product.setName("Amoxicillin");
        product.setCategoryCode(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
        product.setCategoryName("Drugs");
        product.setPrescriptionSellingPrice(new BigDecimal("20000.00"));

        Method method = PurchaseEditController.class.getDeclaredMethod("handleSelectedProduct", ChooseResultVM.class);
        method.setAccessible(true);
        method.invoke(controller, new ChooseResultVM<>(false, Optional.of(product)));

        assertThat(vboxPrescriptionSellingPrice.isDisable(), is(false));
        assertThat(tfPrescriptionSellingPrice.getText(), is(equalTo("20000.0")));
    }

    @Test
    void handleSelectedSupplier_withEmptyResult_shouldDoNothing() throws Exception {
        Method method = PurchaseEditController.class.getDeclaredMethod("handleSelectedSupplier", ChooseResultVM.class);
        method.setAccessible(true);
        method.invoke(controller, new ChooseResultVM<>(false, Optional.empty()));

        SupplierVM selectedSupplier = getField(controller, "selectedSupplier");
        assertThat(selectedSupplier, is(nullValue()));
    }

    @Test
    void handleSelectedSupplier_withSupplier_shouldUpdateUI() throws Exception {
        SupplierVM supplier = new SupplierVM();
        supplier.setId(1L);
        supplier.setName("Pharma Corp");

        Method method = PurchaseEditController.class.getDeclaredMethod("handleSelectedSupplier", ChooseResultVM.class);
        method.setAccessible(true);
        method.invoke(controller, new ChooseResultVM<>(false, Optional.of(supplier)));

        SupplierVM selected = getField(controller, "selectedSupplier");
        assertThat(selected, is(equalTo(supplier)));
        assertThat(tfSupplier.getText(), is(equalTo("Pharma Corp")));
    }

    // ==========================================
    // Tests for Table Actions
    // ==========================================

    @Test
    void onActionBtnRemoveProduct_withSelectedItem_shouldRemoveAndRecalculate() throws Exception {
        PurchaseProductVM p1 = createPurchaseProduct(
                1L,
                "Product A",
                1,
                new BigDecimal("100"),
                null,
                new BigDecimal("100"),
                BigDecimal.ZERO);
        PurchaseProductVM p2 = createPurchaseProduct(
                2L,
                "Product B",
                2,
                new BigDecimal("50"),
                null,
                new BigDecimal("100"),
                BigDecimal.ZERO);

        tblPurchaseProduct.getItems().addAll(p1, p2);
        tblPurchaseProduct.getSelectionModel().select(p1);

        controller.onActionBtnRemoveProduct(new ActionEvent());

        assertThat(tblPurchaseProduct.getItems(), hasSize(1));
        assertThat(tblPurchaseProduct.getItems().get(0), is(equalTo(p2)));
    }

    @Test
    void onActionBtnRemoveProduct_withNoSelection_shouldNotRemove() throws Exception {
        PurchaseProductVM p1 = createPurchaseProduct(
                1L,
                "Product A",
                1,
                new BigDecimal("100"),
                null,
                new BigDecimal("100"),
                BigDecimal.ZERO);
        tblPurchaseProduct.getItems().add(p1);
        tblPurchaseProduct.getSelectionModel().clearSelection();

        controller.onActionBtnRemoveProduct(new ActionEvent());

        assertThat(tblPurchaseProduct.getItems(), hasSize(1));
    }

    @Test
    void handleActionTablePurchaseProduct_withSelectedItem_shouldPopulateInputs() throws Exception {
        PurchaseProductVM item = createPurchaseProduct(
                1L,
                "Test Drug",
                10,
                new BigDecimal("50.00"),
                DiscountType.FIXED_AMOUNT.toString(),
                new BigDecimal("500.00"),
                BigDecimal.ZERO);
        item.setProductCategoryCode("000000001");
        item.setProductCategoryName("Category A");
        item.setProductUnitLabel("Box");
        item.setBatchNumber("BATCH123");
        item.setExpiredDate(LocalDate.of(2027, 1, 1));
        item.setDiscountAmount(new BigDecimal("5.00"));
        item.setBuyingPriceDiscount(new BigDecimal("45.00"));

        tblPurchaseProduct.getItems().add(item);
        tblPurchaseProduct.getSelectionModel().select(item);

        Method method = PurchaseEditController.class.getDeclaredMethod("handleActionTablePurchaseProduct");
        method.setAccessible(true);
        method.invoke(controller);

        assertThat(tfProduct.getText(), is(equalTo("Test Drug")));
        assertThat(tfProductQuantity.getText(), is(equalTo("10")));
        assertThat(tfBuyingPrice.getText(), is(equalTo("50.0")));
        assertThat(tfBatchNumber.getText(), is(equalTo("BATCH123")));
        assertThat(dpExpiredDate.getValue(), is(equalTo(LocalDate.of(2027, 1, 1))));
        assertThat(tfDiscountAmount.getText(), is(equalTo("5.0")));
        assertThat(tfBuyingPriceDiscount.getText(), is(equalTo("45.0")));
    }

    // ==========================================
    // Tests for onActionBtnRemove (Purchase Removal)
    // ==========================================

    @Test
    void onActionBtnRemove_whenConfirmed_shouldRemovePurchaseAndClose() throws Exception {
        PurchaseVM currentPurchase = new PurchaseVM();
        currentPurchase.setId(100L);
        injectField(controller, "currentPurchase", currentPurchase);

        controller.confirmationResult = new AlertResult(Optional.of(ButtonType.YES));

        controller.onActionBtnRemove(new ActionEvent());

        verify(mockPurchaseService).removePurchases(List.of(100L));
        assertThat(controller.closed, is(true));
    }

    @Test
    void onActionBtnRemove_whenCancelled_shouldNotRemovePurchase() throws Exception {
        PurchaseVM currentPurchase = new PurchaseVM();
        currentPurchase.setId(100L);
        injectField(controller, "currentPurchase", currentPurchase);

        controller.confirmationResult = new AlertResult(Optional.of(ButtonType.NO));

        controller.onActionBtnRemove(new ActionEvent());

        verify(mockPurchaseService, never()).removePurchases(anyList());
        assertThat(controller.closed, is(false));
    }

    // ==========================================
    // Tests for initDataSaveControlValues & save
    // ==========================================

    @Test
    void initDataSaveControlValues_shouldPopulateFieldsFromCurrentPurchase() throws Exception {
        PurchaseVM purchase = new PurchaseVM();
        purchase.setId(123L);
        purchase.setSupplierId(1L);
        purchase.setSupplierName("Alpha Pharma");
        purchase.setInvoiceNumber("INV-888");
        purchase.setInvoiceDate(LocalDate.of(2026, 6, 1));
        purchase.setPaymentStatus(PaymentStatus.PAID.toString());
        purchase.setPaymentDueDate(null);
        purchase.setAdditionalDiscount(new BigDecimal("10.00"));
        purchase.setTotalDiscount(new BigDecimal("25.00"));
        purchase.setTax(new BigDecimal("5.00"));
        purchase.setTotalProduct(2);
        purchase.setTotalPrice(new BigDecimal("200.00"));
        purchase.setTotalPayment(new BigDecimal("180.00"));

        PurchaseProductVM p1 = createPurchaseProduct(
                1L,
                "Product A",
                2,
                new BigDecimal("100"),
                null,
                new BigDecimal("200"),
                BigDecimal.ZERO);
        when(mockPurchaseService.getPurchaseProducts(123L)).thenReturn(List.of(p1));

        controller.pageDataToReturn = purchase;

        Method method = PurchaseEditController.class.getDeclaredMethod("initDataSaveControlValues");
        method.setAccessible(true);
        method.invoke(controller);

        assertThat(tfSupplier.getText(), is(equalTo("Alpha Pharma")));
        assertThat(tfInvoiceNumber.getText(), is(equalTo("INV-888")));
        assertThat(dpInvoiceDate.getValue(), is(equalTo(LocalDate.of(2026, 6, 1))));
        assertThat(tblPurchaseProduct.getItems(), hasSize(1));
    }

    @Test
    void save_shouldCallPurchaseServiceUpdate() throws Exception {
        SupplierVM supplier = new SupplierVM();
        supplier.setId(1L);
        injectField(controller, "selectedSupplier", supplier);

        PurchaseVM currentPurchase = new PurchaseVM();
        currentPurchase.setId(123L);
        injectField(controller, "currentPurchase", currentPurchase);

        tfInvoiceNumber.setText("INV-999");
        dpInvoiceDate.setValue(LocalDate.of(2026, 7, 1));
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0); // PAID
        tfTax.setText("10.00");
        injectField(controller, "additionalDiscount", new BigDecimal("5.00"));
        injectField(controller, "totalDiscount", new BigDecimal("15.00"));
        injectField(controller, "totalProduct", 3);
        injectField(controller, "totalPrice", new BigDecimal("300.00"));
        injectField(controller, "totalPayment", new BigDecimal("295.00"));

        Method method = PurchaseEditController.class.getDeclaredMethod("save");
        method.setAccessible(true);
        Object result = method.invoke(controller);

        assertThat(result, is(equalTo(true)));
        verify(mockPurchaseService).updatePurchase(any(PurchaseEditVM.class), eq(123L));
    }

    // ==========================================
    // Helper Methods
    // ==========================================

    private PurchaseProductVM createPurchaseProduct(
            Long productId,
            String name,
            int qty,
            BigDecimal buyingPrice,
            String discountType,
            BigDecimal subtotalPrice,
            BigDecimal subtotalDiscount) {
        PurchaseProductVM vm = new PurchaseProductVM();
        vm.setProductId(productId);
        vm.setProductName(name);
        vm.setQuantity(qty);
        vm.setBuyingPrice(buyingPrice);
        vm.setDiscountType(discountType);
        vm.setSubtotalPrice(subtotalPrice);
        vm.setSubtotalDiscount(subtotalDiscount);
        return vm;
    }

    private void setSelectedProduct(String categoryCode) throws Exception {
        ProductVM product = new ProductVM();
        product.setId(99L);
        product.setName("TestProduct");
        product.setCategoryCode(categoryCode);
        injectField(controller, "selectedProduct", product);
    }

    private ValidationResult invokeValidateAddProduct(
            boolean isProductSelected,
            boolean isProductCategoryDrugs,
            LocalDate expiredDate) throws Exception {
        Method method = PurchaseEditController.class
                .getDeclaredMethod("validateAddProduct", boolean.class, boolean.class, LocalDate.class);
        method.setAccessible(true);
        return (ValidationResult) method.invoke(controller, isProductSelected, isProductCategoryDrugs, expiredDate);
    }

    private void invokeCalculatePurchaseSummary() throws Exception {
        Method method = PurchaseEditController.class.getDeclaredMethod("calculatePurchaseSummary");
        method.setAccessible(true);
        method.invoke(controller);
    }

    private void invokeCalculateBuyingPriceDiscount() throws Exception {
        Method method = PurchaseEditController.class.getDeclaredMethod("calculateBuyingPriceDiscount", Locale.class);
        method.setAccessible(true);
        method.invoke(controller, Locale.US);
    }

    private ValidationResult invokeValidate() throws Exception {
        com.pinodesk.pandora.utility.ControlValidator validator = new com.pinodesk.pandora.utility.ControlValidator(
                mockResources);
        Method method = PurchaseEditController.class
                .getDeclaredMethod("validate", com.pinodesk.pandora.utility.ControlValidator.class);
        method.setAccessible(true);
        method.invoke(controller, validator);
        return validator.getResult();
    }

    private static void injectField(Object target, String fieldName, Object value) throws Exception {
        injectSuperField(target, fieldName, value);
    }

    private static void injectSuperField(Object target, String fieldName, Object value) throws Exception {
        Class<?> clazz = target.getClass();
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (field != null) {
            field.setAccessible(true);
            field.set(target, value);
        } else {
            throw new NoSuchFieldException(fieldName);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object target, String fieldName) throws Exception {
        Class<?> clazz = target.getClass();
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (field != null) {
            field.setAccessible(true);
            return (T) field.get(target);
        } else {
            throw new NoSuchFieldException(fieldName);
        }
    }
}
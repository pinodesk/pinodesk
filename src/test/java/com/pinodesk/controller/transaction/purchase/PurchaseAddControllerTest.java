package com.pinodesk.controller.transaction.purchase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.hamcrest.Matchers;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pinodesk.JavaFXTestBase;
import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.DiscountType;
import com.pinodesk.constant.PaymentStatus;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.Translator;
import com.pinodesk.pandora.utility.ValidationResult;
import com.pinodesk.viewmodel.ChooseResultVM;
import com.pinodesk.viewmodel.ProductVM;
import com.pinodesk.viewmodel.PurchaseProductVM;
import com.pinodesk.viewmodel.SupplierVM;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * JavaFX unit tests for PurchaseAddController.
 * <p>
 * Tests run headlessly via TestFX + Monocle, compatible with GitHub Actions CI.
 * Each test instantiates the controller and injects mocked dependencies and
 * FXML controls via reflection to test business logic in isolation from the
 * FXML loader.
 * </p>
 */
public class PurchaseAddControllerTest extends JavaFXTestBase {

    private PurchaseAddController controller;
    private ResourceBundle mockResources;
    private Translator mockTranslator;

    // FXML controls
    private TextField tfSupplier;
    private TextField tfInvoiceNumber;
    private DatePicker dpInvoiceDate;
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;
    private VBox vboxDueDate;
    private DatePicker dpDueDate;
    private TextField tfTax;
    private TextField tfProduct;
    private TextField tfProductCategory;
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
    private TableView<PurchaseProductVM> tblPurchaseProduct;
    private Label lblTotalProduct;
    private Label lblTotalPrice;
    private Label lblTotalDiscount;
    private Label lblTotalPriceDiscount;
    private Label lblTax;
    private Label lblTotalPayment;
    private VBox vboxDiscountAmount;
    private VBox vboxBuyingPriceDiscount;

    @BeforeEach
    void setUp() throws Exception {
        controller = new PurchaseAddController();
        mockResources = mock(ResourceBundle.class);
        mockTranslator = mock(Translator.class);
        when(mockResources.getLocale()).thenReturn(Locale.US);
        when(mockTranslator.translate(any(String.class))).thenAnswer(inv -> inv.getArgument(0));

        // Create FXML controls
        tfSupplier = new TextField();
        tfInvoiceNumber = new TextField();
        dpInvoiceDate = new DatePicker();
        cbPaymentStatus = new ComboBox<>();
        vboxDueDate = new VBox();
        dpDueDate = new DatePicker();
        tfTax = new TextField();
        tfProduct = new TextField();
        tfProductCategory = new TextField();
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
        tblPurchaseProduct = new TableView<>(FXCollections.observableArrayList());
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
        injectField(controller, "tfSupplier", tfSupplier);
        injectField(controller, "tfInvoiceNumber", tfInvoiceNumber);
        injectField(controller, "dpInvoiceDate", dpInvoiceDate);
        injectField(controller, "cbPaymentStatus", cbPaymentStatus);
        injectField(controller, "vboxDueDate", vboxDueDate);
        injectField(controller, "dpDueDate", dpDueDate);
        injectField(controller, "tfTax", tfTax);
        injectField(controller, "tfProduct", tfProduct);
        injectField(controller, "tfProductCategory", tfProductCategory);
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
        injectField(controller, "tblPurchaseProduct", tblPurchaseProduct);
        injectField(controller, "lblTotalProduct", lblTotalProduct);
        injectField(controller, "lblTotalPrice", lblTotalPrice);
        injectField(controller, "lblTotalDiscount", lblTotalDiscount);
        injectField(controller, "lblTotalPriceDiscount", lblTotalPriceDiscount);
        injectField(controller, "lblTax", lblTax);
        injectField(controller, "lblTotalPayment", lblTotalPayment);
        injectField(controller, "vboxDiscountAmount", vboxDiscountAmount);
        injectField(controller, "vboxBuyingPriceDiscount", vboxBuyingPriceDiscount);

        // Inject into BaseController hierarchy
        injectField(controller, "resources", mockResources, "com.pinodesk.controller.BaseController");
        injectField(controller, "t", mockTranslator, "com.pinodesk.controller.BaseController");
    }

    // -------------------------------------------------------------------------
    // handleSelectedProduct tests
    // -------------------------------------------------------------------------

    @Test
    void handleSelectedProduct_withNullResult_shouldDoNothing() {
        controller.handleSelectedProduct(null);
        assertThat(tfProduct.getText(), is(""));
    }

    @Test
    void handleSelectedProduct_withCancelledResult_shouldDoNothing() {
        tfProduct.setText("SomeProduct");
        ChooseResultVM<ProductVM> result = new ChooseResultVM<>(true, Optional.empty());
        controller.handleSelectedProduct(result);
        // Text should remain unchanged because cancelled results are ignored
        assertThat(tfProduct.getText(), is("SomeProduct"));
    }

    @Test
    void handleSelectedProduct_withEmptyData_shouldClearFields() {
        tfProduct.setText("SomeProduct");
        tfProductCategory.setText("SomeCategory");
        tfProductUnit.setText("SomeUnit");
        tfGeneralSellingPrice.setText("100");
        tfPrescriptionSellingPrice.setText("200");

        ChooseResultVM<ProductVM> result = new ChooseResultVM<>(false, Optional.empty());
        controller.handleSelectedProduct(result);

        assertThat(tfProduct.getText(), is(""));
        assertThat(tfProductCategory.getText(), is(""));
        assertThat(tfProductUnit.getText(), is(""));
        assertThat(tfGeneralSellingPrice.getText(), is(""));
        assertThat(tfPrescriptionSellingPrice.getText(), is(""));
    }

    @Test
    void handleSelectedProduct_withValidProduct_shouldPopulateFields() {
        ProductVM product = createTestProduct(
                1L,
                "Paracetamol",
                "Drugs",
                "000000518",
                "Tablet",
                new BigDecimal("5000"),
                new BigDecimal("7500"));

        ChooseResultVM<ProductVM> result = new ChooseResultVM<>(false, Optional.of(product));
        controller.handleSelectedProduct(result);

        assertThat(tfProduct.getText(), is("Paracetamol"));
        assertThat(tfProductCategory.getText(), is("Drugs"));
        assertThat(tfProductUnit.getText(), is("Tablet"));
        assertThat(tfGeneralSellingPrice.getText(), is("5000.0"));
        assertThat(tfPrescriptionSellingPrice.getText(), is("7500.0"));
        assertThat(vboxPrescriptionSellingPrice.isDisable(), is(false));
    }

    @Test
    void handleSelectedProduct_withNonDrugsCategory_shouldDisablePrescriptionField() {
        ProductVM product = createTestProduct(
                2L,
                "Vitamin C",
                "Supplement",
                "000000999",
                "Box",
                new BigDecimal("3000"),
                null);

        ChooseResultVM<ProductVM> result = new ChooseResultVM<>(false, Optional.of(product));
        controller.handleSelectedProduct(result);

        assertThat(tfProduct.getText(), is("Vitamin C"));
        assertThat(vboxPrescriptionSellingPrice.isDisable(), is(true));
    }

    @Test
    void handleSelectedProduct_withNullPrices_shouldNotSetPriceText() {
        ProductVM product = createTestProduct(3L, "Bandage", "General", "000000001", "Piece", null, null);

        ChooseResultVM<ProductVM> result = new ChooseResultVM<>(false, Optional.of(product));
        controller.handleSelectedProduct(result);

        assertThat(tfProduct.getText(), is("Bandage"));
        assertThat(tfGeneralSellingPrice.getText(), is(""));
        assertThat(tfPrescriptionSellingPrice.getText(), is(""));
    }

    // -------------------------------------------------------------------------
    // handleSelectedSupplier tests
    // -------------------------------------------------------------------------

    @Test
    void handleSelectedSupplier_withNullResult_shouldDoNothing() {
        controller.handleSelectedSupplier(null);
        assertThat(tfSupplier.getText(), is(""));
    }

    @Test
    void handleSelectedSupplier_withCancelledResult_shouldDoNothing() {
        tfSupplier.setText("ExistingSupplier");
        ChooseResultVM<SupplierVM> result = new ChooseResultVM<>(true, Optional.empty());
        controller.handleSelectedSupplier(result);
        assertThat(tfSupplier.getText(), is("ExistingSupplier"));
    }

    @Test
    void handleSelectedSupplier_withValidSupplier_shouldPopulateField() {
        SupplierVM supplier = new SupplierVM();
        supplier.setId(1L);
        supplier.setName("PT Kimia Farma");

        ChooseResultVM<SupplierVM> result = new ChooseResultVM<>(false, Optional.of(supplier));
        controller.handleSelectedSupplier(result);

        assertThat(tfSupplier.getText(), is("PT Kimia Farma"));
        SupplierVM selected = getField(controller, "selectedSupplier");
        assertThat(selected, is(notNullValue()));
        assertThat(selected.getId(), is(1L));
    }

    @Test
    void handleSelectedSupplier_withEmptyData_shouldClearField() {
        tfSupplier.setText("OldSupplier");
        ChooseResultVM<SupplierVM> result = new ChooseResultVM<>(false, Optional.empty());
        controller.handleSelectedSupplier(result);

        assertThat(tfSupplier.getText(), is(""));
        SupplierVM selected = getField(controller, "selectedSupplier");
        assertThat(selected, is(nullValue()));
    }

    // -------------------------------------------------------------------------
    // validateAddProduct tests
    // -------------------------------------------------------------------------

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
        // Set selectedProduct with a non-custom-package category
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
    void validateAddProduct_fixedAmountDiscountGreaterThanBuyingPrice_shouldReturnError() throws Exception {
        tfProductQuantity.setText("10");
        tfBuyingPrice.setText("50");
        tfGeneralSellingPrice.setText("");
        tfPrescriptionSellingPrice.setText("");
        tfDiscountAmount.setText("100");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        setSelectedProduct("000000001");
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validateAddProduct_fixedAmountDiscountLessThanBuyingPrice_shouldPass() throws Exception {
        tfProductQuantity.setText("10");
        tfBuyingPrice.setText("100");
        tfGeneralSellingPrice.setText("");
        tfPrescriptionSellingPrice.setText("");
        tfDiscountAmount.setText("10");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        setSelectedProduct("000000001");
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result.isValid(), is(true));
    }

    @Test
    void validateAddProduct_withNullBuyingPriceAndFixedDiscount_shouldNotThrowNPE() throws Exception {
        tfProductQuantity.setText("10");
        tfBuyingPrice.setText("");
        tfGeneralSellingPrice.setText("");
        tfPrescriptionSellingPrice.setText("");
        tfDiscountAmount.setText("10");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        setSelectedProduct("000000001");
        // Should not throw NPE — the original bug was a NPE here
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result, is(notNullValue()));
    }

    @Test
    void validateAddProduct_withNullDiscountAmountAndFixedDiscount_shouldNotThrowNPE() throws Exception {
        tfProductQuantity.setText("10");
        tfBuyingPrice.setText("100");
        tfGeneralSellingPrice.setText("");
        tfPrescriptionSellingPrice.setText("");
        tfDiscountAmount.setText("");
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        setSelectedProduct("000000001");
        // Should not throw NPE
        ValidationResult result = invokeValidateAddProduct(true, false, null);
        assertThat(result, is(notNullValue()));
    }

    // -------------------------------------------------------------------------
    // calculatePurchaseSummary tests
    // -------------------------------------------------------------------------

    @Test
    void calculatePurchaseSummary_withNoProducts_shouldShowZeros() throws Exception {
        tfAdditionalDiscount.setText("");
        tfTax.setText("");
        invokeCalculatePurchaseSummary();

        assertThat(lblTotalProduct.getText(), is("0"));
        assertThat(lblTotalPrice.getText(), is("0"));
        assertThat(lblTotalPayment.getText(), is("0"));
    }

    @Test
    void calculatePurchaseSummary_withOneProduct_shouldCalculateCorrectly() throws Exception {
        PurchaseProductVM product = createPurchaseProduct(
                1L,
                "Product A",
                10,
                new BigDecimal("100"),
                null,
                new BigDecimal("1000"),
                BigDecimal.ZERO);
        tblPurchaseProduct.getItems().add(product);
        tfAdditionalDiscount.setText("");
        tfTax.setText("");
        invokeCalculatePurchaseSummary();

        Integer totalProduct = getField(controller, "totalProduct");
        BigDecimal totalPrice = getField(controller, "totalPrice");
        BigDecimal totalPayment = getField(controller, "totalPayment");

        assertThat(totalProduct, is(10));
        assertThat(totalPrice, comparesEqualTo(new BigDecimal("1000")));
        assertThat(totalPayment, comparesEqualTo(new BigDecimal("1000")));
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
        // totalPrice = buyingPrice * quantity = 200 * 5 = 1000
        assertThat(totalPrice, comparesEqualTo(new BigDecimal("1000")));
        // totalDiscount = subtotalDiscount(50) + additionalDiscount(100) = 150
        assertThat(totalDiscount, comparesEqualTo(new BigDecimal("150")));
        // totalPayment = totalPrice(1000) + tax(50) - totalDiscount(150) = 900
        assertThat(totalPayment, comparesEqualTo(new BigDecimal("900")));
    }

    @Test
    void calculatePurchaseSummary_withMultipleProducts_shouldSumCorrectly() throws Exception {
        PurchaseProductVM product1 = createPurchaseProduct(
                1L,
                "Product A",
                2,
                new BigDecimal("100"),
                null,
                new BigDecimal("200"),
                BigDecimal.ZERO);
        PurchaseProductVM product2 = createPurchaseProduct(
                2L,
                "Product B",
                3,
                new BigDecimal("50"),
                null,
                new BigDecimal("150"),
                BigDecimal.ZERO);
        tblPurchaseProduct.getItems().addAll(product1, product2);
        tfAdditionalDiscount.setText("");
        tfTax.setText("");
        invokeCalculatePurchaseSummary();

        Integer totalProduct = getField(controller, "totalProduct");
        BigDecimal totalPrice = getField(controller, "totalPrice");

        assertThat(totalProduct, is(5));
        // totalPrice = (100*2) + (50*3) = 200 + 150 = 350
        assertThat(totalPrice, comparesEqualTo(new BigDecimal("350")));
    }

    // -------------------------------------------------------------------------
    // calculateBuyingPriceDiscount tests
    // -------------------------------------------------------------------------

    @Test
    void calculateBuyingPriceDiscount_withPercentageDiscount_shouldCalculateCorrectly() throws Exception {
        ComboBoxUtils.selectIndex(cbDiscountType, 1); // PERCENTAGE
        tfBuyingPrice.setText("1000");
        tfDiscountAmount.setText("10");
        invokeCalculateBuyingPriceDiscount();

        // 10% of 1000 = 100 discount, price after discount = 900
        assertThat(tfBuyingPriceDiscount.getText(), is("900.0"));
    }

    @Test
    void calculateBuyingPriceDiscount_withFixedAmountDiscount_shouldCalculateCorrectly() throws Exception {
        ComboBoxUtils.selectIndex(cbDiscountType, 2); // FIXED_AMOUNT
        tfBuyingPrice.setText("1000");
        tfDiscountAmount.setText("250");
        invokeCalculateBuyingPriceDiscount();

        // 1000 - 250 = 750
        assertThat(tfBuyingPriceDiscount.getText(), is("750.0"));
    }

    @Test
    void calculateBuyingPriceDiscount_withBlankBuyingPrice_shouldClearResult() throws Exception {
        ComboBoxUtils.selectIndex(cbDiscountType, 1); // PERCENTAGE
        tfBuyingPrice.setText("");
        tfDiscountAmount.setText("10");
        invokeCalculateBuyingPriceDiscount();

        assertThat(tfBuyingPriceDiscount.getText(), is(""));
    }

    @Test
    void calculateBuyingPriceDiscount_withBlankDiscountAmount_shouldClearResult() throws Exception {
        ComboBoxUtils.selectIndex(cbDiscountType, 1); // PERCENTAGE
        tfBuyingPrice.setText("1000");
        tfDiscountAmount.setText("");
        invokeCalculateBuyingPriceDiscount();

        assertThat(tfBuyingPriceDiscount.getText(), is(""));
    }

    // -------------------------------------------------------------------------
    // validate (form-level) tests
    // -------------------------------------------------------------------------

    @Test
    void validate_withAllFieldsEmpty_shouldReturnErrors() throws Exception {
        tfSupplier.setText("");
        tfInvoiceNumber.setText("");
        dpInvoiceDate.setValue(null);

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
        // Should have errors for: supplier, invoice number, invoice date, empty product
        // table
        assertThat(result.getMessages().size() >= 4, is(true));
    }

    @Test
    void validate_withInvoiceDateAfterToday_shouldReturnError() throws Exception {
        tfSupplier.setText("Supplier");
        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.now().plusDays(1));
        tblPurchaseProduct.getItems()
                .add(createPurchaseProduct(1L, "P", 1, BigDecimal.TEN, null, BigDecimal.TEN, BigDecimal.ZERO));

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validate_unpaidWithNoDueDate_shouldReturnError() throws Exception {
        tfSupplier.setText("Supplier");
        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.now());
        ComboBoxUtils.selectIndex(cbPaymentStatus, 1); // UNPAID
        dpDueDate.setValue(null);
        tblPurchaseProduct.getItems()
                .add(createPurchaseProduct(1L, "P", 1, BigDecimal.TEN, null, BigDecimal.TEN, BigDecimal.ZERO));

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validate_unpaidWithDueDateBeforeToday_shouldReturnError() throws Exception {
        tfSupplier.setText("Supplier");
        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.now());
        ComboBoxUtils.selectIndex(cbPaymentStatus, 1); // UNPAID
        dpDueDate.setValue(LocalDate.now().minusDays(1));
        tblPurchaseProduct.getItems()
                .add(createPurchaseProduct(1L, "P", 1, BigDecimal.TEN, null, BigDecimal.TEN, BigDecimal.ZERO));

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    @Test
    void validate_withValidPaidForm_shouldPass() throws Exception {
        tfSupplier.setText("Supplier");
        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.now());
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0); // PAID
        tblPurchaseProduct.getItems()
                .add(createPurchaseProduct(1L, "P", 1, BigDecimal.TEN, null, BigDecimal.TEN, BigDecimal.ZERO));

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(true));
    }

    @Test
    void validate_withValidUnpaidForm_shouldPass() throws Exception {
        tfSupplier.setText("Supplier");
        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.now());
        ComboBoxUtils.selectIndex(cbPaymentStatus, 1); // UNPAID
        dpDueDate.setValue(LocalDate.now().plusDays(30));
        tblPurchaseProduct.getItems()
                .add(createPurchaseProduct(1L, "P", 1, BigDecimal.TEN, null, BigDecimal.TEN, BigDecimal.ZERO));

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(true));
    }

    @Test
    void validate_withEmptyProductTable_shouldReturnError() throws Exception {
        tfSupplier.setText("Supplier");
        tfInvoiceNumber.setText("INV-001");
        dpInvoiceDate.setValue(LocalDate.now());
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0); // PAID

        ValidationResult result = invokeValidate();
        assertThat(result.isValid(), is(false));
    }

    // -------------------------------------------------------------------------
    // onActionBtnRemoveProduct tests (via table manipulation)
    // -------------------------------------------------------------------------

    @Test
    void removeProduct_whenTableHasItems_shouldDecreaseCount() throws Exception {
        PurchaseProductVM product = createPurchaseProduct(
                1L,
                "P",
                5,
                BigDecimal.TEN,
                null,
                new BigDecimal("50"),
                BigDecimal.ZERO);
        tblPurchaseProduct.getItems().add(product);
        assertThat(tblPurchaseProduct.getItems(), hasSize(1));

        tblPurchaseProduct.getItems().remove(product);
        assertThat(tblPurchaseProduct.getItems(), is(empty()));
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    private ProductVM createTestProduct(
            Long id,
            String name,
            String categoryName,
            String categoryCode,
            String unitLabel,
            BigDecimal generalPrice,
            BigDecimal prescriptionPrice) {
        ProductVM product = new ProductVM();
        product.setId(id);
        product.setName(name);
        product.setCategoryName(categoryName);
        product.setCategoryCode(categoryCode);
        product.setUnitLabel(unitLabel);
        product.setGeneralSellingPrice(generalPrice);
        product.setPrescriptionSellingPrice(prescriptionPrice);
        return product;
    }

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
        Method method = PurchaseAddController.class
                .getDeclaredMethod("validateAddProduct", boolean.class, boolean.class, LocalDate.class);
        method.setAccessible(true);
        return (ValidationResult) method.invoke(controller, isProductSelected, isProductCategoryDrugs, expiredDate);
    }

    private void invokeCalculatePurchaseSummary() throws Exception {
        Method method = PurchaseAddController.class.getDeclaredMethod("calculatePurchaseSummary");
        method.setAccessible(true);
        method.invoke(controller);
    }

    private void invokeCalculateBuyingPriceDiscount() throws Exception {
        Method method = PurchaseAddController.class.getDeclaredMethod("calculateBuyingPriceDiscount", Locale.class);
        method.setAccessible(true);
        method.invoke(controller, Locale.US);
    }

    private ValidationResult invokeValidate() throws Exception {
        com.pinodesk.pandora.utility.ControlValidator validator = new com.pinodesk.pandora.utility.ControlValidator(
                mockResources);
        Method method = PurchaseAddController.class
                .getDeclaredMethod("validate", com.pinodesk.pandora.utility.ControlValidator.class);
        method.setAccessible(true);
        method.invoke(controller, validator);
        return validator.getResult();
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        injectField(target, fieldName, value, null);
    }

    private void injectField(Object target, String fieldName, Object value, String declaringClassName)
            throws Exception {
        Class<?> clazz = declaringClassName != null ? Class.forName(declaringClassName) : target.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String fieldName) {
        try {
            Class<?> clazz = target.getClass();
            while (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return (T) field.get(target);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            throw new NoSuchFieldException(fieldName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Matcher<T> not(Matcher<T> matcher) {
        return Matchers.not(matcher);
    }
}
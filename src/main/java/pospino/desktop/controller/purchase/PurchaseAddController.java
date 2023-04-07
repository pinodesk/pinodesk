package pospino.desktop.controller.purchase;

import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.formatOrDefault;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toBigDecimalOrZero;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toStringOrEmpty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.mudiasoft.pandora.control.MaskedTextField;
import com.gitlab.mudiasoft.pandora.factory.LocalDateCellFactory;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.model.SimpleComboBoxModel;
import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;
import com.gitlab.mudiasoft.pandora.utility.ValidationResult;
import com.gitlab.mudiasoft.toolbox.data.DateTimeUtils;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.service.PurchaseService;
import pospino.desktop.util.ProductUtils;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.ProductVM;
import pospino.desktop.viewmodel.PurchaseAddVM;
import pospino.desktop.viewmodel.PurchaseProductVM;
import pospino.desktop.viewmodel.SupplierVM;

public class PurchaseAddController extends CommonDataSaveController {

    @FXML
    private TextField tfSupplier;

    @FXML
    private Button btnNewSupplier;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private MaskedTextField tfInvoiceDate;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private VBox vboxDueDate;

    @FXML
    private MaskedTextField tfDueDate;

    @FXML
    private TextField tfDiscount;

    @FXML
    private TextField tfTax;

    @FXML
    private TextField tfProduct;

    @FXML
    private Button btnNewProduct;

    @FXML
    private TextField tfProductCategory;

    @FXML
    private VBox vboxUnit;

    @FXML
    private TextField tfProductUnit;

    @FXML
    private TextField tfProductQuantity;

    @FXML
    private TextField tfBuyingPrice;

    @FXML
    private TextField tfGeneralSellingPrice;

    @FXML
    private VBox vboxPrescriptionSellingPrice;

    @FXML
    private TextField tfPrescriptionSellingPrice;

    @FXML
    private TextField tfBatchNumber;

    @FXML
    private MaskedTextField tfExpiredDate;

    @FXML
    private Button btnAddProduct;

    @FXML
    private TableView<PurchaseProductVM> tblPurchaseProduct;

    @FXML
    private TableColumn<PurchaseProductVM, String> colProductName;

    @FXML
    private TableColumn<PurchaseProductVM, String> colProductCategory;

    @FXML
    private TableColumn<PurchaseProductVM, String> colUnit;

    @FXML
    private TableColumn<PurchaseProductVM, Integer> colQuantity;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colBuyingPrice;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colSubtotal;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colGeneralSellingPrice;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colPrescriptionSellingPrice;

    @FXML
    private TableColumn<PurchaseProductVM, String> colBactchNumber;

    @FXML
    private TableColumn<PurchaseProductVM, LocalDate> colExpiredDate;

    @FXML
    private Label lblTotalProduct;

    @FXML
    private Label lblTotalPurchase;

    @FXML
    private Label lblDiscount;

    @FXML
    private Label lblTax;

    @FXML
    private Label lblTotalPayment;

    @FXML
    private Button btnSaveAndAdd;

    private ProductVM selectedProduct;
    private SupplierVM selectedSupplier;
    private Integer totalProduct;
    private BigDecimal totalPurchase;
    private BigDecimal totalPayment;

    private PurchaseService purchaseService;

    @FXML
    void onActionBtnNewProduct(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_PRODUCT_ADD, false, we -> {
            if (Boolean.TRUE.equals(getPageData())) {
                displayInfo(MessageCode.SUCCESS_ADD_PRODUCT);
            }
        });
    }

    @FXML
    void onActionBtnNewSupplier(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_SUPPLIER_ADD, false, we -> {
            if (getPageData() != null) {
                displayInfo(MessageCode.SUCCESS_ADD_SUPPLIER);
            }
        });
    }

    @FXML
    void onActionBtnRemoveProduct(ActionEvent event) {
        if (TableViewUtils.hasItemSelected(tblPurchaseProduct)) {
            tblPurchaseProduct.getItems().remove(TableViewUtils.getSelectedItem(tblPurchaseProduct));
            calculatePurchaseSummary();
        }
        if (tblPurchaseProduct.getItems().isEmpty()) {
            tblPurchaseProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        }
    }

    @FXML
    void onActionBtnAddProduct(ActionEvent event) {
        boolean isProductSelected = selectedProduct != null;
        boolean isProductCategoryDrugs = isProductSelected
                && ProductUtils.isProductCategoryDrugs(selectedProduct.getCategoryCode());
        LocalDate expiredDate = DateTimeUtils
                .parseLocalDateQuietly(tfExpiredDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN);
        ValidationResult validationResult = validateAddProduct(isProductSelected, isProductCategoryDrugs, expiredDate);
        if (!validationResult.isValid()) {
            displayError(validationResult.getMessages());
            return;
        }
        Integer quantity = toIntegerOrNull(tfProductQuantity.getText());
        BigDecimal buyingPrice = toBigDecimalOrNull(tfBuyingPrice.getText());
        BigDecimal generalSellingPrice = toBigDecimalOrNull(tfGeneralSellingPrice.getText());
        PurchaseProductVM purchaseProduct = new PurchaseProductVM();
        purchaseProduct.setProductId(selectedProduct.getId());
        purchaseProduct.setProductName(selectedProduct.getName());
        purchaseProduct.setProductCategoryCode(selectedProduct.getCategoryCode());
        purchaseProduct.setProductCategoryName(selectedProduct.getCategoryName());
        purchaseProduct.setProductUnitLabel(selectedProduct.getUnitLabel());
        purchaseProduct.setQuantity(quantity);
        purchaseProduct.setBuyingPrice(buyingPrice);
        purchaseProduct.setSubtotal(buyingPrice.multiply(BigDecimal.valueOf(quantity)));
        purchaseProduct.setGeneralSellingPrice(generalSellingPrice);
        if (isProductCategoryDrugs) {
            BigDecimal prescriptionSellingPrice = toBigDecimalOrNull(tfPrescriptionSellingPrice.getText());
            purchaseProduct.setPrescriptionSellingPrice(prescriptionSellingPrice);
        }
        purchaseProduct.setBatchNumber(tfBatchNumber.getText());
        purchaseProduct.setExpiredDate(expiredDate);
        int idx = getProductIndexInTable(selectedProduct, tblPurchaseProduct);
        if (idx != -1) {
            tblPurchaseProduct.getItems().remove(idx);
        }
        tblPurchaseProduct.getItems().add(purchaseProduct);
        selectedProduct = null;
        TextFieldUtils.setTextEmpty(
                tfProduct,
                tfProductCategory,
                tfProductUnit,
                tfProductQuantity,
                tfBuyingPrice,
                tfGeneralSellingPrice,
                tfPrescriptionSellingPrice,
                tfBatchNumber);
        tfExpiredDate.setPlainText("");
        calculatePurchaseSummary();
    }

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_PURCHASE);
            resetControls();
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
        Locale locale = resources.getLocale();
        TextFieldUtils.setDigitTextFields(
                tfDiscount,
                tfTax,
                tfBuyingPrice,
                tfGeneralSellingPrice,
                tfPrescriptionSellingPrice,
                tfProductQuantity);
        TableViewUtils.setColumnValue(colProductName, PurchaseProductVM::getProductName);
        TableViewUtils.setColumnValue(colUnit, PurchaseProductVM::getProductUnitLabel);
        TableViewUtils.setColumnValue(colBactchNumber, PurchaseProductVM::getBatchNumber);
        TableViewUtils.setColumnValue(colProductCategory, PurchaseProductVM::getProductCategoryName);
        TableViewUtils.initTableColumn(
                colBuyingPrice,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getBuyingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSubtotal,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getSubtotal,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colGeneralSellingPrice,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getGeneralSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPrescriptionSellingPrice,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getPrescriptionSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colExpiredDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PurchaseProductVM::getExpiredDate);
        tblPurchaseProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblPurchaseProduct.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblPurchaseProduct.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTablePurchaseProduct();
            }
        });
        setProductChooser(tfProduct, this::handleSelectedProduct, tfProductQuantity);
        setSupplierChooser(tfSupplier, true, this::handleSelectedSupplier, tfInvoiceNumber);
        TextFieldUtils.onTextChanged((ov, nv) -> calculatePurchaseSummary(), tfDiscount, tfTax);
        ComboBoxUtils.onSelectedItemChanged(cbPaymentStatus, (ov, nv) -> {
            boolean isPaid = PaymentStatus.PAID.equals(nv.getValue());
            if (isPaid) {
                tfDueDate.setPlainText("");
            }
            vboxDueDate.setDisable(isPaid);
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        if (!isPharmacyFeatureEnabled()) {
            vboxPrescriptionSellingPrice.setVisible(false);
            tblPurchaseProduct.getColumns().remove(colPrescriptionSellingPrice);
        }
    }

    @Override
    protected Object save() {
        PurchaseAddVM purchase = new PurchaseAddVM();
        purchase.setSupplierId(selectedSupplier.getId());
        purchase.setInvoiceNumber(tfInvoiceNumber.getText().trim());
        purchase.setInvoiceDate(
                DateTimeUtils.parseLocalDateQuietly(tfInvoiceDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        PaymentStatus paymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        purchase.setPaymentStatus(paymentStatus);
        if (PaymentStatus.UNPAID.equals(paymentStatus)) {
            purchase.setPaymentDueDate(
                    DateTimeUtils.parseLocalDateQuietly(tfDueDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        }
        purchase.setDiscount(toBigDecimalOrNull(tfDiscount.getText()));
        purchase.setTax(toBigDecimalOrNull(tfTax.getText()));
        purchase.setTotalPayment(totalPayment);
        purchase.setTotalProduct(totalProduct);
        purchase.setTotalPurchase(totalPurchase);
        purchase.setPurchaseProducts(tblPurchaseProduct.getItems());
        purchaseService.createPurchase(purchase);
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        LocalDate invoiceDate = DateTimeUtils
                .parseLocalDateQuietly(tfInvoiceDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN);
        LocalDate dueDate = DateTimeUtils
                .parseLocalDateQuietly(tfDueDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN);
        PaymentStatus selected = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        boolean isUnpaid = PaymentStatus.UNPAID.equals(selected);
        validator.validateBlank(tfSupplier, MessageCode.ERROR_EMPTY_SUPPLIER);
        validator.validateBlank(tfInvoiceNumber, MessageCode.ERROR_INVALID_INVOICE_NUMBER);
        LocalDate today = LocalDate.now();
        validator.validateCustom(() -> invoiceDate == null, MessageCode.ERROR_INVALID_INVOICE_DATE);
        validator.validateCustom(
                () -> invoiceDate != null && invoiceDate.isAfter(today),
                MessageCode.ERROR_INVOICE_DATE_AFTER_TODAY);
        validator.validateCustom(() -> isUnpaid && dueDate == null, MessageCode.ERROR_INVALID_DUE_DATE);
        validator.validateCustom(
                () -> isUnpaid && dueDate != null && dueDate.isBefore(today),
                MessageCode.ERROR_DUE_DATE_BEFORE_TODAY);
        validator.validateCustom(() -> tblPurchaseProduct.getItems().isEmpty(), MessageCode.ERROR_EMPTY_PRODUCT);
    }

    @Override
    protected void initServices() {
        purchaseService = SpringUtils.getBean(PurchaseService.class);
    }

    public void handleSelectedProduct(ChooseResultVM<ProductVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        vboxPrescriptionSellingPrice.setDisable(true);
        result.getData().ifPresentOrElse(product -> {
            selectedProduct = product;
            tfProduct.setText(product.getName());
            tfProductCategory.setText(product.getCategoryName());
            tfProductUnit.setText(product.getUnitLabel());
            tfGeneralSellingPrice.setText(toStringOrEmpty(product.getGeneralSellingPrice()));
            tfPrescriptionSellingPrice.setText(toStringOrEmpty(product.getPrescriptionSellingPrice()));
            if (ProductUtils.isProductCategoryDrugs(product.getCategoryCode())) {
                vboxPrescriptionSellingPrice.setDisable(false);
            }
        }, () -> {
            selectedProduct = null;
            tfProduct.setText("");
            tfProductCategory.setText("");
            tfProductUnit.setText("");
            tfGeneralSellingPrice.setText("");
            tfPrescriptionSellingPrice.setText("");
        });
    }

    public void handleSelectedSupplier(ChooseResultVM<SupplierVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(supplier -> {
            selectedSupplier = supplier;
            tfSupplier.setText(supplier.getName());
        }, () -> {
            selectedSupplier = null;
            tfSupplier.setText("");
        });
    }

    private int getProductIndexInTable(ProductVM product, TableView<PurchaseProductVM> table) {
        Predicate<PurchaseProductVM> productExists = item -> item.getProductId().equals(product.getId());
        return TableViewUtils.getItemIndex(productExists, table);
    }

    private void calculatePurchaseSummary() {
        Locale locale = resources.getLocale();
        ObservableList<PurchaseProductVM> items = tblPurchaseProduct.getItems();
        totalProduct = items.stream().map(PurchaseProductVM::getQuantity).reduce(0, Integer::sum);
        totalPurchase = items.stream().map(PurchaseProductVM::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = toBigDecimalOrZero(tfDiscount.getText());
        BigDecimal tax = toBigDecimalOrZero(tfTax.getText());
        totalPayment = totalPurchase.add(tax).subtract(discount);
        lblDiscount.setText(formatOrDefault(discount, locale, "0"));
        lblTax.setText(formatOrDefault(tax, locale, "0"));
        lblTotalProduct.setText(formatOrDefault(totalProduct, locale, "0"));
        lblTotalPurchase.setText(formatOrDefault(totalPurchase, locale, "0"));
        lblTotalPayment.setText(formatOrDefault(totalPayment, locale, "0"));
    }

    private void resetControls() {
        this.selectedSupplier = null;
        this.selectedProduct = null;
        TextFieldUtils.setTextEmpty(
                tfSupplier,
                tfInvoiceNumber,
                tfDiscount,
                tfTax,
                tfProduct,
                tfProductCategory,
                tfProductUnit,
                tfProductQuantity,
                tfBuyingPrice,
                tfGeneralSellingPrice,
                tfPrescriptionSellingPrice);
        tfInvoiceDate.setPlainText("");
        tfDueDate.setPlainText("");
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        tblPurchaseProduct.getItems().clear();
        lblDiscount.setText("0");
        lblTax.setText("0");
        lblTotalProduct.setText("0");
        lblTotalPurchase.setText("0");
        lblTotalPayment.setText("0");
    }

    private void handleActionTablePurchaseProduct() {
        if (TableViewUtils.hasItemSelected(tblPurchaseProduct)) {
            PurchaseProductVM selected = TableViewUtils.getSelectedItem(tblPurchaseProduct);
            ProductVM product = new ProductVM();
            product.setName(selected.getProductName());
            product.setId(selected.getProductId());
            product.setCategoryCode(selected.getProductCategoryCode());
            product.setCategoryName(selected.getProductCategoryName());
            product.setUnitLabel(selected.getProductUnitLabel());
            product.setGeneralSellingPrice(selected.getGeneralSellingPrice());
            product.setPrescriptionSellingPrice(selected.getPrescriptionSellingPrice());
            handleSelectedProduct(new ChooseResultVM<>(false, Optional.of(product)));
            tfProductQuantity.setText(toStringOrEmpty(selected.getQuantity()));
            tfBuyingPrice.setText(toStringOrEmpty(selected.getBuyingPrice()));
            tfBatchNumber.setText(selected.getBatchNumber());
            tfExpiredDate.setPlainText("");
            if (selected.getExpiredDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN);
                tfExpiredDate.setText(formatter.format(selected.getExpiredDate()));
            }
        }
    }

    private ValidationResult validateAddProduct(
            boolean isProductSelected,
            boolean isProductCategoryDrugs,
            LocalDate expiredDate) {
        ControlValidator cv = new ControlValidator(resources);
        cv.validateCustom(() -> !isProductSelected, MessageCode.ERROR_EMPTY_PRODUCT);
        cv.validatePositive(tfProductQuantity, MessageCode.ERROR_INVALID_QUANTITY);
        cv.validatePositive(tfBuyingPrice, MessageCode.ERROR_INVALID_BUYING_PRICE);
        if (StringUtils.isNotBlank(tfGeneralSellingPrice.getText())) {
            cv.validatePositive(tfGeneralSellingPrice, MessageCode.ERROR_INVALID_GENERAL_SELLING_PRICE);
        }
        if (StringUtils.isNotBlank(tfPrescriptionSellingPrice.getText()) && isProductCategoryDrugs) {
            cv.validatePositive(tfPrescriptionSellingPrice, MessageCode.ERROR_INVALID_PRESCRIPTION_SELLING_PRICE);
        }
        cv.validateCustom(
                () -> StringUtils.isNotBlank(tfExpiredDate.getPlainText()) && expiredDate == null,
                MessageCode.ERROR_INVALID_EXPIRED_DATE);
        cv.validateCustom(
                () -> isProductSelected && CommonConstants.PRODUCT_CATEGORY_CODE_CUSTOM_PACKAGE
                        .equals(selectedProduct.getCategoryCode()),
                MessageCode.ERROR_PRODUCT_CATEGORY_CUSTOM_PACKAGE_NOT_ALLOWED);
        return cv.getResult();
    }
}

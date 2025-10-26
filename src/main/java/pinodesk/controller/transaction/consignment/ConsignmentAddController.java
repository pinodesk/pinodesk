package pinodesk.controller.transaction.consignment;

import static com.mudiatech.toolbox.data.StringNumberUtils.formatOrDefault;
import static com.mudiatech.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.mudiatech.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.mudiatech.toolbox.data.StringNumberUtils.toStringOrEmpty;
import static pinodesk.constant.CommonConstants.DECIMAL_SCALE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.mudiatech.pandora.factory.LocalDateCellFactory;
import com.mudiatech.pandora.factory.NumberCellFactory;
import com.mudiatech.pandora.utility.ControlValidator;
import com.mudiatech.pandora.utility.EventUtils;
import com.mudiatech.pandora.utility.ScrollPaneUtils;
import com.mudiatech.pandora.utility.StageUtils;
import com.mudiatech.pandora.utility.TableViewUtils;
import com.mudiatech.pandora.utility.TextFieldUtils;
import com.mudiatech.pandora.utility.ValidationResult;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.MessageCode;
import pinodesk.constant.Page;
import pinodesk.constant.StyleConstants;
import pinodesk.controller.CommonDataSaveController;
import pinodesk.service.ConsignmentService;
import pinodesk.util.ProductUtils;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.ChooseResultVM;
import pinodesk.viewmodel.ConsignmentAddVM;
import pinodesk.viewmodel.ConsignmentProductVM;
import pinodesk.viewmodel.ProductVM;
import pinodesk.viewmodel.SupplierVM;

public class ConsignmentAddController extends CommonDataSaveController {

    @FXML
    private ScrollPane scrollPaneConsignmentAdd;

    @FXML
    private TextField tfSupplier;

    @FXML
    private Button btnNewSupplier;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private DatePicker dpInvoiceDate;
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
    private TextField tfSupplierPrice;

    @FXML
    private TextField tfGeneralSellingPrice;

    @FXML
    private VBox vboxPrescriptionSellingPrice;

    @FXML
    private TextField tfPrescriptionSellingPrice;

    @FXML
    private TextField tfBatchNumber;

    @FXML
    private DatePicker dpExpiredDate;

    @FXML
    private Button btnAddProduct;

    @FXML
    private TableView<ConsignmentProductVM> tblConsignmentProducts;

    @FXML
    private TableColumn<ConsignmentProductVM, String> colProductName;

    @FXML
    private TableColumn<ConsignmentProductVM, String> colProductCategory;

    @FXML
    private TableColumn<ConsignmentProductVM, String> colUnit;

    @FXML
    private TableColumn<ConsignmentProductVM, Integer> colQuantity;

    @FXML
    private TableColumn<ConsignmentProductVM, BigDecimal> colSupplierPrice;

    @FXML
    private TableColumn<ConsignmentProductVM, BigDecimal> colSubtotalPrice;

    @FXML
    private TableColumn<ConsignmentProductVM, BigDecimal> colGeneralSellingPrice;

    @FXML
    private TableColumn<ConsignmentProductVM, BigDecimal> colPrescriptionSellingPrice;

    @FXML
    private TableColumn<ConsignmentProductVM, String> colBactchNumber;

    @FXML
    private TableColumn<ConsignmentProductVM, LocalDate> colExpiredDate;

    @FXML
    private Label lblTotalProduct;

    @FXML
    private Button btnSaveAndAdd;

    private ProductVM selectedProduct;
    private SupplierVM selectedSupplier;

    private ConsignmentService consignmentService;

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
        if (TableViewUtils.hasItemSelected(tblConsignmentProducts)) {
            tblConsignmentProducts.getItems().remove(TableViewUtils.getSelectedItem(tblConsignmentProducts));
            calculateConsignmentSummary();
        }
        if (tblConsignmentProducts.getItems().isEmpty()) {
            tblConsignmentProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        }
    }

    @FXML
    void onActionBtnAddProduct(ActionEvent event) {
        boolean isProductSelected = selectedProduct != null;
        boolean isProductCategoryDrugs = isProductSelected
                && ProductUtils.isProductCategoryDrugs(selectedProduct.getCategoryCode());
        LocalDate expiredDate = dpExpiredDate.getValue();
        ValidationResult validationResult = validateAddProduct(isProductSelected, isProductCategoryDrugs, expiredDate);
        if (!validationResult.isValid()) {
            displayError(validationResult.getMessages());
            return;
        }
        String batchNumber = StringUtils.trimToNull(tfBatchNumber.getText());
        Integer quantity = toIntegerOrNull(tfProductQuantity.getText());
        BigDecimal supplierPrice = toBigDecimalOrNull(tfSupplierPrice.getText(), DECIMAL_SCALE);
        BigDecimal generalSellingPrice = toBigDecimalOrNull(tfGeneralSellingPrice.getText(), DECIMAL_SCALE);
        ConsignmentProductVM consignmentProduct = new ConsignmentProductVM();
        consignmentProduct.setProductId(selectedProduct.getId());
        consignmentProduct.setProductName(selectedProduct.getName());
        consignmentProduct.setProductCategoryCode(selectedProduct.getCategoryCode());
        consignmentProduct.setProductCategoryName(selectedProduct.getCategoryName());
        consignmentProduct.setProductUnitLabel(selectedProduct.getUnitLabel());
        consignmentProduct.setQuantity(quantity);
        consignmentProduct.setSupplierPrice(supplierPrice);
        consignmentProduct.setGeneralSellingPrice(generalSellingPrice);
        if (isProductCategoryDrugs) {
            BigDecimal prescriptionSellingPrice = toBigDecimalOrNull(
                    tfPrescriptionSellingPrice.getText(),
                    DECIMAL_SCALE);
            consignmentProduct.setPrescriptionSellingPrice(prescriptionSellingPrice);
        }
        consignmentProduct.setBatchNumber(batchNumber);
        consignmentProduct.setExpiredDate(expiredDate);
        consignmentProduct.setSubtotalPrice(supplierPrice.multiply(BigDecimal.valueOf(quantity)));
        int idx = getProductIndexInTable(selectedProduct, tblConsignmentProducts);
        if (idx != -1) {
            tblConsignmentProducts.getItems().remove(idx);
        }
        tblConsignmentProducts.getItems().add(consignmentProduct);
        selectedProduct = null;
        TextFieldUtils.setTextEmpty(
                tfProduct,
                tfProductCategory,
                tfProductUnit,
                tfProductQuantity,
                tfSupplierPrice,
                tfGeneralSellingPrice,
                tfPrescriptionSellingPrice,
                tfBatchNumber);
        dpExpiredDate.setValue(null);
        calculateConsignmentSummary();
    }

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_CONSIGNMENT);
            resetControls();
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        initCustomDatePicker(dpInvoiceDate, dpExpiredDate);
        Locale locale = resources.getLocale();
        TextFieldUtils.setDecimalTextFields(tfSupplierPrice, tfGeneralSellingPrice, tfPrescriptionSellingPrice);
        TextFieldUtils.setDigitTextFields(tfProductQuantity);
        initTableConsignmentProduct(locale);
        setProductChooser(tfProduct, this::handleSelectedProduct, tfProductQuantity);
        setSupplierChooser(tfSupplier, true, this::handleSelectedSupplier, tfInvoiceNumber);
    }

    private void initTableConsignmentProduct(Locale locale) {
        TableViewUtils.setColumnValue(colProductName, ConsignmentProductVM::getProductName);
        TableViewUtils.setColumnValue(colUnit, ConsignmentProductVM::getProductUnitLabel);
        TableViewUtils.setColumnValue(colBactchNumber, ConsignmentProductVM::getBatchNumber);
        TableViewUtils.setColumnValue(colProductCategory, ConsignmentProductVM::getProductCategoryName);
        TableViewUtils.initTableColumn(
                colSupplierPrice,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                ConsignmentProductVM::getSupplierPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                ConsignmentProductVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSubtotalPrice,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                ConsignmentProductVM::getSubtotalPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colGeneralSellingPrice,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                ConsignmentProductVM::getGeneralSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPrescriptionSellingPrice,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                ConsignmentProductVM::getPrescriptionSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colExpiredDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ConsignmentProductVM::getExpiredDate);
        tblConsignmentProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblConsignmentProducts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblConsignmentProducts.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableConsignmentProduct();
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        if (!isPharmacyFeatureEnabled()) {
            vboxPrescriptionSellingPrice.setVisible(false);
            tblConsignmentProducts.getColumns().remove(colPrescriptionSellingPrice);
        }
        Platform.runLater(() -> {
            ScrollPaneUtils.fixBlur(scrollPaneConsignmentAdd);
        });
    }

    @Override
    protected Object save() {
        ObservableList<ConsignmentProductVM> items = tblConsignmentProducts.getItems();
        ConsignmentAddVM consignment = new ConsignmentAddVM();
        consignment.setSupplierId(selectedSupplier.getId());
        consignment.setInvoiceNumber(tfInvoiceNumber.getText().trim());
        consignment.setInvoiceDate(dpInvoiceDate.getValue());
        consignment.setTotalProduct(items.size());
        consignment.setConsignmentProducts(items);
        consignmentService.createConsignment(consignment);
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        LocalDate invoiceDate = dpInvoiceDate.getValue();
        validator.validateBlank(tfSupplier, MessageCode.ERROR_EMPTY_SUPPLIER);
        validator.validateBlank(tfInvoiceNumber, MessageCode.ERROR_INVALID_INVOICE_NUMBER);
        LocalDate today = LocalDate.now();
        validator.validateCustom(() -> invoiceDate == null, MessageCode.ERROR_INVALID_INVOICE_DATE);
        validator.validateCustom(
                () -> invoiceDate != null && invoiceDate.isAfter(today),
                MessageCode.ERROR_INVOICE_DATE_AFTER_TODAY);
        validator.validateCustom(() -> tblConsignmentProducts.getItems().isEmpty(), MessageCode.ERROR_EMPTY_PRODUCT);
    }

    @Override
    protected void initServices() {
        consignmentService = SpringUtils.getBean(ConsignmentService.class);
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
            if (product.getGeneralSellingPrice() != null) {
                tfGeneralSellingPrice.setText(product.getGeneralSellingPrice().doubleValue() + "");
            }
            if (product.getPrescriptionSellingPrice() != null) {
                tfPrescriptionSellingPrice.setText(product.getPrescriptionSellingPrice().doubleValue() + "");
            }
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

    private int getProductIndexInTable(ProductVM product, TableView<ConsignmentProductVM> table) {
        Predicate<ConsignmentProductVM> productExists = item -> item.getProductId().equals(product.getId());
        return TableViewUtils.getItemIndex(productExists, table);
    }

    private void calculateConsignmentSummary() {
        Locale locale = resources.getLocale();
        ObservableList<ConsignmentProductVM> items = tblConsignmentProducts.getItems();
        lblTotalProduct.setText(formatOrDefault(items.size(), locale, DECIMAL_SCALE, "0"));
    }

    private void resetControls() {
        this.selectedSupplier = null;
        this.selectedProduct = null;
        TextFieldUtils.setTextEmpty(
                tfSupplier,
                tfInvoiceNumber,
                tfProduct,
                tfProductCategory,
                tfProductUnit,
                tfProductQuantity,
                tfSupplierPrice,
                tfGeneralSellingPrice,
                tfPrescriptionSellingPrice);
        dpInvoiceDate.setValue(null);
        tblConsignmentProducts.getItems().clear();
        lblTotalProduct.setText("0");
    }

    private void handleActionTableConsignmentProduct() {
        if (TableViewUtils.hasItemSelected(tblConsignmentProducts)) {
            ConsignmentProductVM selected = TableViewUtils.getSelectedItem(tblConsignmentProducts);
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
            tfSupplierPrice.setText(toStringOrEmpty(selected.getSupplierPrice().doubleValue()));
            tfBatchNumber.setText(selected.getBatchNumber());
            dpExpiredDate.setValue(null);
            if (selected.getExpiredDate() != null) {
                dpExpiredDate.setValue(selected.getExpiredDate());
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
        cv.validatePositive(tfSupplierPrice, MessageCode.ERROR_INVALID_SUPPLIER_PRICE);
        if (StringUtils.isNotBlank(tfGeneralSellingPrice.getText())) {
            cv.validatePositive(tfGeneralSellingPrice, MessageCode.ERROR_INVALID_GENERAL_SELLING_PRICE);
        }
        if (StringUtils.isNotBlank(tfPrescriptionSellingPrice.getText()) && isProductCategoryDrugs) {
            cv.validatePositive(tfPrescriptionSellingPrice, MessageCode.ERROR_INVALID_PRESCRIPTION_SELLING_PRICE);
        }
        cv.validateCustom(
                () -> isProductSelected && CommonConstants.PRODUCT_CATEGORY_CODE_CUSTOM_PACKAGE
                        .equals(selectedProduct.getCategoryCode()),
                MessageCode.ERROR_PRODUCT_CATEGORY_CUSTOM_PACKAGE_NOT_ALLOWED);
        return cv.getResult();
    }

}

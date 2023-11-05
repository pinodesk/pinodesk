package pospino.desktop.controller.transaction.purchase;

import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.formatOrDefault;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toBigDecimalOrZero;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toStringOrDefault;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toStringOrEmpty;
import static pospino.desktop.constant.CommonConstants.DECIMAL_SCALE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.mudiasoft.pandora.factory.DefaultCellFactory;
import com.gitlab.mudiasoft.pandora.factory.LocalDateCellFactory;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.model.SimpleComboBoxModel;
import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.ScrollPaneUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;
import com.gitlab.mudiasoft.pandora.utility.ValidationResult;
import com.gitlab.mudiasoft.toolbox.data.StringNumberUtils;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.DiscountType;
import pospino.desktop.constant.MenuCodeConstants;
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
import pospino.desktop.viewmodel.PurchaseEditVM;
import pospino.desktop.viewmodel.PurchaseProductVM;
import pospino.desktop.viewmodel.PurchaseVM;
import pospino.desktop.viewmodel.SupplierVM;

public class PurchaseEditController extends CommonDataSaveController {

    @FXML
    private ScrollPane scrollPanePurchaseEdit;

    @FXML
    private Button btnRemove;

    @FXML
    private TextField tfSupplier;

    @FXML
    private Button btnNewSupplier;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private DatePicker dpInvoiceDate;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private VBox vboxDueDate;

    @FXML
    private DatePicker dpDueDate;

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
    private DatePicker dpExpiredDate;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbDiscountType;

    @FXML
    private Label lblDiscountPercent;

    @FXML
    private TextField tfBuyingPriceDiscount;

    @FXML
    private TextField tfAdditionalDiscount;

    @FXML
    private TextField tfDiscountAmount;

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
    private TableColumn<PurchaseProductVM, String> colDiscount;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colBuyingPriceDiscount;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colSubtotalPrice;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colSubtotalDiscount;

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
    private Label lblTotalPrice;

    @FXML
    private Label lblTotalDiscount;

    @FXML
    private Label lblTotalPriceDiscount;

    @FXML
    private Label lblTax;

    @FXML
    private Label lblTotalPayment;

    @FXML
    private VBox vboxDiscountAmount;

    @FXML
    private VBox vboxBuyingPriceDiscount;

    private ProductVM selectedProduct;
    private SupplierVM selectedSupplier;
    private Integer totalProduct;
    private BigDecimal totalPrice;
    private BigDecimal totalPayment;
    private BigDecimal totalDiscount;
    private BigDecimal additionalDiscount;

    private PurchaseService purchaseService;

    private PurchaseVM currentPurchase;

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
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_PURCHASE);
        if (result.isConfirmed()) {
            purchaseService.removePurchases(List.of(currentPurchase.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_PURCHASE);
            setPageData(Boolean.TRUE);
            close();
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
        purchaseProduct.setSubtotalPrice(buyingPrice.multiply(BigDecimal.valueOf(quantity)));
        purchaseProduct.setGeneralSellingPrice(generalSellingPrice);
        if (isProductCategoryDrugs) {
            BigDecimal prescriptionSellingPrice = toBigDecimalOrNull(tfPrescriptionSellingPrice.getText());
            purchaseProduct.setPrescriptionSellingPrice(prescriptionSellingPrice);
        }
        purchaseProduct.setBatchNumber(tfBatchNumber.getText());
        purchaseProduct.setExpiredDate(expiredDate);
        DiscountType discountType = ComboBoxUtils.getSelectedItem(cbDiscountType).getValue();
        BigDecimal discountAmount = toBigDecimalOrNull(tfDiscountAmount.getText(), DECIMAL_SCALE);
        BigDecimal buyingPriceDiscount = toBigDecimalOrNull(tfBuyingPriceDiscount.getText(), DECIMAL_SCALE);
        purchaseProduct.setDiscountType(Objects.toString(discountType, null));
        purchaseProduct.setDiscountAmount(discountAmount);
        purchaseProduct.setBuyingPriceDiscount(buyingPriceDiscount);
        purchaseProduct.setSubtotalPrice(
                buyingPriceDiscount == null ?
                        buyingPrice.multiply(BigDecimal.valueOf(quantity)) :
                        buyingPriceDiscount.multiply(BigDecimal.valueOf(quantity)));
        purchaseProduct.setSubtotalDiscount(
                buyingPriceDiscount == null ?
                        BigDecimal.ZERO :
                        buyingPrice.subtract(buyingPriceDiscount).multiply(BigDecimal.valueOf(quantity)));
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
                tfDiscountAmount,
                tfBuyingPriceDiscount,
                tfGeneralSellingPrice,
                tfPrescriptionSellingPrice,
                tfBatchNumber);
        dpExpiredDate.setValue(null);
        ComboBoxUtils.selectIndex(cbDiscountType, 0);
        vboxDiscountAmount.setDisable(true);
        vboxBuyingPriceDiscount.setDisable(true);
        calculatePurchaseSummary();
    }

    @Override
    protected void onActionBtnSave(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_EDIT_PURCHASE);
        if (!result.isConfirmed()) {
            return;
        }
        super.onActionBtnSave(event);
    }

    @Override
    protected void initDataSaveControlActions() {
        disableWriteAction(MenuCodeConstants.TRANSACTION_PURCHASES, btnSave, btnRemove, btnNewProduct, btnNewSupplier);
        initCustomDatePicker(dpInvoiceDate, dpDueDate, dpExpiredDate);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
        Locale locale = resources.getLocale();
        TextFieldUtils.setDecimalTextFields(tfDiscountAmount);
        TextFieldUtils.setDigitTextFields(
                tfAdditionalDiscount,
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
        TableViewUtils.initTableColumn(colDiscount, new DefaultCellFactory<>(), (vm) -> {
            BigDecimal discountAmount = vm.getDiscountAmount();
            if (discountAmount == null) {
                return null;
            }
            String strDiscount = StringNumberUtils.format(discountAmount, locale);
            if (DiscountType.PERCENTAGE.toString().equals(vm.getDiscountType())) {
                BigDecimal realAmount = vm.getBuyingPrice().subtract(vm.getBuyingPriceDiscount());
                String strRealAmount = StringNumberUtils.format(realAmount, locale);
                strDiscount = String.format("%s%% = %s", strDiscount, strRealAmount);
            }
            return strDiscount;
        }, StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colBuyingPriceDiscount,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                PurchaseProductVM::getBuyingPriceDiscount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSubtotalPrice,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                PurchaseProductVM::getSubtotalPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSubtotalDiscount,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                (vm) -> vm.getDiscountType() == null ? null : vm.getSubtotalDiscount(),
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
        TextFieldUtils.onTextChanged((ov, nv) -> calculatePurchaseSummary(), tfAdditionalDiscount, tfTax);
        TextFieldUtils.onTextChanged((ov, nv) -> {
            calculateBuyingPriceDiscount(locale);
        }, tfDiscountAmount, tfBuyingPrice);
        ComboBoxUtils.onSelectedItemChanged(cbPaymentStatus, (ov, nv) -> {
            boolean isPaid = PaymentStatus.PAID.equals(nv.getValue());
            if (isPaid) {
                dpDueDate.setValue(null);
            }
            vboxDueDate.setDisable(isPaid);
        });
        ComboBoxUtils.initSimple(
                cbDiscountType,
                new SimpleComboBoxModel(null, t.translate(CommonLabel.LBL_NO_DISCOUNT)),
                new SimpleComboBoxModel(DiscountType.PERCENTAGE, t.translate(CommonLabel.LBL_PERCENTAGE)),
                new SimpleComboBoxModel(DiscountType.FIXED_AMOUNT, t.translate(CommonLabel.LBL_FIXED_AMOUNT)));
        ComboBoxUtils.onSelectedItemChanged(cbDiscountType, (ov, nv) -> {
            boolean isNoDiscount = nv.getValue() == null;
            vboxDiscountAmount.setDisable(isNoDiscount);
            vboxBuyingPriceDiscount.setDisable(isNoDiscount);
            if (isNoDiscount) {
                TextFieldUtils.setTextEmpty(tfDiscountAmount, tfBuyingPriceDiscount);
            } else {
                calculateBuyingPriceDiscount(locale);
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        Locale locale = resources.getLocale();
        currentPurchase = getPageData();
        List<PurchaseProductVM> products = purchaseService.getPurchaseProducts(currentPurchase.getId());
        tblPurchaseProduct.getItems().addAll(products);
        selectedSupplier = new SupplierVM();
        selectedSupplier.setId(currentPurchase.getSupplierId());
        selectedSupplier.setName(currentPurchase.getSupplierName());
        LocalDate invoiceDate = currentPurchase.getInvoiceDate();
        LocalDate paymentDueDate = currentPurchase.getPaymentDueDate();
        BigDecimal additionalDiscount = currentPurchase.getAdditionalDiscount();
        BigDecimal totalDiscount = currentPurchase.getTotalDiscount();
        BigDecimal tax = currentPurchase.getTax();
        totalProduct = currentPurchase.getTotalProduct();
        totalPrice = currentPurchase.getTotalPrice();
        totalPayment = currentPurchase.getTotalPayment();
        tfSupplier.setText(currentPurchase.getSupplierName());
        tfInvoiceNumber.setText(currentPurchase.getInvoiceNumber());
        dpInvoiceDate.setValue(invoiceDate);
        ComboBoxUtils.select(cbPaymentStatus, () -> cbPaymentStatus.getItems().stream().filter(vm -> {
            PaymentStatus paymentStatus = vm.getValue();
            return paymentStatus.toString().equals(currentPurchase.getPaymentStatus());
        }).findAny().orElseThrow());
        if (paymentDueDate != null) {
            dpDueDate.setValue(paymentDueDate);
        }
        lblTotalDiscount.setText(formatOrDefault(totalDiscount, locale, "0"));
        lblTax.setText(formatOrDefault(tax, locale, "0"));
        lblTotalPayment.setText(formatOrDefault(totalPayment, locale, "0"));
        lblTotalProduct.setText(formatOrDefault(totalProduct, locale, "0"));
        lblTotalPrice.setText(formatOrDefault(totalPrice, locale, "0"));
        tfAdditionalDiscount.setText(toStringOrEmpty(additionalDiscount));
        tfTax.setText(toStringOrEmpty(tax));
        if (!isPharmacyFeatureEnabled()) {
            vboxPrescriptionSellingPrice.setVisible(false);
            tblPurchaseProduct.getColumns().remove(colPrescriptionSellingPrice);
        }
        Platform.runLater(() -> {
            ScrollPaneUtils.fixBlur(scrollPanePurchaseEdit);
        });
    }

    @Override
    protected Object save() {
        PurchaseEditVM purchase = new PurchaseEditVM();
        purchase.setSupplierId(selectedSupplier.getId());
        purchase.setInvoiceNumber(tfInvoiceNumber.getText().trim());
        purchase.setInvoiceDate(dpInvoiceDate.getValue());
        PaymentStatus paymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        purchase.setPaymentStatus(paymentStatus);
        if (PaymentStatus.UNPAID.equals(paymentStatus)) {
            purchase.setPaymentDueDate(dpDueDate.getValue());
        }
        purchase.setAdditionalDiscount(additionalDiscount);
        purchase.setTotalDiscount(totalDiscount);
        purchase.setTax(toBigDecimalOrNull(tfTax.getText()));
        purchase.setTotalPayment(totalPayment);
        purchase.setTotalProduct(totalProduct);
        purchase.setTotalPrice(totalPrice);
        purchase.setPurchaseProducts(tblPurchaseProduct.getItems());
        purchaseService.updatePurchase(purchase, currentPurchase.getId());
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        LocalDate invoiceDate = dpInvoiceDate.getValue();
        LocalDate dueDate = dpDueDate.getValue();
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
        totalPrice = items.stream().map(vm -> vm.getBuyingPrice().multiply(BigDecimal.valueOf(vm.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        additionalDiscount = toBigDecimalOrNull(tfAdditionalDiscount.getText());
        totalDiscount = items.stream().map(PurchaseProductVM::getSubtotalDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(additionalDiscount == null ? BigDecimal.ZERO : additionalDiscount);
        BigDecimal tax = toBigDecimalOrZero(tfTax.getText());
        totalPayment = totalPrice.add(tax).subtract(totalDiscount);
        lblTotalDiscount.setText(formatOrDefault(totalDiscount, locale, DECIMAL_SCALE, "0"));
        lblTax.setText(formatOrDefault(tax, locale, DECIMAL_SCALE, "0"));
        lblTotalProduct.setText(formatOrDefault(totalProduct, locale, DECIMAL_SCALE, "0"));
        lblTotalPrice.setText(formatOrDefault(totalPrice, locale, DECIMAL_SCALE, "0"));
        lblTotalPayment.setText(formatOrDefault(totalPayment, locale, DECIMAL_SCALE, "0"));
        lblTotalPriceDiscount.setText(formatOrDefault(totalPrice.subtract(totalDiscount), locale, DECIMAL_SCALE, "0"));
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
            dpExpiredDate.setValue(null);
            if (selected.getExpiredDate() != null) {
                dpExpiredDate.setValue(selected.getExpiredDate());
            }
            if (selected.getDiscountType() != null) {
                vboxDiscountAmount.setDisable(false);
                vboxBuyingPriceDiscount.setDisable(false);
            }
            ComboBoxUtils.select(cbDiscountType, () -> cbDiscountType.getItems().stream().filter(model -> {
                DiscountType val = (DiscountType) model.getValue();
                String strDiscountType = selected.getDiscountType();
                return StringUtils.isBlank(strDiscountType) || (val != null && val.toString().equals(strDiscountType));
            }).findAny().orElseThrow());
            BigDecimal discountAmount = selected.getDiscountAmount();
            BigDecimal buyingPriceDiscount = selected.getBuyingPriceDiscount();
            tfDiscountAmount.setText(discountAmount == null ? "" : toStringOrEmpty(discountAmount.doubleValue()));
            tfBuyingPriceDiscount
                    .setText(buyingPriceDiscount == null ? "" : toStringOrEmpty(buyingPriceDiscount.doubleValue()));
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
        DiscountType discountType = ComboBoxUtils.getSelectedItem(cbDiscountType).getValue();
        BigDecimal discountAmount = toBigDecimalOrNull(tfDiscountAmount.getText(), CommonConstants.DECIMAL_SCALE);
        cv.validateCustom(() -> {
            return DiscountType.PERCENTAGE.equals(discountType) && discountAmount != null
                    && discountAmount.doubleValue() <= 0.0 && discountAmount.doubleValue() > 100.0;
        }, MessageCode.ERROR_DISCOUNT_AMOUNT_PERCENTAGE_OUT_OF_RANGE);
        cv.validateCustom(() -> {
            BigDecimal buyingPrice = toBigDecimalOrNull(tfBuyingPrice.getText(), CommonConstants.DECIMAL_SCALE);
            return DiscountType.FIXED_AMOUNT.equals(discountType) && discountAmount != null
                    && discountAmount.compareTo(buyingPrice) > 0;
        }, MessageCode.ERROR_DISCOUNT_AMOUNT_FIXED_AMOUNT_GREATER_THAN_BUYING_PRICE);
        cv.validateCustom(
                () -> isProductSelected && CommonConstants.PRODUCT_CATEGORY_CODE_CUSTOM_PACKAGE
                        .equals(selectedProduct.getCategoryCode()),
                MessageCode.ERROR_PRODUCT_CATEGORY_CUSTOM_PACKAGE_NOT_ALLOWED);
        return cv.getResult();
    }

    private void calculateBuyingPriceDiscount(Locale locale) {
        String strBuyingPrice = tfBuyingPrice.getText();
        String strDiscountAmount = tfDiscountAmount.getText();
        if (StringUtils.isAnyBlank(strDiscountAmount, strBuyingPrice)) {
            tfBuyingPriceDiscount.setText("");
            return;
        }
        BigDecimal buyingPrice = toBigDecimalOrZero(strBuyingPrice, DECIMAL_SCALE);
        BigDecimal discountAmount = toBigDecimalOrZero(strDiscountAmount, DECIMAL_SCALE);
        DiscountType discountType = ComboBoxUtils.getSelectedItem(cbDiscountType).getValue();
        if (DiscountType.PERCENTAGE.equals(discountType)) {
            BigDecimal unitDiscount = discountAmount.divide(BigDecimal.valueOf(100)).multiply(buyingPrice);
            tfBuyingPriceDiscount.setText(toStringOrDefault(buyingPrice.subtract(unitDiscount).doubleValue(), "0"));
        } else if (DiscountType.FIXED_AMOUNT.equals(discountType)) {
            tfBuyingPriceDiscount.setText(toStringOrDefault(buyingPrice.subtract(discountAmount).doubleValue(), "0"));
        }
    }

}

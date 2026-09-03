package com.pinodesk.controller.catalog.product;

import static com.pinodesk.constant.CommonConstants.DECIMAL_SCALE;
import static com.pinodesk.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.pinodesk.toolbox.data.StringNumberUtils.toIntegerOrDefault;
import static com.pinodesk.toolbox.data.StringNumberUtils.toStringOrEmpty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;

import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.ProductStatus;
import com.pinodesk.constant.StyleConstants;
import com.pinodesk.controller.CommonDataSaveController;
import com.pinodesk.pandora.factory.LocalDateTimeCellFactory;
import com.pinodesk.pandora.factory.NumberCellFactory;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.AlertResult;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.TableViewUtils;
import com.pinodesk.pandora.utility.TextFieldUtils;
import com.pinodesk.pandora.utility.ValidationResult;
import com.pinodesk.service.ProductCategoryService;
import com.pinodesk.service.ProductService;
import com.pinodesk.service.UnitService;
import com.pinodesk.toolbox.future.AsyncUtils;
import com.pinodesk.toolbox.jackson.ObjectConverter;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ChooseResultVM;
import com.pinodesk.viewmodel.PackageProductVM;
import com.pinodesk.viewmodel.ProductCategoryVM;
import com.pinodesk.viewmodel.ProductEditVM;
import com.pinodesk.viewmodel.ProductPriceVM;
import com.pinodesk.viewmodel.ProductVM;
import com.pinodesk.viewmodel.UnitVM;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ProductPackageEditController extends CommonDataSaveController {

    @FXML
    private TabPane tabPaneAddPackage;

    @FXML
    private Tab tabPackage;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfBarcode;

    @FXML
    private TextField tfDescription;

    @FXML
    private TextField tfCategory;

    @FXML
    private TextField tfUnit;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;

    @FXML
    private TextField tfProduct;

    @FXML
    private TextField tfQuantity;

    @FXML
    private Button btnAddProduct;

    @FXML
    private Button btnRemoveProduct;

    @FXML
    private TableView<PackageProductVM> tblProducts;

    @FXML
    private TableColumn<PackageProductVM, String> colName;

    @FXML
    private TableColumn<PackageProductVM, Integer> colCurrentQuantity;

    @FXML
    private TableColumn<PackageProductVM, Integer> colQuantityInPackage;

    @FXML
    private TableColumn<PackageProductVM, String> colUnit;

    @FXML
    private TableColumn<PackageProductVM, BigDecimal> colGeneralSellingPrice;

    @FXML
    private TableColumn<PackageProductVM, BigDecimal> colPrescriptionSellingPrice;

    @FXML
    private Tab tabPrice;

    @FXML
    private TextField tfGeneralSellPrice;

    @FXML
    private VBox vboxPresciptionSellPrice;

    @FXML
    private TextField tfPrescriptionSellPrice;

    @FXML
    private TextField tfPriceRemarks;

    @FXML
    private TableView<ProductPriceVM> tblPrice;

    @FXML
    private TableColumn<ProductPriceVM, BigDecimal> colGeneralSellingPrice1;

    @FXML
    private TableColumn<ProductPriceVM, BigDecimal> colPrescriptionSellingPrice1;

    @FXML
    private TableColumn<ProductPriceVM, String> colProductPricePurchaseInvoiceNumber;

    @FXML
    private TableColumn<ProductPriceVM, String> colProductPriceSaleInvoiceNumber;

    @FXML
    private TableColumn<ProductPriceVM, String> colProductPriceActivity;

    @FXML
    private TableColumn<ProductPriceVM, String> colProductPriceRemarks;

    @FXML
    private TableColumn<ProductPriceVM, String> colProductPriceUser;

    @FXML
    private TableColumn<ProductPriceVM, LocalDateTime> colProductPriceCreatedAt;

    @FXML
    protected Button btnRemove;

    private ProductService productService;

    private ProductCategoryService productCategoryService;

    private UnitService unitService;

    private ProductCategoryVM selectedProductCategory;

    private UnitVM selectedUnit;

    private ProductVM selectedProduct;

    private ProductVM currentPackage;

    private ObjectConverter objectConverter;

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_PRODUCT);
        if (result.isConfirmed()) {
            productService.removeProducts(Arrays.asList(currentPackage.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_PRODUCT);
            setPageData(Boolean.TRUE);
            close();
        }
    }

    @FXML
    void onActionBtnAddProduct(ActionEvent event) {
        ValidationResult validationResult = validateAddProduct();
        if (!validationResult.isValid()) {
            displayError(validationResult.getMessages());
            return;
        }
        int idx = getProductIndexInTable(selectedProduct, tblProducts);
        if (idx != -1) {
            tblProducts.getItems().remove(idx);
        }
        PackageProductVM pp = objectConverter.convertObject(selectedProduct, PackageProductVM.class);
        pp.setQuantityInPackage(toIntegerOrDefault(tfQuantity.getText(), 1));
        tblProducts.getItems().add(pp);
        calculatePrice();
        selectedProduct = null;
        tfProduct.setText("");
        tfQuantity.setText("1");
    }

    @FXML
    void onActionBtnRemoveProduct(ActionEvent event) {
        if (TableViewUtils.hasItemSelected(tblProducts)) {
            tblProducts.getItems().remove(TableViewUtils.getSelectedItem(tblProducts));
        }
        if (tblProducts.getItems().isEmpty()) {
            tblProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        }
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
        Locale locale = resources.getLocale();
        TextFieldUtils.setDecimalTextFields(tfGeneralSellPrice, tfPrescriptionSellPrice);
        TextFieldUtils.setDigitTextFields(tfBarcode);
        setProductChooser(tfProduct, this::handleSelectedProduct, tfQuantity);
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(ProductStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE.toString())),
                new SimpleComboBoxModel(ProductStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE.toString())));
        ComboBoxUtils.selectIndex(cbStatus, 0);
        initTablePackageProduct(locale);
        initTableProductPrice(locale);
    }

    private void initTablePackageProduct(Locale locale) {
        TableViewUtils.setColumnValue(colName, PackageProductVM::getName);
        TableViewUtils.setColumnValue(colUnit, PackageProductVM::getUnitLabel);
        TableViewUtils.initTableColumn(
                colQuantityInPackage,
                new NumberCellFactory<>(locale),
                PackageProductVM::getQuantityInPackage,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colCurrentQuantity,
                new NumberCellFactory<>(locale),
                PackageProductVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colGeneralSellingPrice,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                PackageProductVM::getGeneralSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPrescriptionSellingPrice,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                PackageProductVM::getPrescriptionSellingPrice,
                StyleConstants.ALIGN_RIGHT);
    }

    private void initTableProductPrice(Locale locale) {
        TableViewUtils.initTableColumn(
                colGeneralSellingPrice1,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                ProductPriceVM::getGeneralSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPrescriptionSellingPrice1,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                ProductPriceVM::getPrescriptionSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.setColumnValue(colProductPricePurchaseInvoiceNumber, ProductPriceVM::getPurchaseInvoiceNumber);
        TableViewUtils.setColumnValue(colProductPriceSaleInvoiceNumber, ProductPriceVM::getSaleInvoiceNumber);
        TableViewUtils.setColumnValue(colProductPriceActivity, ProductPriceVM::getActivity);
        TableViewUtils.setColumnValue(colProductPriceRemarks, ProductPriceVM::getRemarks);
        TableViewUtils.setColumnValue(colProductPriceUser, ProductPriceVM::getUserFullName);
        TableViewUtils.initTableColumn(
                colProductPriceCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ProductPriceVM::getCreatedAt);
        TableViewUtils.enableSort(false, tblPrice);
    }

    @Override
    protected void initDataSaveControlValues() {
        currentPackage = getPageData();
        Locale locale = resources.getLocale();
        selectedProductCategory = productCategoryService
                .getProductCategoryByCode(CommonConstants.PRODUCT_CATEGORY_CODE_CUSTOM_PACKAGE, locale.getLanguage());
        selectedUnit = unitService.getUnitByCode(CommonConstants.UNIT_CODE_BUNDLE, locale.getLanguage());
        tfCategory.setText(selectedProductCategory.getName());
        tfUnit.setText(selectedUnit.getLabel());
        tfQuantity.setText("1");
        tfName.setText(currentPackage.getName());
        tfBarcode.setText(currentPackage.getBarcode());
        tfCode.setText(currentPackage.getCode());
        tfDescription.setText(currentPackage.getDescription());
        BigDecimal generalSellingPrice = currentPackage.getGeneralSellingPrice();
        if (generalSellingPrice != null) {
            tfGeneralSellPrice.setText(toStringOrEmpty(generalSellingPrice.doubleValue()));
        }
        BigDecimal prescriptionSellingPrice = currentPackage.getPrescriptionSellingPrice();
        if (prescriptionSellingPrice != null) {
            tfPrescriptionSellPrice.setText(toStringOrEmpty(prescriptionSellingPrice.doubleValue()));
        }
        loadProductPrice(currentPackage.getId());
        loadPackageProducts(currentPackage.getId());
        if (!isPharmacyFeatureEnabled()) {
            setVisibleInLayout(false, vboxPresciptionSellPrice);
            tblProducts.getColumns().remove(colPrescriptionSellingPrice);
            tblPrice.getColumns().remove(colPrescriptionSellingPrice1);
        }
    }

    @Override
    protected Object save() {
        ProductEditVM productEdit = new ProductEditVM();
        productEdit.setName(tfName.getText());
        productEdit.setCode(tfCode.getText());
        productEdit.setBarcode(tfBarcode.getText());
        productEdit.setDescription(tfDescription.getText());
        productEdit.setProductCategory(selectedProductCategory);
        productEdit.setUnit(selectedUnit);
        SimpleComboBoxModel status = ComboBoxUtils.getSelectedItem(cbStatus);
        productEdit.setStatus(status.getValue());
        productEdit.setGeneralSellingPrice(toBigDecimalOrNull(tfGeneralSellPrice.getText(), DECIMAL_SCALE));
        productEdit.setPrescriptionSellingPrice(toBigDecimalOrNull(tfPrescriptionSellPrice.getText(), DECIMAL_SCALE));
        productEdit.setPriceRemarks(tfPriceRemarks.getText());
        productService.updatePackage(productEdit, currentPackage.getId(), tblProducts.getItems());
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
        validator.validateBlank(tfCode, MessageCode.ERROR_EMPTY_CODE);
        validator.validateCustom(() -> tblProducts.getItems().isEmpty(), MessageCode.ERROR_EMPTY_PRODUCT);
    }

    @Override
    protected void initServices() {
        productCategoryService = SpringUtils.getBean(ProductCategoryService.class);
        unitService = SpringUtils.getBean(UnitService.class);
        productService = SpringUtils.getBean(ProductService.class);
        objectConverter = SpringUtils.getBean(ObjectConverter.class);
    }

    public void handleSelectedProduct(ChooseResultVM<ProductVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(product -> {
            selectedProduct = product;
            tfProduct.setText(product.getName());
        }, () -> {
            selectedProduct = null;
            tfProduct.setText("");
        });
    }

    private ValidationResult validateAddProduct() {
        boolean isProductSelected = selectedProduct != null;
        ControlValidator cv = new ControlValidator(resources);
        cv.validateCustom(() -> !isProductSelected, MessageCode.ERROR_EMPTY_PRODUCT);
        cv.validateCustom(
                () -> isProductSelected && CommonConstants.PRODUCT_CATEGORY_CODE_CUSTOM_PACKAGE
                        .equals(selectedProduct.getCategoryCode()),
                MessageCode.ERROR_PRODUCT_CATEGORY_CUSTOM_PACKAGE_NOT_ALLOWED);
        return cv.getResult();
    }

    private int getProductIndexInTable(ProductVM product, TableView<PackageProductVM> table) {
        Predicate<PackageProductVM> productExists = item -> item.getId().equals(product.getId());
        return TableViewUtils.getItemIndex(productExists, table);
    }

    private void resetControls() {
        TextFieldUtils.setTextEmpty(
                tfName,
                tfCode,
                tfBarcode,
                tfDescription,
                tfGeneralSellPrice,
                tfPrescriptionSellPrice,
                tfPriceRemarks);
        ComboBoxUtils.selectIndex(cbStatus, 0);
    }

    private void calculatePrice() {
        BigDecimal generalSellPrice = null;
        BigDecimal prescriptionSellPrice = null;
        for (PackageProductVM pp : tblProducts.getItems()) {
            Integer qtyInPackage = pp.getQuantityInPackage();
            if (pp.getGeneralSellingPrice() != null) {
                BigDecimal total = pp.getGeneralSellingPrice().multiply(BigDecimal.valueOf(qtyInPackage));
                generalSellPrice = generalSellPrice == null ? total : generalSellPrice.add(total);
            }
            if (pp.getPrescriptionSellingPrice() != null) {
                BigDecimal total = pp.getPrescriptionSellingPrice().multiply(BigDecimal.valueOf(qtyInPackage));
                prescriptionSellPrice = prescriptionSellPrice == null ? total : prescriptionSellPrice.add(total);
            }
        }
        if (generalSellPrice != null) {
            tfGeneralSellPrice.setText(toStringOrEmpty(generalSellPrice.doubleValue()));
        }
        if (prescriptionSellPrice != null) {
            tfPrescriptionSellPrice.setText(toStringOrEmpty(prescriptionSellPrice.doubleValue()));
        }
    }

    private void loadProductPrice(Long productId) {
        tblPrice.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblPrice.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> productService.getProductPriceByProductId(productId))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblPrice.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblPrice.setItems(FXCollections.observableList(list));
                }));
    }

    private void loadPackageProducts(Long productId) {
        tblProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblProducts.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> productService.getPackageProductsByProductId(productId))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblProducts.setItems(FXCollections.observableList(list));
                }));
    }

}

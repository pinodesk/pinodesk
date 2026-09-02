package pinodesk.controller.catalog.product;

import static com.pinodesk.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.pinodesk.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.pinodesk.toolbox.data.StringNumberUtils.toIntegerOrZero;
import static pinodesk.constant.CommonConstants.DECIMAL_SCALE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.pinodesk.pandora.factory.DefaultCellFactory;
import com.pinodesk.pandora.factory.LocalDateCellFactory;
import com.pinodesk.pandora.factory.LocalDateTimeCellFactory;
import com.pinodesk.pandora.factory.NumberCellFactory;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.AlertResult;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.TableViewUtils;
import com.pinodesk.pandora.utility.TextFieldUtils;
import com.pinodesk.pandora.utility.ValidationResult;
import com.pinodesk.toolbox.data.StringNumberUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pinodesk.constant.Activity;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.DiscountType;
import pinodesk.constant.MenuCodeConstants;
import pinodesk.constant.MessageCode;
import pinodesk.constant.ProductStatus;
import pinodesk.constant.StyleConstants;
import pinodesk.controller.CommonDataSaveController;
import pinodesk.service.DrugClassificationService;
import pinodesk.service.DrugService;
import pinodesk.service.ProductCategoryService;
import pinodesk.service.ProductService;
import pinodesk.service.UnitService;
import pinodesk.util.SpringUtils;
import pinodesk.util.TaskUtils;
import pinodesk.viewmodel.ChooseResultVM;
import pinodesk.viewmodel.DrugClassificationVM;
import pinodesk.viewmodel.DrugVM;
import pinodesk.viewmodel.ProductCategoryVM;
import pinodesk.viewmodel.ProductEditVM;
import pinodesk.viewmodel.ProductExpiryAddVM;
import pinodesk.viewmodel.ProductExpiryVM;
import pinodesk.viewmodel.ProductPriceVM;
import pinodesk.viewmodel.ProductPurchaseVM;
import pinodesk.viewmodel.ProductStockVM;
import pinodesk.viewmodel.ProductVM;
import pinodesk.viewmodel.UnitVM;

public class ProductEditController extends CommonDataSaveController {

    @FXML
    private TabPane tabPaneEditProduct;

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
    private VBox vboxMedicine;

    @FXML
    private TextField tfDrugClassification;

    @FXML
    private TextField tfIndication;

    @FXML
    private TextField tfContraindication;

    @FXML
    private TextField tfGeneralSellingPrice;

    @FXML
    private VBox vboxPresciptionSellPrice;

    @FXML
    private TextField tfPrescriptionSellingPrice;

    @FXML
    private TextField tfPriceRemarks;

    @FXML
    private TextField tfStockQuantity;

    @FXML
    private TextField tfStockRemarks;

    @FXML
    private TextField tfBatchNumber;

    @FXML
    private DatePicker dpExpiredDate;

    @FXML
    private TextField tfExpiryQuantity;

    @FXML
    private TextField tfExpiryRemarks;

    @FXML
    private TableView<ProductPriceVM> tblPrice;

    @FXML
    private TableColumn<ProductPriceVM, BigDecimal> colGeneralSellingPrice;

    @FXML
    private TableColumn<ProductPriceVM, BigDecimal> colPrescripionSellingPrice;

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
    private TableView<ProductStockVM> tblStock;

    @FXML
    private TableColumn<ProductStockVM, Integer> colQuantityIn;

    @FXML
    private TableColumn<ProductStockVM, Integer> colQuantityOut;

    @FXML
    private TableColumn<ProductStockVM, Integer> colFinalQuantity;

    @FXML
    private TableColumn<ProductStockVM, String> colProductStockPurchaseInvoiceNumber;

    @FXML
    private TableColumn<ProductStockVM, String> colProductStockSaleInvoceNumber;

    @FXML
    private TableColumn<ProductStockVM, String> colProductStockActivity;

    @FXML
    private TableColumn<ProductStockVM, String> colProductStockRemarks;

    @FXML
    private TableColumn<ProductStockVM, String> colProductStockUser;

    @FXML
    private TableColumn<ProductStockVM, LocalDateTime> colProductStockCreatedAt;

    @FXML
    private TableView<ProductExpiryVM> tblExpiry;

    @FXML
    private TableColumn<ProductExpiryVM, String> colBatchNumber;

    @FXML
    private TableColumn<ProductExpiryVM, LocalDate> colExpiredDate;

    @FXML
    private TableColumn<ProductExpiryVM, Integer> colProductExpiryQuantityIn;

    @FXML
    private TableColumn<ProductExpiryVM, Integer> colProductExpiryQuantityOut;

    @FXML
    private TableColumn<ProductExpiryVM, Integer> colProductExpiryFinalQuantity;

    @FXML
    private TableColumn<ProductExpiryVM, String> colProductExpiryPurchaseInvoiceNumber;

    @FXML
    private TableColumn<ProductExpiryVM, String> colProductExpirySaleInvoiceNumber;

    @FXML
    private TableColumn<ProductExpiryVM, String> colProductExpiryActivity;

    @FXML
    private TableColumn<ProductExpiryVM, String> colProductExpiryRemarks;

    @FXML
    private TableColumn<ProductExpiryVM, String> colProductExpiryUser;

    @FXML
    private TableColumn<ProductExpiryVM, LocalDateTime> colProductExpiryCreatedAt;

    @FXML
    protected Button btnRemove;

    @FXML
    private Button btnAddExpiry;

    @FXML
    private Tab tabPrice;

    @FXML
    private Tab tabStock;

    @FXML
    private Tab tabExpiry;

    @FXML
    private Tab tabPurchases;

    @FXML
    private TableView<ProductPurchaseVM> tblPurchases;

    @FXML
    private TableColumn<ProductPurchaseVM, BigDecimal> colProductPurchaseBuyingPrice;

    @FXML
    private TableColumn<ProductPurchaseVM, BigDecimal> colProductPurchaseBuyingPriceDiscount;

    @FXML
    private TableColumn<ProductPurchaseVM, String> colProductPurchaseDiscount;

    @FXML
    private TableColumn<ProductPurchaseVM, LocalDate> colProductPurchaseInvoiceDate;

    @FXML
    private TableColumn<ProductPurchaseVM, String> colProductPurchaseInvoiceNumber;

    @FXML
    private TableColumn<ProductPurchaseVM, Integer> colProductPurchaseQuantity;

    @FXML
    private TableColumn<ProductPurchaseVM, String> colProductPurchaseSupplierName;

    private List<ProductPriceVM> productPrices;

    private List<ProductStockVM> productStocks;

    private List<ProductExpiryVM> productExpiries;

    private List<ProductPurchaseVM> productPurchases;

    private ProductVM currentProduct;

    private ProductService productService;

    private ProductCategoryService productCategoryService;

    private UnitService unitService;

    private DrugClassificationService drugClassificationService;

    private DrugService drugService;

    private ProductCategoryVM selectedProductCategory;

    private UnitVM selectedUnit;

    private DrugClassificationVM selectedDrugClassification;

    @FXML
    void onSelectionChangedTabPrice(Event event) {
        if (tabPrice.isSelected() && productPrices == null) {
            loadProductPrice(currentProduct.getId());
        }
    }

    @FXML
    void onSelectionChangedTabStock(Event event) {
        if (tabStock.isSelected() && productStocks == null) {
            loadProductStock(currentProduct.getId());
        }
    }

    @FXML
    void onSelectionChangedTabExpiry(Event event) {
        if (tabExpiry.isSelected() && productExpiries == null) {
            loadProductExpiry(currentProduct.getId());
        }
    }

    @FXML
    void onSelectionChangedTabPurchases(Event event) {
        if (tabPurchases.isSelected() && productPurchases == null) {
            loadProductPurchases(currentProduct.getId());
        }
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_PRODUCT);
        if (result.isConfirmed()) {
            productService.removeProducts(Arrays.asList(currentProduct.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_PRODUCT);
            setPageData(Boolean.TRUE);
            close();
        }
    }

    @FXML
    void onActionBtnAddExpiry(ActionEvent event) {
        if (dpExpiredDate.getValue() == null && StringUtils.isBlank(tfExpiryQuantity.getText())) {
            return;
        }
        ControlValidator cv = new ControlValidator(resources);
        cv.validateCustom(this::isExpiredDateRequired, MessageCode.ERROR_INVALID_DATE_FORMAT);
        cv.validateCustom(this::isExpiryQuantityRequired, MessageCode.ERROR_INCORRECT_PRODUCT_EXPIRY_QUANTITY);
        ValidationResult result = cv.getResult();
        if (!result.isValid()) {
            displayError(result.getMessages());
            return;
        }
        ProductExpiryAddVM vm = new ProductExpiryAddVM();
        vm.setProductId(currentProduct.getId());
        vm.setBatchNumber(tfBatchNumber.getText());
        vm.setExpiredDate(dpExpiredDate.getValue());
        vm.setQuantity(toIntegerOrNull(tfExpiryQuantity.getText()));
        vm.setRemarks(tfExpiryRemarks.getText());
        productService.addProductExpiry(vm, Activity.EDIT_PRODUCT);
        loadProductExpiry(currentProduct.getId());
        TextFieldUtils.setTextEmpty(tfBatchNumber, tfExpiryQuantity, tfExpiryRemarks);
        dpExpiredDate.setValue(null);
    }

    @Override
    protected void initDataSaveControlActions() {
        disableWriteAction(MenuCodeConstants.CATALOG_PRODUCTS, btnSave, btnRemove, btnAddExpiry);
        handleReadAction(MenuCodeConstants.TRANSACTION_PURCHASES, isReadAllowed -> {
            if (!Boolean.TRUE.equals(isReadAllowed)) {
                tabPaneEditProduct.getTabs().remove(tabPurchases);
            }
        });
        initCustomDatePicker(dpExpiredDate);
        Locale locale = resources.getLocale();
        TextFieldUtils.setDecimalTextFields(tfGeneralSellingPrice, tfPrescriptionSellingPrice);
        TextFieldUtils.setDigitTextFields(tfBarcode, tfStockQuantity, tfExpiryQuantity);
        setProductCategoryChooser(tfCategory, this::handleSelectedProductCategory, tfUnit.getParent());
        setUnitChooser(tfUnit, this::handleSelectedUnit, cbStatus);
        setDrugClassificationChooser(tfDrugClassification, this::handleSelectedDrugClassification, tfIndication);
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(ProductStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE.toString())),
                new SimpleComboBoxModel(ProductStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE.toString())));
        ComboBoxUtils.selectIndex(cbStatus, 0);
        initTableProductPrice(locale);
        initTableProductStock();
        initTableProductExpiry(locale);
        initTablePurchases(locale);
    }

    private void initTableProductExpiry(Locale locale) {
        TableViewUtils.setColumnValue(colBatchNumber, ProductExpiryVM::getBatchNumber);
        TableViewUtils.initTableColumn(
                colExpiredDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ProductExpiryVM::getExpiredDate);
        TableViewUtils.initTableColumn(
                colProductExpiryQuantityIn,
                new NumberCellFactory<>(locale),
                ProductExpiryVM::getQuantityIn,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colProductExpiryQuantityOut,
                new NumberCellFactory<>(locale),
                ProductExpiryVM::getQuantityOut,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colProductExpiryFinalQuantity,
                new NumberCellFactory<>(locale),
                ProductExpiryVM::getFinalQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.setColumnValue(colProductExpiryPurchaseInvoiceNumber, ProductExpiryVM::getPurchaseInvoiceNumber);
        TableViewUtils.setColumnValue(colProductExpirySaleInvoiceNumber, ProductExpiryVM::getSaleInvoiceNumber);
        TableViewUtils.setColumnValue(colProductExpiryActivity, ProductExpiryVM::getActivity);
        TableViewUtils.setColumnValue(colProductExpiryRemarks, ProductExpiryVM::getRemarks);
        TableViewUtils.setColumnValue(colProductExpiryUser, ProductExpiryVM::getUserFullName);
        TableViewUtils.initTableColumn(
                colProductExpiryCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ProductExpiryVM::getCreatedAt);
        TableViewUtils.enableSort(false, tblExpiry);
    }

    private void initTableProductStock() {
        TableViewUtils.setColumnValue(colQuantityIn, ProductStockVM::getQuantityIn);
        TableViewUtils.setColumnValue(colQuantityOut, ProductStockVM::getQuantityOut);
        TableViewUtils.setColumnValue(colFinalQuantity, ProductStockVM::getFinalQuantity);
        TableViewUtils.setColumnValue(colProductStockPurchaseInvoiceNumber, ProductStockVM::getPurchaseInvoiceNumber);
        TableViewUtils.setColumnValue(colProductStockSaleInvoceNumber, ProductStockVM::getSaleInvoiceNumber);
        TableViewUtils.setColumnValue(colProductStockActivity, ProductStockVM::getActivity);
        TableViewUtils.setColumnValue(colProductStockRemarks, ProductStockVM::getRemarks);
        TableViewUtils.setColumnValue(colProductStockUser, ProductStockVM::getUserFullName);
        TableViewUtils.initTableColumn(
                colProductStockCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ProductStockVM::getCreatedAt);
        TableViewUtils.enableSort(false, tblStock);
    }

    private void initTableProductPrice(Locale locale) {
        TableViewUtils.initTableColumn(
                colGeneralSellingPrice,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                ProductPriceVM::getGeneralSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPrescripionSellingPrice,
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

    private void initTablePurchases(Locale locale) {
        TableViewUtils.setColumnValue(colProductPurchaseInvoiceNumber, ProductPurchaseVM::getInvoiceNumber);
        TableViewUtils.setColumnValue(colProductPurchaseSupplierName, ProductPurchaseVM::getSupplierName);
        TableViewUtils.setColumnValue(colProductPurchaseInvoiceDate, ProductPurchaseVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colProductPurchaseBuyingPrice,
                new NumberCellFactory<>(locale),
                ProductPurchaseVM::getBuyingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colProductPurchaseBuyingPriceDiscount,
                new NumberCellFactory<>(DECIMAL_SCALE, locale),
                ProductPurchaseVM::getBuyingPriceDiscount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colProductPurchaseDiscount, new DefaultCellFactory<>(), (vm) -> {
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
                colProductPurchaseQuantity,
                new NumberCellFactory<>(locale),
                ProductPurchaseVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
    }

    @Override
    protected void initDataSaveControlValues() {
        currentProduct = getPageData();
        selectedProductCategory = productCategoryService.getProductCategoryById(currentProduct.getCategoryId());
        selectedUnit = unitService.getUnitById(currentProduct.getUnitId());
        tfName.setText(currentProduct.getName());
        tfCode.setText(currentProduct.getCode());
        tfBarcode.setText(currentProduct.getBarcode());
        tfDescription.setText(currentProduct.getDescription());
        tfCategory.setText(selectedProductCategory.getName());
        tfUnit.setText(selectedUnit.getLabel());
        ComboBoxUtils.select(cbStatus, () -> cbStatus.getItems().stream().filter(vm -> {
            ProductStatus status = vm.getValue();
            return status.toString().equals(currentProduct.getStatus());
        }).findAny().orElseThrow());
        if (isProductCategoryDrugSelected()) {
            DrugVM drug = drugService.getDrugByProductId(currentProduct.getId());
            if (StringUtils.isNotBlank(drug.getClassificationCode())) {
                selectedDrugClassification = drugClassificationService
                        .getDrugClassificationByCode(drug.getClassificationCode(), resources.getLocale().getLanguage());
                tfDrugClassification.setText(selectedDrugClassification.getName());
            }
            tfIndication.setText(drug.getIndication());
            tfContraindication.setText(drug.getContraindication());
            vboxMedicine.setDisable(false);
            vboxPresciptionSellPrice.setDisable(false);
        }
        if (!isPharmacyFeatureEnabled()) {
            setVisibleInLayout(false, vboxMedicine);
            setVisibleInLayout(false, vboxPresciptionSellPrice);
            tblPrice.getColumns().remove(colPrescripionSellingPrice);
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
        productEdit.setDrugClassification(selectedDrugClassification);
        productEdit.setIndication(tfIndication.getText());
        productEdit.setContraindication(tfContraindication.getText());
        productEdit.setGeneralSellingPrice(toBigDecimalOrNull(tfGeneralSellingPrice.getText(), DECIMAL_SCALE));
        productEdit
                .setPrescriptionSellingPrice(toBigDecimalOrNull(tfPrescriptionSellingPrice.getText(), DECIMAL_SCALE));
        productEdit.setPriceRemarks(tfPriceRemarks.getText());
        productEdit.setStockQuantity(toIntegerOrNull(tfStockQuantity.getText()));
        productEdit.setStockRemarks(tfStockRemarks.getText());
        productEdit.setExpiredDate(dpExpiredDate.getValue());
        productEdit.setBatchNumber(tfBatchNumber.getText());
        productEdit.setExpiryQuantity(toIntegerOrNull(tfExpiryQuantity.getText()));
        productEdit.setExpiryRemarks(tfExpiryRemarks.getText());
        productService.updateProduct(productEdit, currentProduct.getId());
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
        validator.validateBlank(tfCode, MessageCode.ERROR_EMPTY_CODE);
        validator.validateBlank(tfCategory, MessageCode.ERROR_EMPTY_CATEGORY);
        validator.validateBlank(tfUnit, MessageCode.ERROR_EMPTY_UNIT);
        validator.validateCustom(this::isExpiredDateRequired, MessageCode.ERROR_INVALID_DATE_FORMAT);
        validator.validateCustom(this::isExpiryQuantityRequired, MessageCode.ERROR_INCORRECT_PRODUCT_EXPIRY_QUANTITY);
        validator.validateCustom(
                this::isExpiryQuantityExceedStockQuantity,
                MessageCode.ERROR_INCORRECT_PRODUCT_EXPIRY_QUANTITY);
    }

    private boolean isExpiredDateRequired() {
        Integer expiryQty = toIntegerOrZero(tfExpiryQuantity.getText());
        LocalDate expiredDate = dpExpiredDate.getValue();
        return expiryQty > 0 && expiredDate == null;
    }

    private boolean isExpiryQuantityExceedStockQuantity() {
        Integer expiryQty = toIntegerOrZero(tfExpiryQuantity.getText());
        Integer stockQty = toIntegerOrNull(tfStockQuantity.getText());
        return stockQty != null && expiryQty.compareTo(stockQty) > 0;
    }

    private boolean isExpiryQuantityRequired() {
        return dpExpiredDate.getValue() != null && StringUtils.isBlank(tfExpiryQuantity.getText());
    }

    private boolean isProductCategoryDrugSelected() {
        return selectedProductCategory != null
                && selectedProductCategory.getCode().equals(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
    }

    @Override
    protected void initServices() {
        productService = SpringUtils.getBean(ProductService.class);
        productCategoryService = SpringUtils.getBean(ProductCategoryService.class);
        unitService = SpringUtils.getBean(UnitService.class);
        drugClassificationService = SpringUtils.getBean(DrugClassificationService.class);
        drugService = SpringUtils.getBean(DrugService.class);
    }

    private void handleSelectedProductCategory(ChooseResultVM<ProductCategoryVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(category -> {
            selectedProductCategory = category;
            tfCategory.setText(category.getName());
            boolean isDrug = isProductCategoryDrugSelected();
            vboxMedicine.setDisable(!isDrug);
            vboxPresciptionSellPrice.setDisable(!isDrug);
            TextFieldUtils
                    .setTextEmpty(tfDrugClassification, tfIndication, tfContraindication, tfPrescriptionSellingPrice);
        }, () -> {
            selectedProductCategory = null;
            selectedDrugClassification = null;
            vboxMedicine.setDisable(true);
            vboxPresciptionSellPrice.setDisable(true);
            TextFieldUtils.setTextEmpty(
                    tfCategory,
                    tfDrugClassification,
                    tfIndication,
                    tfContraindication,
                    tfPrescriptionSellingPrice);
        });
    }

    public void handleSelectedUnit(ChooseResultVM<UnitVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(unit -> {
            selectedUnit = unit;
            tfUnit.setText(unit.getLabel());
        }, () -> {
            selectedUnit = null;
            tfUnit.setText("");
        });
    }

    public void handleSelectedDrugClassification(ChooseResultVM<DrugClassificationVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(classification -> {
            selectedDrugClassification = classification;
            tfDrugClassification.setText(classification.getName());
        }, () -> {
            selectedDrugClassification = null;
            tfDrugClassification.setText("");
        });
    }

    private void loadProductExpiry(Long productId) {
        tblExpiry.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblExpiry.setItems(FXCollections.observableArrayList());
        TaskUtils.runTask("Load product expiry", () -> {
            productExpiries = productService.getProductExpiryByProductId(productId);
            Platform.runLater(() -> {
                if (productExpiries.isEmpty()) {
                    tblExpiry.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                }
                tblExpiry.setItems(FXCollections.observableList(productExpiries));
            });
        });
    }

    private void loadProductStock(Long productId) {
        tblStock.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblStock.setItems(FXCollections.observableArrayList());
        TaskUtils.runTask("Load product stock", () -> {
            productStocks = productService.getProductStockByProductId(productId);
            Platform.runLater(() -> {
                if (productStocks.isEmpty()) {
                    tblStock.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                }
                tblStock.setItems(FXCollections.observableList(productStocks));
            });
        });
    }

    private void loadProductPrice(Long productId) {
        tblPrice.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblPrice.setItems(FXCollections.observableArrayList());
        TaskUtils.runTask("Load product price", () -> {
            productPrices = productService.getProductPriceByProductId(productId);
            Platform.runLater(() -> {
                if (productPrices.isEmpty()) {
                    tblPrice.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                }
                tblPrice.setItems(FXCollections.observableList(productPrices));
            });
        });
    }

    private void loadProductPurchases(Long productId) {
        tblPurchases.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblPurchases.setItems(FXCollections.observableArrayList());
        TaskUtils.runTask("Load product purchase", () -> {
            productPurchases = productService.getProductPurchaseByProductId(productId);
            Platform.runLater(() -> {
                if (productPurchases.isEmpty()) {
                    tblPurchases.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                }
                tblPurchases.setItems(FXCollections.observableList(productPurchases));
                TableViewUtils.sortDescending(tblPurchases, colProductPurchaseInvoiceDate);
            });
        });
    }

}

package stoready.desktop.controller.product;

import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toIntegerOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;
import com.gitlab.muhammadkholidb.pandora.utility.ValidationResult;
import com.gitlab.muhammadkholidb.toolbox.data.DateTimeUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import stoready.desktop.constant.Activity;
import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.CommonLabel;
import stoready.desktop.constant.MenuCodeConstants;
import stoready.desktop.constant.MessageCode;
import stoready.desktop.constant.ProductStatus;
import stoready.desktop.constant.StyleConstants;
import stoready.desktop.controller.CommonDataSaveController;
import stoready.desktop.service.DrugClassificationService;
import stoready.desktop.service.DrugService;
import stoready.desktop.service.ProductCategoryService;
import stoready.desktop.service.ProductService;
import stoready.desktop.service.UnitService;
import stoready.desktop.viewmodel.ChooseResultVM;
import stoready.desktop.viewmodel.DrugClassificationVM;
import stoready.desktop.viewmodel.DrugVM;
import stoready.desktop.viewmodel.ProductCategoryVM;
import stoready.desktop.viewmodel.ProductEditVM;
import stoready.desktop.viewmodel.ProductExpiryAddVM;
import stoready.desktop.viewmodel.ProductExpiryVM;
import stoready.desktop.viewmodel.ProductPriceVM;
import stoready.desktop.viewmodel.ProductStockVM;
import stoready.desktop.viewmodel.ProductVM;
import stoready.desktop.viewmodel.UnitVM;

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
    private MaskedTextField tfExpiredDate;

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
        if (StringUtils.isAllBlank(tfExpiredDate.getPlainText(), tfExpiryQuantity.getText())) {
            return;
        }
        ControlValidator cv = new ControlValidator(resources);
        cv.validateCustom(this::isInvalidExpiredDate, MessageCode.ERROR_INVALID_DATE_FORMAT);
        cv.validateCustom(this::isExpiryQuantityRequired, MessageCode.ERROR_INCORRECT_PRODUCT_EXPIRY_QUANTITY);
        ValidationResult result = cv.getResult();
        if (!result.isValid()) {
            displayError(result.getMessages());
            return;
        }
        ProductExpiryAddVM vm = new ProductExpiryAddVM();
        vm.setProductId(currentProduct.getId());
        vm.setBatchNumber(tfBatchNumber.getText());
        vm.setExpiredDate(
                DateTimeUtils.parseLocalDateQuietly(tfExpiredDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        vm.setQuantity(toIntegerOrNull(tfExpiryQuantity.getText()));
        vm.setRemarks(tfExpiryRemarks.getText());
        productService.addProductExpiry(vm, Activity.EDIT_PRODUCT);
        loadProductExpiry(currentProduct.getId());
        TextFieldUtils.setTextEmpty(tfBatchNumber, tfExpiryQuantity, tfExpiryRemarks);
        tfExpiredDate.setPlainText("");
    }

    @Override
    protected void initDataSaveControlActions() {
        disableWriteAction(MenuCodeConstants.MASTER_PRODUCTS, btnSave, btnRemove, btnAddExpiry);
        Locale locale = resources.getLocale();
        TextFieldUtils.setDigitTextFields(
                tfBarcode,
                tfGeneralSellingPrice,
                tfPrescriptionSellingPrice,
                tfStockQuantity,
                tfExpiryQuantity);
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
                new NumberCellFactory<>(locale),
                ProductPriceVM::getGeneralSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPrescripionSellingPrice,
                new NumberCellFactory<>(locale),
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
        currentProduct = getPageData();
        Long productId = currentProduct.getId();
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
        loadProductPrice(productId);
        loadProductStock(productId);
        loadProductExpiry(productId);
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
        productEdit.setGeneralSellingPrice(toBigDecimalOrNull(tfGeneralSellingPrice.getText()));
        productEdit.setPrescriptionSellingPrice(toBigDecimalOrNull(tfPrescriptionSellingPrice.getText()));
        productEdit.setPriceRemarks(tfPriceRemarks.getText());
        productEdit.setStockQuantity(toIntegerOrNull(tfStockQuantity.getText()));
        productEdit.setStockRemarks(tfStockRemarks.getText());
        String expiredDate = tfExpiredDate.getTextMasked();
        productEdit
                .setExpiredDate(DateTimeUtils.parseLocalDateQuietly(expiredDate, CommonConstants.DATE_DISPLAY_PATTERN));
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
        validator.validateCustom(this::isInvalidExpiredDate, MessageCode.ERROR_INVALID_DATE_FORMAT);
        validator.validateCustom(this::isExpiryQuantityRequired, MessageCode.ERROR_INCORRECT_PRODUCT_EXPIRY_QUANTITY);
        validator.validateCustom(
                this::isExpiryQuantityExceedStockQuantity,
                MessageCode.ERROR_INCORRECT_PRODUCT_EXPIRY_QUANTITY);
    }

    private boolean isInvalidExpiredDate() {
        Integer expiryQty = toIntegerOrZero(tfExpiryQuantity.getText());
        LocalDate expiredDate = DateTimeUtils
                .parseLocalDateQuietly(tfExpiredDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN);
        boolean isExpiredDateRequired = expiryQty > 0 && expiredDate == null;
        boolean isExpiredDateInvalid = StringUtils.isNotBlank(tfExpiredDate.getPlainText())
                && (expiredDate == null || expiredDate.isBefore(LocalDate.now()));
        return isExpiredDateRequired || isExpiredDateInvalid;
    }

    private boolean isExpiryQuantityExceedStockQuantity() {
        Integer expiryQty = toIntegerOrZero(tfExpiryQuantity.getText());
        Integer stockQty = toIntegerOrNull(tfStockQuantity.getText());
        return stockQty != null && expiryQty.compareTo(stockQty) > 0;
    }

    private boolean isExpiryQuantityRequired() {
        return StringUtils.isNotBlank(tfExpiredDate.getPlainText()) && StringUtils.isBlank(tfExpiryQuantity.getText());
    }

    private boolean isProductCategoryDrugSelected() {
        return selectedProductCategory != null
                && selectedProductCategory.getCode().equals(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = ctx.getBean(ProductService.class);
        productCategoryService = ctx.getBean(ProductCategoryService.class);
        unitService = ctx.getBean(UnitService.class);
        drugClassificationService = ctx.getBean(DrugClassificationService.class);
        drugService = ctx.getBean(DrugService.class);
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
        AsyncUtils.supply(() -> productService.getProductExpiryByProductId(productId))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblExpiry.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblExpiry.setItems(FXCollections.observableList(list));
                }));
    }

    private void loadProductStock(Long productId) {
        tblStock.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblStock.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> productService.getProductStockByProductId(productId))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblStock.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblStock.setItems(FXCollections.observableList(list));
                }));
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

}

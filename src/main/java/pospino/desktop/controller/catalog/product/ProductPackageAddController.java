package pospino.desktop.controller.catalog.product;

import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toIntegerOrDefault;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toStringOrEmpty;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.function.Predicate;

import com.gitlab.mudiasoft.pandora.constant.KeyConstants;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.model.SimpleComboBoxModel;
import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;
import com.gitlab.mudiasoft.pandora.utility.ValidationResult;
import com.gitlab.mudiasoft.toolbox.jackson.ObjectConverter;

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
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.ProductStatus;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.service.ProductCategoryService;
import pospino.desktop.service.ProductService;
import pospino.desktop.service.UnitService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.PackageProductVM;
import pospino.desktop.viewmodel.ProductAddVM;
import pospino.desktop.viewmodel.ProductCategoryVM;
import pospino.desktop.viewmodel.ProductVM;
import pospino.desktop.viewmodel.UnitVM;

public class ProductPackageAddController extends CommonDataSaveController {

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
    private TableColumn<PackageProductVM, Integer> colQuantityInPackage;

    @FXML
    private TableColumn<PackageProductVM, Integer> colCurrentQuantity;

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
    private Button btnSaveAndAdd;

    private ProductService productService;

    private ProductCategoryService productCategoryService;

    private UnitService unitService;

    private ProductCategoryVM selectedProductCategory;

    private UnitVM selectedUnit;

    private ProductVM selectedProduct;

    private ObjectConverter objectConverter;

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
        TextFieldUtils.setDigitTextFields(tfBarcode, tfGeneralSellPrice, tfPrescriptionSellPrice);
        setProductChooser(tfProduct, this::handleSelectedProduct, tfQuantity);
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(ProductStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE.toString())),
                new SimpleComboBoxModel(ProductStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE.toString())));
        ComboBoxUtils.selectIndex(cbStatus, 0);
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
                new NumberCellFactory<>(locale),
                PackageProductVM::getGeneralSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPrescriptionSellingPrice,
                new NumberCellFactory<>(locale),
                PackageProductVM::getPrescriptionSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyConstants.CTRL_SHIFT_S.match(event)) {
                btnSaveAndAdd.fire();
                return;
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        Locale locale = resources.getLocale();
        selectedProductCategory = productCategoryService
                .getProductCategoryByCode(CommonConstants.PRODUCT_CATEGORY_CODE_CUSTOM_PACKAGE, locale.getLanguage());
        selectedUnit = unitService.getUnitByCode(CommonConstants.UNIT_CODE_BUNDLE, locale.getLanguage());
        tfCategory.setText(selectedProductCategory.getName());
        tfUnit.setText(selectedUnit.getLabel());
        tfQuantity.setText("1");
        if (!isPharmacyFeatureEnabled()) {
            setVisibleInLayout(false, vboxPresciptionSellPrice);
            tblProducts.getColumns().remove(colPrescriptionSellingPrice);
        }
    }

    @Override
    protected Object save() {
        ProductAddVM productAdd = new ProductAddVM();
        productAdd.setName(tfName.getText());
        productAdd.setCode(tfCode.getText());
        productAdd.setBarcode(tfBarcode.getText());
        productAdd.setDescription(tfDescription.getText());
        productAdd.setProductCategory(selectedProductCategory);
        productAdd.setUnit(selectedUnit);
        SimpleComboBoxModel status = ComboBoxUtils.getSelectedItem(cbStatus);
        productAdd.setStatus(status.getValue());
        productAdd.setGeneralSellingPrice(toBigDecimalOrNull(tfGeneralSellPrice.getText()));
        productAdd.setPrescriptionSellingPrice(toBigDecimalOrNull(tfPrescriptionSellPrice.getText()));
        productAdd.setPriceRemarks(tfPriceRemarks.getText());
        productService.createPackage(productAdd, tblProducts.getItems());
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
        tblProducts.setItems(FXCollections.observableArrayList());
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
        tfGeneralSellPrice.setText(toStringOrEmpty(generalSellPrice));
        tfPrescriptionSellPrice.setText(toStringOrEmpty(prescriptionSellPrice));
    }

}

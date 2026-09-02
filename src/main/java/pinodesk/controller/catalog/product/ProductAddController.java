package pinodesk.controller.catalog.product;

import static com.pinodesk.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.pinodesk.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.pinodesk.toolbox.data.StringNumberUtils.toIntegerOrZero;
import static pinodesk.constant.CommonConstants.DECIMAL_SCALE;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import com.pinodesk.pandora.constant.KeyConstants;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.TextFieldUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.MessageCode;
import pinodesk.constant.ProductStatus;
import pinodesk.controller.CommonDataSaveController;
import pinodesk.service.ProductService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.ChooseResultVM;
import pinodesk.viewmodel.DrugClassificationVM;
import pinodesk.viewmodel.ProductAddVM;
import pinodesk.viewmodel.ProductCategoryVM;
import pinodesk.viewmodel.UnitVM;

public class ProductAddController extends CommonDataSaveController {

    @FXML
    private TabPane tabPaneAddProduct;

    @FXML
    private Tab tabProduct;

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
    private TextField tfGeneralSellPrice;

    @FXML
    private VBox vboxPresciptionSellPrice;

    @FXML
    private TextField tfPrescriptionSellPrice;

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
    private SplitMenuButton btnSaveAndAdd;

    private MenuItem btnSaveAndCopy;

    private ProductCategoryVM selectedProductCategory;

    private UnitVM selectedUnit;

    private DrugClassificationVM selectedDrugClassification;

    private ProductService productService;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_PRODUCT);
            resetControls();
            tabPaneAddProduct.getSelectionModel().select(tabProduct);
            setFocused(tfName);
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        initCustomDatePicker(dpExpiredDate);
        TextFieldUtils.setDecimalTextFields(tfGeneralSellPrice, tfPrescriptionSellPrice);
        TextFieldUtils.setDigitTextFields(tfBarcode, tfStockQuantity, tfExpiryQuantity);
        setProductCategoryChooser(tfCategory, this::handleSelectedProductCategory, tfUnit.getParent());
        setUnitChooser(tfUnit, this::handleSelectedUnit, cbStatus);
        setDrugClassificationChooser(tfDrugClassification, this::handleSelectedDrugClassification, tfIndication);
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(ProductStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE.toString())),
                new SimpleComboBoxModel(ProductStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE.toString())));
        ComboBoxUtils.selectIndex(cbStatus, 0);
        initBtnSaveAndAdd();
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyConstants.CTRL_SHIFT_S.match(event)) {
                btnSaveAndAdd.fire();
                return;
            }
            if (KeyConstants.CTRL_SHIFT_C.match(event)) {
                btnSaveAndCopy.fire();
                return;
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        if (!isPharmacyFeatureEnabled()) {
            setVisibleInLayout(false, vboxMedicine);
            setVisibleInLayout(false, vboxPresciptionSellPrice);
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
        productAdd.setDrugClassification(selectedDrugClassification);
        productAdd.setIndication(tfIndication.getText());
        productAdd.setContraindication(tfContraindication.getText());
        productAdd.setGeneralSellingPrice(toBigDecimalOrNull(tfGeneralSellPrice.getText(), DECIMAL_SCALE));
        productAdd.setPrescriptionSellingPrice(toBigDecimalOrNull(tfPrescriptionSellPrice.getText(), DECIMAL_SCALE));
        productAdd.setPriceRemarks(tfPriceRemarks.getText());
        productAdd.setStockQuantity(toIntegerOrNull(tfStockQuantity.getText()));
        productAdd.setStockRemarks(tfStockRemarks.getText());
        productAdd.setExpiredDate(dpExpiredDate.getValue());
        productAdd.setBatchNumber(tfBatchNumber.getText());
        productAdd.setExpiryQuantity(toIntegerOrNull(tfExpiryQuantity.getText()));
        productAdd.setExpiryRemarks(tfExpiryRemarks.getText());
        productService.createProduct(productAdd);
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
        Integer stockQty = toIntegerOrZero(tfStockQuantity.getText());
        return expiryQty.compareTo(stockQty) > 0;
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
    }

    private void resetControls() {
        TextFieldUtils.setTextEmpty(
                tfName,
                tfCode,
                tfBarcode,
                tfDescription,
                tfCategory,
                tfUnit,
                tfDrugClassification,
                tfIndication,
                tfContraindication,
                tfGeneralSellPrice,
                tfPrescriptionSellPrice,
                tfPriceRemarks,
                tfStockQuantity,
                tfStockRemarks,
                tfBatchNumber,
                tfExpiryQuantity,
                tfExpiryRemarks);
        dpExpiredDate.setValue(null);
        ComboBoxUtils.selectIndex(cbStatus, 0);
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
                    .setTextEmpty(tfDrugClassification, tfIndication, tfContraindication, tfPrescriptionSellPrice);
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
                    tfPrescriptionSellPrice);
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

    private void initBtnSaveAndAdd() {
        btnSaveAndCopy = new MenuItem(t.translate(CommonLabel.BTN_SAVE_AND_COPY));
        btnSaveAndCopy.setOnAction(event -> {
            processDataSave();
            if (isLastDataSaved()) {
                displayInfo(MessageCode.SUCCESS_ADD_PRODUCT);
                TextFieldUtils.setTextEmpty(tfUnit);
                selectedUnit = null;
                tabPaneAddProduct.getSelectionModel().select(tabProduct);
                setFocused(tfName);
            }
        });
        btnSaveAndAdd.getItems().addAll(btnSaveAndCopy);
    }

}

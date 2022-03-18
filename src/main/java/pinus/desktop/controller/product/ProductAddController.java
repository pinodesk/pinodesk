package pinus.desktop.controller.product;

import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toIntegerOrZero;

import java.time.LocalDate;

import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;
import com.gitlab.muhammadkholidb.toolbox.data.DateTimeUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.constant.ProductStatus;
import pinus.desktop.controller.CommonDataSaveController;
import pinus.desktop.service.ProductService;
import pinus.desktop.viewmodel.ChooseResultVM;
import pinus.desktop.viewmodel.DrugCategoryVM;
import pinus.desktop.viewmodel.ProductAddVM;
import pinus.desktop.viewmodel.ProductCategoryVM;
import pinus.desktop.viewmodel.UnitVM;

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
    private TextField tfDrugCategory;

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
    private MaskedTextField tfExpiredDate;

    @FXML
    private TextField tfExpiryQuantity;

    @FXML
    private TextField tfExpiryRemarks;

    @FXML
    private SplitMenuButton btnSaveAndAdd;

    private MenuItem btnSaveAndCopy;

    private ProductService productService;

    private ProductCategoryVM selectedProductCategory;

    private UnitVM selectedUnit;

    private DrugCategoryVM selectedDrugCategory;

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
        TextFieldUtils.setDigitTextFields(
                tfBarcode,
                tfGeneralSellPrice,
                tfPrescriptionSellPrice,
                tfStockQuantity,
                tfExpiryQuantity);
        setProductCategoryChooser(tfCategory, this::handleSelectedProductCategory, tfUnit.getParent());
        setUnitChooser(tfUnit, this::handleSelectedUnit, cbStatus);
        setDrugCategoryChooser(tfDrugCategory, this::handleSelectedDrugCategory, tfIndication);
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(ProductStatus.ACTIVE.name(), translate(CommonLabel.LBL_ACTIVE.toString())),
                new SimpleComboBoxModel(ProductStatus.INACTIVE.name(), translate(CommonLabel.LBL_INACTIVE.toString())));
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
        // Nothing to init
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
        productAdd.setStatus(ProductStatus.valueOf(status.getValue()));
        productAdd.setDrugCategory(selectedDrugCategory);
        productAdd.setIndication(tfIndication.getText());
        productAdd.setContraindication(tfContraindication.getText());
        productAdd.setGeneralSellingPrice(toBigDecimalOrNull(tfGeneralSellPrice.getText()));
        productAdd.setPrescriptionSellingPrice(toBigDecimalOrNull(tfPrescriptionSellPrice.getText()));
        productAdd.setPriceRemarks(tfPriceRemarks.getText());
        productAdd.setStockQuantity(toIntegerOrNull(tfStockQuantity.getText()));
        productAdd.setStockRemarks(tfStockRemarks.getText());
        String expiredDate = tfExpiredDate.getTextMasked();
        productAdd
                .setExpiredDate(DateTimeUtils.parseLocalDateQuietly(expiredDate, CommonConstants.DATE_DISPLAY_PATTERN));
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
        validator.validateCustom(this::isDrugCategoryRequired, MessageCode.ERROR_EMPTY_DRUG_CATEGORY);
        validator.validateCustom(this::isInvalidExpiredDate, MessageCode.ERROR_INVALID_DATE_FORMAT);
        validator.validateCustom(this::isExpiryQuantityRequired, MessageCode.ERROR_INCORRECT_PRODUCT_EXPIRY_QUANTITY);
        validator.validateCustom(
                this::isExpiryQuantityExceedStockQuantity,
                MessageCode.ERROR_INCORRECT_PRODUCT_EXPIRY_QUANTITY);
    }

    private boolean isDrugCategoryRequired() {
        return isProductCategoryDrugSelected() && selectedDrugCategory == null;
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
        Integer stockQty = toIntegerOrZero(tfStockQuantity.getText());
        return expiryQty.compareTo(stockQty) > 0;
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
    }

    private void resetControls() {
        TextFieldUtils.setTextEmpty(
                tfName,
                tfCode,
                tfBarcode,
                tfDescription,
                tfCategory,
                tfUnit,
                tfDrugCategory,
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
        tfExpiredDate.setPlainText("");
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
            TextFieldUtils.setTextEmpty(tfDrugCategory, tfIndication, tfContraindication, tfPrescriptionSellPrice);
        }, () -> {
            selectedProductCategory = null;
            selectedDrugCategory = null;
            vboxMedicine.setDisable(true);
            vboxPresciptionSellPrice.setDisable(true);
            TextFieldUtils.setTextEmpty(
                    tfCategory,
                    tfDrugCategory,
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

    public void handleSelectedDrugCategory(ChooseResultVM<DrugCategoryVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(drugCategory -> {
            selectedDrugCategory = drugCategory;
            tfDrugCategory.setText(drugCategory.getName());
        }, () -> {
            selectedDrugCategory = null;
            tfDrugCategory.setText("");
        });
    }

    private void initBtnSaveAndAdd() {
        btnSaveAndCopy = new MenuItem(translate(CommonLabel.BTN_SAVE_AND_COPY));
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

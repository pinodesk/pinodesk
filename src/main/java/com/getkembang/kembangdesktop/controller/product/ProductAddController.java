package com.getkembang.kembangdesktop.controller.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.CommonDataSaveController;
import com.getkembang.kembangdesktop.javafx.converter.DrugCategoryComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.converter.ProductCategoryComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.converter.RackComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.converter.UnitComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.listener.DrugCategoryComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.javafx.listener.ProductCategoryComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.javafx.listener.RackComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.javafx.listener.UnitComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.service.ConfigurationService;
import com.getkembang.kembangdesktop.service.ProductService;
import com.getkembang.kembangdesktop.viewmodel.DrugCategoryVM;
import com.getkembang.kembangdesktop.viewmodel.DrugVM;
import com.getkembang.kembangdesktop.viewmodel.ProductAddVM;
import com.getkembang.kembangdesktop.viewmodel.ProductCategoryVM;
import com.getkembang.kembangdesktop.viewmodel.RackVM;
import com.getkembang.kembangdesktop.viewmodel.UnitVM;
import com.getkembang.kembangdesktop.viewmodel.WholesaleVM;
import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;

public class ProductAddController extends CommonDataSaveController {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfBarcode;

    @FXML
    private TextField tfDescription;

    @FXML
    private ComboBox<ProductCategoryVM> cbCategory;

    @FXML
    private TextField tfQuantity;

    @FXML
    private ComboBox<UnitVM> cbUnit;

    @FXML
    private TextField tfPurchasePrice;

    @FXML
    private TextField tfSellingPrice;

    @FXML
    private CheckBox chkIncludesVat;

    @FXML
    private TextField tfSellingPriceBeforeTax;

    @FXML
    private TextField tfVat;

    @FXML
    private TextField tfProfit;

    @FXML
    private MaskedTextField tfExpiredDate;

    @FXML
    private ComboBox<RackVM> cbRack;

    @FXML
    private ComboBox<DrugCategoryVM> cbDrugCategory;

    @FXML
    private TextField tfPrescriptionPrice;

    @FXML
    private TextField tfIndication;

    @FXML
    private TextField tfContraindication;

    @FXML
    private TextField tfPurchaseQuantity1;

    @FXML
    private TextField tfPurchaseQuantity2;

    @FXML
    private TextField tfPurchaseQuantity3;

    @FXML
    private TextField tfSellingPrice1;

    @FXML
    private TextField tfSellingPrice2;

    @FXML
    private TextField tfSellingPrice3;

    @FXML
    private TextField tfVat1;

    @FXML
    private TextField tfVat2;

    @FXML
    private TextField tfVat3;

    @FXML
    private TextField tfProfit1;

    @FXML
    private TextField tfProfit2;

    @FXML
    private TextField tfProfit3;

    @FXML
    private TextField tfSellingPriceBeforeTax1;

    @FXML
    private TextField tfSellingPriceBeforeTax2;

    @FXML
    private TextField tfSellingPriceBeforeTax3;

    @FXML
    private SplitMenuButton btnSaveAndAdd;

    private MenuItem btnSaveAndCopy;

    private BigDecimal vatPercentage;

    private ProductService productService;

    private ConfigurationService configurationService;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_PRODUCT);
            resetControls();
            tfName.requestFocus();
        }
    }

    @Override
    protected void registerValidator(ValidationSupport vs) {
        registerBlankValidator(tfName);
        registerBlankValidator(tfCode);
        registerBlankValidator(tfPurchasePrice);
        registerBlankValidator(tfSellingPrice);
        registerEmptyValidator(cbCategory);
        registerEmptyValidator(cbUnit);
        vs.registerValidator(cbDrugCategory, false, (c, v) -> {
            // @formatter:off
            boolean condition = v == null && !StringUtils.isAllBlank(
                    tfPrescriptionPrice.getText(),
                    tfIndication.getText(), 
                    tfContraindication.getText());
            // @formatter:on
            return ValidationResult.fromErrorIf(c, translate(MessageCode.ERROR_EMPTY_OR_BLANK), condition);
        });
        vs.registerValidator(cbDrugCategory, false, (c, v) -> {
            boolean condition = v != null && !isProductCategoryDrugs();
            return ValidationResult.fromErrorIf(c, translate(MessageCode.ERROR_INCORRECT_PRODUCT_CATEGORY_DRUGS),
                    condition);
        });
    }

    @Override
    protected Object save() {
        ProductAddVM productAdd = new ProductAddVM();
        productAdd.setName(tfName.getText());
        productAdd.setCode(tfCode.getText());
        productAdd.setBarcode(tfBarcode.getText());
        productAdd.setDescription(tfDescription.getText());
        productAdd.setQuantity(NumberUtils.toInt(tfQuantity.getText()));
        productAdd.setPurchasePrice(NumberUtils.toScaledBigDecimal(tfPurchasePrice.getText()));
        productAdd.setSellingPrice(NumberUtils.toScaledBigDecimal(tfSellingPrice.getText()));
        productAdd.setVatIncluded(chkIncludesVat.isSelected() ? CommonConstants.YES : CommonConstants.NO);
        productAdd.setUnit(cbUnit.getSelectionModel().getSelectedItem());
        productAdd.setProductCategory(cbCategory.getSelectionModel().getSelectedItem());
        String expiredDate = tfExpiredDate.getTextMasked();
        productAdd.setExpiredDate(parseDateQuietly(expiredDate, CommonConstants.DATE_PATTERN));
        productAdd.setRack(cbRack.getSelectionModel().getSelectedItem());
        if (ComboBoxUtils.hasItemSelected(cbDrugCategory)) {
            DrugCategoryVM drugCategory = ComboBoxUtils.getSelectedItem(cbDrugCategory);
            DrugVM drug = new DrugVM();
            drug.setDrugCategoryId(drugCategory.getId());
            drug.setDrugCategoryCode(drugCategory.getCode());
            drug.setDrugCategoryName(drugCategory.getName());
            drug.setPrescriptionPrice(NumberUtils.toScaledBigDecimal(tfPrescriptionPrice.getText()));
            drug.setIndication(tfIndication.getText());
            drug.setContraindication(tfIndication.getText());
            productAdd.setDrug(drug);
        }
        productAdd.setWholesales(loadWholesales());
        return productService.createProduct(productAdd) > 0;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = ctx.getBean(ProductService.class);
        configurationService = ctx.getBean(ConfigurationService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        tfPurchasePrice.setOnKeyTyped(event -> {
            calculateTaxAndProfit();
            calculateWholesaleTaxAndProfit();
        });
        tfSellingPrice.setOnKeyTyped(event -> calculateTaxAndProfit());
        chkIncludesVat.selectedProperty().addListener((observable, oldValue, newValue) -> {
            calculateTaxAndProfit();
            calculateWholesaleTaxAndProfit();
        });
        ComboBoxUtils.initAutoComplete(cbCategory, new ProductCategoryComboBoxKeyEventHandler(cbCategory),
                new ProductCategoryComboBoxConverter(cbCategory));
        ComboBoxUtils.initAutoComplete(cbUnit, new UnitComboBoxKeyEventHandler(cbUnit),
                new UnitComboBoxConverter(cbUnit));
        ComboBoxUtils.initAutoComplete(cbRack, new RackComboBoxKeyEventHandler(cbRack),
                new RackComboBoxConverter(cbRack));
        ComboBoxUtils.initAutoComplete(cbDrugCategory, new DrugCategoryComboBoxKeyEventHandler(cbDrugCategory),
                new DrugCategoryComboBoxConverter(cbDrugCategory));
        tfSellingPrice1.setOnKeyTyped(event -> calculateWholesaleTaxAndProfit());
        tfSellingPrice2.setOnKeyTyped(event -> calculateWholesaleTaxAndProfit());
        tfSellingPrice3.setOnKeyTyped(event -> calculateWholesaleTaxAndProfit());
        // @formatter:off
        TextFieldUtils.setDigitTextFields(
                tfBarcode,
                tfSellingPrice, 
                tfPurchasePrice, 
                tfQuantity, 
                tfPrescriptionPrice, 
                tfPurchaseQuantity1,
                tfPurchaseQuantity2, 
                tfPurchaseQuantity3, 
                tfSellingPrice1, 
                tfSellingPrice2,
                tfSellingPrice3);
        // @formatter:on
        initBtnSaveAndAdd();
        disableOnValidationError(btnSaveAndAdd);
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
        String vatPercentageBase = configurationService.getConfiguration(ConfigurationConstants.VAT_PERCENTAGE);
        vatPercentage = NumberUtils.toScaledBigDecimal(vatPercentageBase).divide(new BigDecimal(100));
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_PRODUCT_ADD;
    }

    private void initBtnSaveAndAdd() {
        btnSaveAndCopy = new MenuItem(translate("btn.saveandcopy"));
        btnSaveAndCopy.setOnAction(event -> {
            processDataSave();
            if (isLastDataSaved()) {
                displayInfo(MessageCode.SUCCESS_ADD_PRODUCT);
                cbUnit.getSelectionModel().clearSelection();
                cbUnit.requestFocus();
            }
        });
        btnSaveAndAdd.getItems().addAll(btnSaveAndCopy);
    }

    // @formatter:off
    private void calculate(
            TextField _tfSellingPrice, 
            TextField _tfVat, 
            TextField _tfSellingPriceBeforeTax,
            TextField _tfProfit) {
    // @formatter:on

        boolean includesVat = chkIncludesVat.isSelected();
        double sellingPrice = NumberUtils.toDouble(StringUtils.defaultIfBlank(_tfSellingPrice.getText(), null));
        double purchasePrice = NumberUtils.toDouble(StringUtils.defaultIfBlank(tfPurchasePrice.getText(), null));
        double vatAmount = includesVat ? sellingPrice * vatPercentage.doubleValue() : 0;
        double sellingPriceBeforeTax = includesVat ? sellingPrice - vatAmount : sellingPrice;
        double profitAmount = includesVat ? sellingPriceBeforeTax - purchasePrice : sellingPrice - purchasePrice;
        double profitPercentage = purchasePrice == 0 ? profitAmount * 100 : profitAmount / purchasePrice * 100;
        _tfVat.setText(BigDecimal.valueOf(vatAmount).setScale(0, RoundingMode.HALF_EVEN).toString());
        _tfSellingPriceBeforeTax
                .setText(BigDecimal.valueOf(sellingPriceBeforeTax).setScale(0, RoundingMode.HALF_EVEN).toString());
        _tfProfit.setText(BigDecimal.valueOf(profitAmount).setScale(0, RoundingMode.HALF_EVEN).toString() + " ("
                + BigDecimal.valueOf(profitPercentage).setScale(2, RoundingMode.HALF_EVEN).toString() + "%)");
    }

    private void calculateTaxAndProfit() {
        calculate(tfSellingPrice, tfVat, tfSellingPriceBeforeTax, tfProfit);
    }

    private void calculateWholesaleTaxAndProfit() {
        if (StringUtils.isNoneBlank(tfPurchaseQuantity1.getText(), tfSellingPrice1.getText())) {
            calculate(tfSellingPrice1, tfVat1, tfSellingPriceBeforeTax1, tfProfit1);
        }
        if (StringUtils.isNoneBlank(tfPurchaseQuantity2.getText(), tfSellingPrice2.getText())) {
            calculate(tfSellingPrice2, tfVat2, tfSellingPriceBeforeTax2, tfProfit2);
        }
        if (StringUtils.isNoneBlank(tfPurchaseQuantity3.getText(), tfSellingPrice3.getText())) {
            calculate(tfSellingPrice3, tfVat3, tfSellingPriceBeforeTax3, tfProfit3);
        }
    }

    private boolean isProductCategoryDrugs() {
        ProductCategoryVM category = ComboBoxUtils.getSelectedItem(cbCategory);
        return category != null && category.getCode().equals(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
    }

    private List<WholesaleVM> loadWholesales() {
        List<WholesaleVM> wholesales = new ArrayList<>();
        if (StringUtils.isNoneBlank(tfPurchaseQuantity1.getText(), tfSellingPrice1.getText())) {
            WholesaleVM wholesale = new WholesaleVM();
            wholesale.setPurchaseQuantity(NumberUtils.toInt(tfPurchaseQuantity1.getText()));
            wholesale.setSellingPrice(NumberUtils.toScaledBigDecimal(tfSellingPrice1.getText()));
            wholesales.add(wholesale);
        }
        if (StringUtils.isNoneBlank(tfPurchaseQuantity2.getText(), tfSellingPrice2.getText())) {
            WholesaleVM wholesale = new WholesaleVM();
            wholesale.setPurchaseQuantity(NumberUtils.toInt(tfPurchaseQuantity2.getText()));
            wholesale.setSellingPrice(NumberUtils.toScaledBigDecimal(tfSellingPrice2.getText()));
            wholesales.add(wholesale);
        }
        if (StringUtils.isNoneBlank(tfPurchaseQuantity3.getText(), tfSellingPrice3.getText())) {
            WholesaleVM wholesale = new WholesaleVM();
            wholesale.setPurchaseQuantity(NumberUtils.toInt(tfPurchaseQuantity3.getText()));
            wholesale.setSellingPrice(NumberUtils.toScaledBigDecimal(tfSellingPrice3.getText()));
            wholesales.add(wholesale);
        }
        return wholesales;
    }

    private void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfBarcode.setText(null);
        tfDescription.setText(null);
        tfQuantity.setText(null);
        tfPurchasePrice.setText(null);
        tfSellingPrice.setText(null);
        chkIncludesVat.setSelected(false);
        tfExpiredDate.setPlainText(null);
        cbCategory.getSelectionModel().clearSelection();
        cbUnit.getSelectionModel().clearSelection();
        cbRack.getSelectionModel().clearSelection();
        cbDrugCategory.getSelectionModel().clearSelection();
        tfPrescriptionPrice.setText(null);
        tfIndication.setText(null);
        tfContraindication.setText(null);
        tfPurchaseQuantity1.setText(null);
        tfPurchaseQuantity2.setText(null);
        tfPurchaseQuantity3.setText(null);
        tfSellingPrice1.setText(null);
        tfSellingPrice2.setText(null);
        tfSellingPrice3.setText(null);
    }

}

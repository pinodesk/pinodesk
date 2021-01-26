package com.getkembang.kembangdesktop.controller.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.BaseController;
import com.getkembang.kembangdesktop.javafx.control.MaskedTextField;
import com.getkembang.kembangdesktop.javafx.converter.DrugCategoryComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.converter.ProductCategoryComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.converter.RackComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.converter.UnitComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.formatter.DigitFormatter;
import com.getkembang.kembangdesktop.javafx.listener.DrugCategoryComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.javafx.listener.ProductCategoryComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.javafx.listener.RackComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.javafx.listener.UnitComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.service.ConfigurationService;
import com.getkembang.kembangdesktop.service.DrugCategoryService;
import com.getkembang.kembangdesktop.service.DrugService;
import com.getkembang.kembangdesktop.service.ProductCategoryService;
import com.getkembang.kembangdesktop.service.ProductService;
import com.getkembang.kembangdesktop.service.RackService;
import com.getkembang.kembangdesktop.service.UnitService;
import com.getkembang.kembangdesktop.service.WholesaleService;
import com.getkembang.kembangdesktop.utility.ComboBoxUtils;
import com.getkembang.kembangdesktop.viewmodel.AlertResult;
import com.getkembang.kembangdesktop.viewmodel.DrugCategoryVM;
import com.getkembang.kembangdesktop.viewmodel.DrugVM;
import com.getkembang.kembangdesktop.viewmodel.ProductCategoryVM;
import com.getkembang.kembangdesktop.viewmodel.ProductEditVM;
import com.getkembang.kembangdesktop.viewmodel.ProductVM;
import com.getkembang.kembangdesktop.viewmodel.RackVM;
import com.getkembang.kembangdesktop.viewmodel.UnitVM;
import com.getkembang.kembangdesktop.viewmodel.ValidationResult;
import com.getkembang.kembangdesktop.viewmodel.WholesaleVM;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductEditController extends BaseController {

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
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private VBox contentPane;

    private ProductVM currentProduct;

    private BigDecimal vatPercentage;

    private ProductService productService;

    private ProductCategoryService productCategoryService;

    private RackService rackService;

    private UnitService unitService;

    private DrugService drugService;

    private DrugCategoryService drugCategoryService;

    private WholesaleService wholesaleService;

    private ConfigurationService configurationService;

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = ctx.getBean(ProductService.class);
        productCategoryService = ctx.getBean(ProductCategoryService.class);
        rackService = ctx.getBean(RackService.class);
        unitService = ctx.getBean(UnitService.class);
        drugService = ctx.getBean(DrugService.class);
        drugCategoryService = ctx.getBean(DrugCategoryService.class);
        wholesaleService = ctx.getBean(WholesaleService.class);
        configurationService = ctx.getBean(ConfigurationService.class);
    }

    @Override
    protected void initControlsActions() {
        initDigitTextFields();
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
        contentPane.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                save();
            }
        });
    }

    @Override
    protected void initControlsValues() {
        String vatPercentageBase = configurationService.getConfiguration(ConfigurationConstants.VAT_PERCENTAGE);
        vatPercentage = NumberUtils.toScaledBigDecimal(vatPercentageBase).divide(new BigDecimal(100));
        currentProduct = getPageData();
        tfName.setText(currentProduct.getName());
        tfCode.setText(currentProduct.getCode());
        tfBarcode.setText(currentProduct.getBarcode());
        tfDescription.setText(currentProduct.getDescription());
        tfQuantity.setText(currentProduct.getQuantity().toString());
        tfPurchasePrice.setText(currentProduct.getPurchasePrice().setScale(0).toString());
        tfSellingPrice.setText(currentProduct.getSellingPrice().setScale(0).toString());
        chkIncludesVat.setText(
                chkIncludesVat.getText() + " (" + vatPercentage.multiply(new BigDecimal(100)).setScale(0) + "%)");
        chkIncludesVat.setSelected(CommonConstants.YES.equals(currentProduct.getVatIncluded()));
        Date expiredDate = currentProduct.getExpiredDate();
        tfExpiredDate.setPlainText(
                expiredDate == null ? null : DateFormatUtils.format(expiredDate, CommonConstants.DATE_PATTERN));
        ComboBoxUtils.select(cbCategory,
                () -> productCategoryService.getProductCategoryById(currentProduct.getCategoryId()));
        ComboBoxUtils.select(cbUnit, () -> unitService.getUnitById(currentProduct.getUnitId()));
        ComboBoxUtils.select(cbRack, () -> {
            Long rackId = currentProduct.getRackId();
            return rackId == null ? null : rackService.getRackById(rackId);
        });
        initDrugControlsValues();
        initWholesaleControlsValues();
        calculateTaxAndProfit();
        calculateWholesaleTaxAndProfit();
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_PRODUCT_EDIT;
    }

    @Override
    protected Stage getCurrentStage() {
        return (Stage) contentPane.getScene().getWindow();
    }

    // @formatter:off
    private void initDigitTextFields() {
        Arrays.asList(
                tfSellingPrice, 
                tfPurchasePrice, 
                tfQuantity, 
                tfPrescriptionPrice, 
                tfPurchaseQuantity1,
                tfPurchaseQuantity2, 
                tfPurchaseQuantity3, 
                tfSellingPrice1, 
                tfSellingPrice2,
                tfSellingPrice3).forEach(tf -> tf.setTextFormatter(new DigitFormatter()));
    }
    // @formatter:on

    private void initDrugControlsValues() {
        DrugVM drug = drugService.getDrugByProductId(currentProduct.getId());
        if (drug != null) {
            DrugCategoryVM selectedDrugCategory = drugCategoryService.getDrugCategoryById(drug.getDrugCategoryId());
            cbDrugCategory.getItems().add(selectedDrugCategory);
            cbDrugCategory.getSelectionModel().select(selectedDrugCategory);
            tfPrescriptionPrice.setText(drug.getPrescriptionPrice().setScale(0).toString());
            tfIndication.setText(drug.getIndication());
            tfContraindication.setText(drug.getContraindication());
        }
    }

    private void initWholesaleControlsValues() {
        List<WholesaleVM> wholesales = wholesaleService.getWholesalesByProductId(currentProduct.getId());
        for (int i = 0; i < wholesales.size(); i++) {
            WholesaleVM wholesale = wholesales.get(i);
            Integer purchaseQuantity = wholesale.getPurchaseQuantity();
            BigDecimal sellingPrice = wholesale.getSellingPrice();
            switch (i) {
                case 0:
                    tfPurchaseQuantity1.setText(purchaseQuantity.toString());
                    tfSellingPrice1.setText(sellingPrice.setScale(0).toString());
                    break;
                case 1:
                    tfPurchaseQuantity2.setText(purchaseQuantity.toString());
                    tfSellingPrice2.setText(sellingPrice.setScale(0).toString());
                    break;
                case 2:
                    tfPurchaseQuantity3.setText(purchaseQuantity.toString());
                    tfSellingPrice3.setText(sellingPrice.setScale(0).toString());
                    break;
                default:
                    // Do nothing
            }
        }
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

    private ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        if (StringUtils.isBlank(tfName.getText())) {
            result.setError(MessageCode.ERROR_EMPTY_NAME);
        }
        if (StringUtils.isBlank(tfCode.getText())) {
            result.setError(MessageCode.ERROR_EMPTY_CODE);
        }
        if (StringUtils.isBlank(tfPurchasePrice.getText())) {
            result.setError(MessageCode.ERROR_EMPTY_PURCHASE_PRICE);
        }
        if (StringUtils.isBlank(tfSellingPrice.getText())) {
            result.setError(MessageCode.ERROR_EMPTY_SELLING_PRICE);
        }
        if (!ComboBoxUtils.hasItemSelected(cbCategory)) {
            result.setError(MessageCode.ERROR_EMPTY_CATEGORY);
        }
        if (!ComboBoxUtils.hasItemSelected(cbUnit)) {
            result.setError(MessageCode.ERROR_EMPTY_UNIT);
        }
        // @formatter:off
        if (!ComboBoxUtils.hasItemSelected(cbDrugCategory) 
                && !StringUtils.isAllBlank(
                        tfPrescriptionPrice.getText(),
                        tfIndication.getText(), 
                        tfContraindication.getText())) {
        // @formatter:on
            result.setError(MessageCode.ERROR_EMPTY_DRUG_CATEGORY);
        }
        if (ComboBoxUtils.hasItemSelected(cbDrugCategory) && !isProductCategoryDrugs()) {
            result.setError(MessageCode.ERROR_INCORRECT_PRODUCT_CATEGORY_DRUGS);
        }
        return result;
    }

    private boolean isProductCategoryDrugs() {
        return ComboBoxUtils.getSelectedItem(cbCategory).getCode().equals(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
    }

    private boolean save() {
        ValidationResult result = validate();
        if (result.isError()) {
            displayError(result.getMessageCode());
            return false;
        }
        ProductEditVM productEdit = new ProductEditVM();
        productEdit.setId(currentProduct.getId());
        productEdit.setName(tfName.getText());
        productEdit.setCode(tfCode.getText());
        productEdit.setBarcode(tfBarcode.getText());
        productEdit.setDescription(tfDescription.getText());
        productEdit.setQuantity(NumberUtils.toInt(tfQuantity.getText()));
        productEdit.setPurchasePrice(NumberUtils.toScaledBigDecimal(tfPurchasePrice.getText()));
        productEdit.setSellingPrice(NumberUtils.toScaledBigDecimal(tfSellingPrice.getText()));
        productEdit.setVatIncluded(chkIncludesVat.isSelected() ? CommonConstants.YES : CommonConstants.NO);
        productEdit.setUnit(cbUnit.getSelectionModel().getSelectedItem());
        productEdit.setProductCategory(cbCategory.getSelectionModel().getSelectedItem());
        String expiredDate = tfExpiredDate.getTextMasked();
        productEdit.setExpiredDate(parseDateQuietly(expiredDate, CommonConstants.DATE_PATTERN));
        productEdit.setRack(cbRack.getSelectionModel().getSelectedItem());
        if (ComboBoxUtils.hasItemSelected(cbDrugCategory)) {
            DrugCategoryVM drugCategory = ComboBoxUtils.getSelectedItem(cbDrugCategory);
            DrugVM drug = new DrugVM();
            drug.setProductId(currentProduct.getId());
            drug.setDrugCategoryId(drugCategory.getId());
            drug.setDrugCategoryCode(drugCategory.getCode());
            drug.setDrugCategoryName(drugCategory.getName());
            drug.setPrescriptionPrice(NumberUtils.toScaledBigDecimal(tfPrescriptionPrice.getText()));
            drug.setIndication(tfIndication.getText());
            drug.setContraindication(tfIndication.getText());
            productEdit.setDrug(drug);
        }
        productEdit.setWholesales(loadWholesales());
        return productService.updateProduct(productEdit);
       
    }

    private List<WholesaleVM> loadWholesales() {
        List<WholesaleVM> wholesales = new ArrayList<>();
        if (StringUtils.isNoneBlank(tfPurchaseQuantity1.getText(), tfSellingPrice1.getText())) {
            WholesaleVM wholesale = new WholesaleVM();
            wholesale.setProductId(currentProduct.getId());
            wholesale.setPurchaseQuantity(NumberUtils.toInt(tfPurchaseQuantity1.getText()));
            wholesale.setSellingPrice(NumberUtils.toScaledBigDecimal(tfSellingPrice1.getText()));
            wholesales.add(wholesale);
        }
        if (StringUtils.isNoneBlank(tfPurchaseQuantity2.getText(), tfSellingPrice2.getText())) {
            WholesaleVM wholesale = new WholesaleVM();
            wholesale.setProductId(currentProduct.getId());
            wholesale.setPurchaseQuantity(NumberUtils.toInt(tfPurchaseQuantity2.getText()));
            wholesale.setSellingPrice(NumberUtils.toScaledBigDecimal(tfSellingPrice2.getText()));
            wholesales.add(wholesale);
        }
        if (StringUtils.isNoneBlank(tfPurchaseQuantity3.getText(), tfSellingPrice3.getText())) {
            WholesaleVM wholesale = new WholesaleVM();
            wholesale.setProductId(currentProduct.getId());
            wholesale.setPurchaseQuantity(NumberUtils.toInt(tfPurchaseQuantity3.getText()));
            wholesale.setSellingPrice(NumberUtils.toScaledBigDecimal(tfSellingPrice3.getText()));
            wholesales.add(wholesale);
        }
        return wholesales;
    }

    @FXML
    void onActionBtnSave(ActionEvent event) {
        boolean updated = save();
        if (updated) {
            displayInfo(MessageCode.SUCCESS_EDIT_PRODUCT);
            setPrevPageData(Boolean.TRUE);
            close();
        }
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        close();
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_PRODUCT);
        if (result.isConfirmed()) {
            productService.removeProducts(Arrays.asList(currentProduct.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_PRODUCT);
            setPrevPageData(Boolean.TRUE);
            close();
        }
    }

}

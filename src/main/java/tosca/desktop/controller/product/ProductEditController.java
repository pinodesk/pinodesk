package tosca.desktop.controller.product;

import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toScaledBigDecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tosca.desktop.constant.CommonConstants;
import tosca.desktop.constant.ConfigurationConstants;
import tosca.desktop.constant.MessageCode;
import tosca.desktop.controller.CommonDataSaveController;
import tosca.desktop.javafx.converter.DrugCategoryComboBoxConverter;
import tosca.desktop.javafx.converter.ProductCategoryComboBoxConverter;
import tosca.desktop.javafx.converter.RackComboBoxConverter;
import tosca.desktop.javafx.converter.UnitComboBoxConverter;
import tosca.desktop.javafx.listener.DrugCategoryComboBoxKeyEventHandler;
import tosca.desktop.javafx.listener.ProductCategoryComboBoxKeyEventHandler;
import tosca.desktop.javafx.listener.RackComboBoxKeyEventHandler;
import tosca.desktop.javafx.listener.UnitComboBoxKeyEventHandler;
import tosca.desktop.service.ConfigurationService;
import tosca.desktop.service.DrugCategoryService;
import tosca.desktop.service.DrugService;
import tosca.desktop.service.ProductCategoryService;
import tosca.desktop.service.ProductService;
import tosca.desktop.service.RackService;
import tosca.desktop.service.UnitService;
import tosca.desktop.service.WholesaleService;
import tosca.desktop.viewmodel.DrugCategoryVM;
import tosca.desktop.viewmodel.DrugVM;
import tosca.desktop.viewmodel.ProductCategoryVM;
import tosca.desktop.viewmodel.ProductEditVM;
import tosca.desktop.viewmodel.ProductVM;
import tosca.desktop.viewmodel.RackVM;
import tosca.desktop.viewmodel.UnitVM;
import tosca.desktop.viewmodel.WholesaleVM;
import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
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
import javafx.scene.control.TextField;

public class ProductEditController extends CommonDataSaveController {

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
    }

    @Override
    protected void initDataSaveControlValues() {
        String vatPercentageBase = configurationService.getConfiguration(ConfigurationConstants.VAT_PERCENTAGE);
        vatPercentage = toScaledBigDecimal(vatPercentageBase).divide(new BigDecimal(100));
        currentProduct = getPageData();
        tfName.setText(currentProduct.getName());
        tfCode.setText(currentProduct.getCode());
        tfBarcode.setText(currentProduct.getBarcode());
        tfDescription.setText(currentProduct.getDescription());
        tfQuantity.setText(toStringOrNull(currentProduct.getQuantity()));
        tfPurchasePrice.setText(toStringOrNull(currentProduct.getPurchasePrice()));
        tfSellingPrice.setText(toStringOrNull(currentProduct.getSellingPrice()));
        chkIncludesVat.setText(
                chkIncludesVat.getText() + " (" + vatPercentage.multiply(new BigDecimal(100)).setScale(0) + "%)");
        chkIncludesVat.setSelected(CommonConstants.YES.equals(currentProduct.getVatIncluded()));
        LocalDate expiredDate = currentProduct.getExpiredDate();
        tfExpiredDate.setPlainText(
                expiredDate == null ? null : expiredDate.format(DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN)));
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

    private void initDrugControlsValues() {
        DrugVM drug = drugService.getDrugByProductId(currentProduct.getId());
        if (drug != null) {
            DrugCategoryVM selectedDrugCategory = drugCategoryService.getDrugCategoryById(drug.getDrugCategoryId());
            cbDrugCategory.getItems().add(selectedDrugCategory);
            cbDrugCategory.getSelectionModel().select(selectedDrugCategory);
            tfPrescriptionPrice.setText(toStringOrNull(drug.getPrescriptionPrice()));
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

    @Override
    protected void registerValidator(ValidationSupport vs) {
        registerRequiredFields(tfName, tfCode, tfPurchasePrice, tfSellingPrice, cbCategory, cbUnit);
        registerWhitespaceValidator(tfName);
        registerWhitespaceValidator(tfCode);
        registerWhitespaceValidator(tfPurchasePrice);
        registerWhitespaceValidator(tfSellingPrice);
        vs.registerValidator(cbDrugCategory, false, (c, v) -> {
            // @formatter:off
            boolean condition1 = v == null && !StringUtils.isAllBlank(
                    tfPrescriptionPrice.getText(),
                    tfIndication.getText(), 
                    tfContraindication.getText());
            // @formatter:on
            boolean condition2 = v != null && !isProductCategoryDrugs();
            return ValidationResult.fromErrorIf(c, translate(
                    condition1 ? MessageCode.ERROR_EMPTY_OR_BLANK : MessageCode.ERROR_INCORRECT_PRODUCT_CATEGORY_DRUGS),
                    condition1 || condition2);
        });
        revalidateOnChange(vs);
    }

    private void revalidateOnChange(ValidationSupport vs) {
        ComboBoxUtils.onSelectedItemChanged(cbCategory, (ov, nv) -> vs.revalidate(cbDrugCategory));
        TextFieldUtils.onTextChanged(tfPrescriptionPrice, (ov, nv) -> vs.revalidate(cbDrugCategory));
        TextFieldUtils.onTextChanged(tfIndication, (ov, nv) -> vs.revalidate(cbDrugCategory));
        TextFieldUtils.onTextChanged(tfContraindication, (ov, nv) -> vs.revalidate(cbDrugCategory));
    }

    private boolean isProductCategoryDrugs() {
        ProductCategoryVM category = ComboBoxUtils.getSelectedItem(cbCategory);
        return category != null && category.getCode().equals(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
    }

    @Override
    protected Object save() {
        ProductEditVM productEdit = new ProductEditVM();
        productEdit.setId(currentProduct.getId());
        productEdit.setName(tfName.getText());
        productEdit.setCode(tfCode.getText());
        productEdit.setBarcode(tfBarcode.getText());
        productEdit.setDescription(tfDescription.getText());
        productEdit.setQuantity(tfQuantity.getText() == null ? null : toInt(tfQuantity.getText()));
        productEdit.setPurchasePrice(toScaledBigDecimal(tfPurchasePrice.getText()));
        productEdit.setSellingPrice(toScaledBigDecimal(tfSellingPrice.getText()));
        productEdit.setVatIncluded(chkIncludesVat.isSelected() ? CommonConstants.YES : CommonConstants.NO);
        productEdit.setUnit(cbUnit.getSelectionModel().getSelectedItem());
        productEdit.setProductCategory(cbCategory.getSelectionModel().getSelectedItem());
        String expiredDate = tfExpiredDate.getTextMasked();
        productEdit.setExpiredDate(parseLocalDateQuietly(expiredDate, CommonConstants.DATE_DISPLAY_PATTERN));
        productEdit.setRack(cbRack.getSelectionModel().getSelectedItem());
        if (ComboBoxUtils.hasItemSelected(cbDrugCategory)) {
            DrugCategoryVM drugCategory = ComboBoxUtils.getSelectedItem(cbDrugCategory);
            String strPrescriptionPrice = tfPrescriptionPrice.getText();
            DrugVM drug = new DrugVM();
            drug.setProductId(currentProduct.getId());
            drug.setDrugCategoryId(drugCategory.getId());
            drug.setDrugCategoryCode(drugCategory.getCode());
            drug.setDrugCategoryName(drugCategory.getName());
            drug.setPrescriptionPrice(StringUtils.isBlank(strPrescriptionPrice) ? null
                    : toScaledBigDecimal(tfPrescriptionPrice.getText()));
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
            wholesale.setPurchaseQuantity(toInt(tfPurchaseQuantity1.getText()));
            wholesale.setSellingPrice(toScaledBigDecimal(tfSellingPrice1.getText()));
            wholesales.add(wholesale);
        }
        if (StringUtils.isNoneBlank(tfPurchaseQuantity2.getText(), tfSellingPrice2.getText())) {
            WholesaleVM wholesale = new WholesaleVM();
            wholesale.setProductId(currentProduct.getId());
            wholesale.setPurchaseQuantity(toInt(tfPurchaseQuantity2.getText()));
            wholesale.setSellingPrice(toScaledBigDecimal(tfSellingPrice2.getText()));
            wholesales.add(wholesale);
        }
        if (StringUtils.isNoneBlank(tfPurchaseQuantity3.getText(), tfSellingPrice3.getText())) {
            WholesaleVM wholesale = new WholesaleVM();
            wholesale.setProductId(currentProduct.getId());
            wholesale.setPurchaseQuantity(toInt(tfPurchaseQuantity3.getText()));
            wholesale.setSellingPrice(toScaledBigDecimal(tfSellingPrice3.getText()));
            wholesales.add(wholesale);
        }
        return wholesales;
    }

}

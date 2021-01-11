package com.gitlab.muhammadkholidb.bianglala.controller.product;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.Page;
import com.gitlab.muhammadkholidb.bianglala.controller.BaseController;
import com.gitlab.muhammadkholidb.bianglala.javafx.control.MaskedTextField;
import com.gitlab.muhammadkholidb.bianglala.javafx.converter.DrugCategoryComboBoxConverter;
import com.gitlab.muhammadkholidb.bianglala.javafx.converter.ProductCategoryComboBoxConverter;
import com.gitlab.muhammadkholidb.bianglala.javafx.converter.RackComboBoxConverter;
import com.gitlab.muhammadkholidb.bianglala.javafx.converter.UnitComboBoxConverter;
import com.gitlab.muhammadkholidb.bianglala.javafx.formatter.DigitFormatter;
import com.gitlab.muhammadkholidb.bianglala.javafx.listener.DrugCategoryComboBoxKeyEventHandler;
import com.gitlab.muhammadkholidb.bianglala.javafx.listener.ProductCategoryComboBoxKeyEventHandler;
import com.gitlab.muhammadkholidb.bianglala.javafx.listener.RackComboBoxKeyEventHandler;
import com.gitlab.muhammadkholidb.bianglala.javafx.listener.UnitComboBoxKeyEventHandler;
import com.gitlab.muhammadkholidb.bianglala.service.DrugCategoryService;
import com.gitlab.muhammadkholidb.bianglala.service.DrugService;
import com.gitlab.muhammadkholidb.bianglala.service.ProductCategoryService;
import com.gitlab.muhammadkholidb.bianglala.service.ProductService;
import com.gitlab.muhammadkholidb.bianglala.service.RackService;
import com.gitlab.muhammadkholidb.bianglala.service.UnitService;
import com.gitlab.muhammadkholidb.bianglala.service.WholesaleService;
import com.gitlab.muhammadkholidb.bianglala.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.bianglala.utility.FXUtils;
import com.gitlab.muhammadkholidb.bianglala.utility.PageData;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.DrugCategoryVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.DrugVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategoryVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductEditVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.RackVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.UnitVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.WholesaleVM;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductEditController extends BaseController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    private TextField tfProfitAmount;

    @FXML
    private TextField tfProfitPercentage;

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
    private TextField tfPurchaseQuantity4;

    @FXML
    private TextField tfSellingPrice1;

    @FXML
    private TextField tfSellingPrice2;

    @FXML
    private TextField tfSellingPrice3;

    @FXML
    private TextField tfSellingPrice4;

    @FXML
    private TextField tfProfitAmount1;

    @FXML
    private TextField tfProfitAmount2;

    @FXML
    private TextField tfProfitAmount3;

    @FXML
    private TextField tfProfitAmount4;

    @FXML
    private TextField tfProfitPercentage1;

    @FXML
    private TextField tfProfitPercentage2;

    @FXML
    private TextField tfProfitPercentage3;

    @FXML
    private TextField tfProfitPercentage4;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private VBox contentPane;

    private ProductVM currentProduct;

    private ProductService productService;

    private ProductCategoryService productCategoryService;

    private RackService rackService;

    private UnitService unitService;

    private DrugService drugService;

    private DrugCategoryService drugCategoryService;

    private WholesaleService wholesaleService;

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = ctx.getBean(ProductService.class);
        productCategoryService = ctx.getBean(ProductCategoryService.class);
        rackService = ctx.getBean(RackService.class);
        unitService = ctx.getBean(UnitService.class);
        drugService = ctx.getBean(DrugService.class);
        drugCategoryService = ctx.getBean(DrugCategoryService.class);
        wholesaleService = ctx.getBean(WholesaleService.class);
    }

    @Override
    protected void initControls() {
        currentProduct = PageData.INSTANCE.get(Page.MASTER_PRODUCT_MAIN, Page.MASTER_PRODUCT_EDIT);
        tfName.setText(currentProduct.getName());
        tfCode.setText(currentProduct.getCode());
        tfBarcode.setText(currentProduct.getBarcode());
        tfDescription.setText(currentProduct.getDescription());
        tfQuantity.setText(currentProduct.getQuantity().toString());
        tfPurchasePrice.setText(currentProduct.getPurchasePrice().toString());
        tfSellingPrice.setText(currentProduct.getSellingPrice().toString());
        ComboBoxUtils.initEditable(cbCategory, new ProductCategoryComboBoxKeyEventHandler(cbCategory),
                new ProductCategoryComboBoxConverter(cbCategory),
                () -> productCategoryService.getProductCategoryById(currentProduct.getCategoryId()));
        ComboBoxUtils.initEditable(cbUnit, new UnitComboBoxKeyEventHandler(cbUnit), new UnitComboBoxConverter(cbUnit),
                () -> unitService.getUnitById(currentProduct.getUnitId()));
        ComboBoxUtils.initEditable(cbRack, new RackComboBoxKeyEventHandler(cbRack), new RackComboBoxConverter(cbRack),
                () -> {
                    Long rackId = currentProduct.getRackId();
                    return rackId == null ? null : rackService.getRackById(rackId);
                });
        ComboBoxUtils.initEditable(cbDrugCategory, new DrugCategoryComboBoxKeyEventHandler(cbDrugCategory),
                new DrugCategoryComboBoxConverter(cbDrugCategory));
        DrugVM drug = drugService.getDrugByProductId(currentProduct.getId());
        if (drug != null) {
            DrugCategoryVM selectedDrugCategory = drugCategoryService.getDrugCategoryById(drug.getDrugCategoryId());
            cbDrugCategory.getSelectionModel().select(selectedDrugCategory);
            tfPrescriptionPrice.setText(drug.getPrescriptionPrice().toString());
            tfIndication.setText(drug.getIndication());
            tfContraindication.setText(drug.getContraindication());
        }
        List<WholesaleVM> wholesales = wholesaleService.getWholesalesByProductId(currentProduct.getId());
        for (int i = 0; i < wholesales.size(); i++) {
            WholesaleVM wholesale = wholesales.get(i);
            Integer purchaseQuantity = wholesale.getPurchaseQuantity();
            BigDecimal sellingPrice = wholesale.getSellingPrice();
            switch (i) {
                case 0:
                    tfPurchaseQuantity1.setText(purchaseQuantity.toString());
                    tfSellingPrice1.setText(sellingPrice.toString());
                    break;

                case 1:
                    tfPurchaseQuantity2.setText(purchaseQuantity.toString());
                    tfSellingPrice2.setText(sellingPrice.toString());
                    break;

                case 2:
                    tfPurchaseQuantity3.setText(purchaseQuantity.toString());
                    tfSellingPrice3.setText(sellingPrice.toString());
                    break;

                case 3:
                    tfPurchaseQuantity4.setText(purchaseQuantity.toString());
                    tfSellingPrice4.setText(sellingPrice.toString());
                    break;

                default:
                    // Do nothing
            }
        }
        initDigitTextFields();
    }

    private void initDigitTextFields() {
        Arrays.asList(tfSellingPrice, tfPurchasePrice, tfQuantity, tfPrescriptionPrice, tfPurchaseQuantity1,
                tfPurchaseQuantity2, tfPurchaseQuantity3, tfPurchaseQuantity4, tfSellingPrice1, tfSellingPrice2,
                tfSellingPrice3, tfSellingPrice4).forEach(tf -> tf.setTextFormatter(new DigitFormatter()));
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        close();
    }

    private String validate() {
        if (StringUtils.isEmpty(tfName.getText())) {
            return "Name cannot be empty";
        }
        if (StringUtils.isEmpty(tfCode.getText())) {
            return "Code cannot be empty";
        }
        if (cbCategory.getSelectionModel().isEmpty()) {
            return "Category cannot be empty";
        }
        if (cbUnit.getSelectionModel().isEmpty()) {
            return "Unit cannot be empty";
        }
        if (cbDrugCategory.getSelectionModel().isEmpty() && ObjectUtils.isEmpty(tfPrescriptionPrice)) {
            return "Drug category cannot be empty";
        }
        // if (StringUtils.isAnyBlank(tfPurchaseQuantity1.getText(), tfSellingPrice1.getText())
        //         || StringUtils.isAnyBlank(tfPurchaseQuantity2.getText(), tfSellingPrice2.getText())
        //         || StringUtils.isAnyBlank(tfPurchaseQuantity3.getText(), tfSellingPrice3.getText())
        //         || StringUtils.isAnyBlank(tfPurchaseQuantity4.getText(), tfSellingPrice4.getText())) {
        //     return "Both wholesale purchase quantity and selling price must be filled";
        // }
        return null;
    }

    @FXML
    void onActionBtnSave(ActionEvent event) {
        String err = validate();
        if (StringUtils.isNotBlank(err)) {
            FXUtils.showError(err);
            return;
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
        productEdit.setRack(cbRack.getSelectionModel().getSelectedItem());
        if (StringUtils.isNotBlank(tfPrescriptionPrice.getText())) {
            DrugCategoryVM drugCategory = cbDrugCategory.getSelectionModel().getSelectedItem();
            DrugVM drug = new DrugVM();
            drug.setDrugCategoryId(drugCategory.getId());
            drug.setDrugCategoryCode(drugCategory.getCode());
            drug.setDrugCategoryName(drugCategory.getName());
            drug.setPrescriptionPrice(NumberUtils.toScaledBigDecimal(tfPrescriptionPrice.getText()));
            drug.setIndication(tfIndication.getText());
            drug.setContraindication(tfIndication.getText());
            productEdit.setDrug(drug);
        }
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
        if (StringUtils.isNoneBlank(tfPurchaseQuantity4.getText(), tfSellingPrice4.getText())) {
            WholesaleVM wholesale = new WholesaleVM();
            wholesale.setProductId(currentProduct.getId());
            wholesale.setPurchaseQuantity(NumberUtils.toInt(tfPurchaseQuantity4.getText()));
            wholesale.setSellingPrice(NumberUtils.toScaledBigDecimal(tfSellingPrice4.getText()));
            wholesales.add(wholesale);
        }
        productEdit.setWholesales(wholesales);
        boolean updated = productService.updateProduct(productEdit);
        if (updated) {
            Optional<ButtonType> result = FXUtils.showInfo("Successfully update product");
            log.debug("Result: {}", result);
            // close();   
        }
    }

    private void close() {
        ((Stage) contentPane.getScene().getWindow()).close();
    }

}

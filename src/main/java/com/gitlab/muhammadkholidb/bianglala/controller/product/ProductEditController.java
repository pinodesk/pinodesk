package com.gitlab.muhammadkholidb.bianglala.controller.product;

import java.net.URL;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.Page;
import com.gitlab.muhammadkholidb.bianglala.control.MaskedTextField;
import com.gitlab.muhammadkholidb.bianglala.controller.BaseController;
import com.gitlab.muhammadkholidb.bianglala.converter.ProductCategoryComboBoxConverter;
import com.gitlab.muhammadkholidb.bianglala.converter.RackComboBoxConverter;
import com.gitlab.muhammadkholidb.bianglala.converter.UnitComboBoxConverter;
import com.gitlab.muhammadkholidb.bianglala.listener.ProductCategoryComboBoxKeyEventHandler;
import com.gitlab.muhammadkholidb.bianglala.listener.RackComboBoxKeyEventHandler;
import com.gitlab.muhammadkholidb.bianglala.listener.UnitComboBoxKeyEventHandler;
import com.gitlab.muhammadkholidb.bianglala.service.ProductCategoryService;
import com.gitlab.muhammadkholidb.bianglala.service.RackService;
import com.gitlab.muhammadkholidb.bianglala.service.UnitService;
import com.gitlab.muhammadkholidb.bianglala.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.bianglala.utility.PageData;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategoryVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.RackVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.UnitVM;

import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

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
    private ComboBox<?> cbDrugCategory;

    @FXML
    private TextField tfPrescriptionPrice;

    @FXML
    private TextField tfIndication;

    @FXML
    private TextField tfContraindication;

    @FXML
    private CheckBox chkWholesale;

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

    private ProductCategoryService productCategoryService;

    private RackService rackService;

    private UnitService unitService;

    @Override
    protected void initServices(ApplicationContext ctx) {
        productCategoryService = ctx.getBean(ProductCategoryService.class);
        rackService = ctx.getBean(RackService.class);
        unitService = ctx.getBean(UnitService.class);
    }

    @Override
    protected void initControls() {
        ProductVM result = PageData.INSTANCE.get(Page.MASTER_PRODUCT_MAIN, Page.MASTER_PRODUCT_EDIT);
        tfName.setText(result.getName());
        tfCode.setText(result.getCode());
        tfBarcode.setText(result.getBarcode());
        tfDescription.setText(result.getDescription());
        tfQuantity.setText(result.getQuantity().toString());
        tfPurchasePrice.setText(result.getPurchasePrice().toString());
        ComboBoxUtils.initEditable(cbCategory, new ProductCategoryComboBoxKeyEventHandler(cbCategory),
                new ProductCategoryComboBoxConverter(cbCategory),
                () -> productCategoryService.getProductCateoryById(result.getCategoryId()));
        ComboBoxUtils.initEditable(cbUnit, new UnitComboBoxKeyEventHandler(cbUnit), new UnitComboBoxConverter(cbUnit),
                () -> unitService.getUnitById(result.getUnitId()));
        ComboBoxUtils.initEditable(cbRack, new RackComboBoxKeyEventHandler(cbRack), new RackComboBoxConverter(cbRack),
                () -> {
                    Long rackId = result.getRackId();
                    return rackId == null ? null : rackService.getRackById(rackId);
                });
    }

}

package com.gitlab.muhammadkholidb.bianglala.controller.product;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
import com.gitlab.muhammadkholidb.bianglala.service.RackService;
import com.gitlab.muhammadkholidb.bianglala.service.UnitService;
import com.gitlab.muhammadkholidb.bianglala.service.WholesaleService;
import com.gitlab.muhammadkholidb.bianglala.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.bianglala.utility.PageData;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.DrugCategoryVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.DrugVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategoryVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.RackVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.UnitVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.WholesaleVM;

import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private VBox contentPane;

    private ProductCategoryService productCategoryService;

    private RackService rackService;

    private UnitService unitService;

    private DrugService drugService;

    private DrugCategoryService drugCategoryService;

    private WholesaleService wholesaleService;

    @Override
    protected void initServices(ApplicationContext ctx) {
        productCategoryService = ctx.getBean(ProductCategoryService.class);
        rackService = ctx.getBean(RackService.class);
        unitService = ctx.getBean(UnitService.class);
        drugService = ctx.getBean(DrugService.class);
        drugCategoryService = ctx.getBean(DrugCategoryService.class);
        wholesaleService = ctx.getBean(WholesaleService.class);
    }

    @Override
    protected void initControls() {
        ProductVM result = PageData.INSTANCE.get(Page.MASTER_PRODUCT_MAIN, Page.MASTER_PRODUCT_EDIT);
        tfName.setText(result.getName());
        tfCode.setText(result.getCode());
        tfBarcode.setText(result.getBarcode());
        tfDescription.setText(result.getDescription());
        tfQuantity.setTextFormatter(new DigitFormatter());
        tfQuantity.setText(result.getQuantity().toString());
        tfPurchasePrice.setTextFormatter(new DigitFormatter());
        tfPurchasePrice.setText(result.getPurchasePrice().toString());
        tfSellingPrice.setTextFormatter(new DigitFormatter());
        tfSellingPrice.setText(result.getSellingPrice().toString());
        ComboBoxUtils.initEditable(cbCategory, new ProductCategoryComboBoxKeyEventHandler(cbCategory),
                new ProductCategoryComboBoxConverter(cbCategory),
                () -> productCategoryService.getProductCategoryById(result.getCategoryId()));
        ComboBoxUtils.initEditable(cbUnit, new UnitComboBoxKeyEventHandler(cbUnit), new UnitComboBoxConverter(cbUnit),
                () -> unitService.getUnitById(result.getUnitId()));
        ComboBoxUtils.initEditable(cbRack, new RackComboBoxKeyEventHandler(cbRack), new RackComboBoxConverter(cbRack),
                () -> {
                    Long rackId = result.getRackId();
                    return rackId == null ? null : rackService.getRackById(rackId);
                });
        ComboBoxUtils.initEditable(cbDrugCategory, new DrugCategoryComboBoxKeyEventHandler(cbDrugCategory),
                new DrugCategoryComboBoxConverter(cbDrugCategory));
        DrugVM drug = drugService.getDrugByProductId(result.getId());
        if (drug != null) {
            DrugCategoryVM selectedDrugCategory = drugCategoryService.getDrugCategoryById(drug.getDrugCategoryId());
            cbDrugCategory.getSelectionModel().select(selectedDrugCategory);
            tfPrescriptionPrice.setText(drug.getPrescriptionPrice().toString());
            tfPrescriptionPrice.setTextFormatter(new DigitFormatter());
            tfIndication.setText(drug.getIndication());
            tfContraindication.setText(drug.getContraindication());
        }
        List<WholesaleVM> wholesales = wholesaleService.getWholesalesByProductId(result.getId());
        for (int i = 0; i < wholesales.size(); i++) {
            WholesaleVM wholesale = wholesales.get(i);
            Integer purchaseQuantity = wholesale.getPurchaseQuantity();
            BigDecimal sellingPrice = wholesale.getSellingPrice();
            switch (i) {
                case 1:
                    tfPurchaseQuantity1.setText(purchaseQuantity.toString());
                    tfPurchaseQuantity1.setTextFormatter(new DigitFormatter());
                    tfSellingPrice1.setText(sellingPrice.toString());
                    tfSellingPrice1.setTextFormatter(new DigitFormatter());
                    break;

                case 2:
                    tfPurchaseQuantity2.setText(purchaseQuantity.toString());
                    tfPurchaseQuantity2.setTextFormatter(new DigitFormatter());
                    tfSellingPrice2.setText(sellingPrice.toString());
                    tfSellingPrice2.setTextFormatter(new DigitFormatter());
                    break;

                case 3:
                    tfPurchaseQuantity3.setText(purchaseQuantity.toString());
                    tfPurchaseQuantity3.setTextFormatter(new DigitFormatter());
                    tfSellingPrice3.setText(sellingPrice.toString());
                    tfSellingPrice3.setTextFormatter(new DigitFormatter());
                    break;

                case 4:
                    tfPurchaseQuantity4.setText(purchaseQuantity.toString());
                    tfPurchaseQuantity4.setTextFormatter(new DigitFormatter());
                    tfSellingPrice4.setText(sellingPrice.toString());
                    tfSellingPrice4.setTextFormatter(new DigitFormatter());
                    break;

                default:
                    // Do nothing
            }
        }
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        close();
    }

    @FXML
    void onActionBtnSave(ActionEvent event) {
        close();
    }

    private void close() {
        ((Stage) contentPane.getScene().getWindow()).close();
    }

}

package com.gitlab.muhammadkholidb.bianglala.controller.product;

import com.gitlab.muhammadkholidb.bianglala.constant.Page;
import com.gitlab.muhammadkholidb.bianglala.control.MaskedTextField;
import com.gitlab.muhammadkholidb.bianglala.utility.PageData;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductSearchResult;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ProductEditController {
    
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
    private ComboBox<?> cbCategory;

    @FXML
    private TextField tfQuantity;

    @FXML
    private ComboBox<?> cbUnit;

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
    private ComboBox<?> cbRack;

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
    
    @FXML
    void initialize() {
        ProductSearchResult result = PageData.INSTANCE.get(Page.MASTER_PRODUCT_MAIN, Page.MASTER_PRODUCT_EDIT);
        tfCode.setText(result.getCode());
        tfName.setText(result.getName());
        tfDescription.setText(result.getDescription());
        tfQuantity.setText(result.getQuantity().toString());
        tfPurchasePrice.setText(result.getPurchasePrice().toString());
        
    }
    
}

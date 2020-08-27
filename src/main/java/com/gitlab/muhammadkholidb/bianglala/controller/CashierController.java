package com.gitlab.muhammadkholidb.bianglala.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.ViewConstants;
import com.gitlab.muhammadkholidb.bianglala.converter.ProductStringConverter;
import com.gitlab.muhammadkholidb.bianglala.data.model.Product;
import com.gitlab.muhammadkholidb.bianglala.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.bianglala.listener.ComboBoxProductAutoCompleteListener;
import com.gitlab.muhammadkholidb.bianglala.service.ProductService;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.ViewLoader;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CashierController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TableView<Product> tableProduct;

    @FXML
    private TableColumn<Product, String> colProductCode;

    @FXML
    private TableColumn<Product, String> colProductName;

    @FXML
    private TableColumn<Product, Integer> colQuantity;

    @FXML
    private TableColumn<Product, BigDecimal> colPrice;

    @FXML
    private TableColumn<Product, BigDecimal> colSubtotal;

    @FXML
    private ComboBox<Product> cbProductAutoComplete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initialize");

        populateTableProduct();

        cbProductAutoComplete.setConverter(new ProductStringConverter());
        cbProductAutoComplete.getEditor().textProperty()
                .addListener(new ComboBoxProductAutoCompleteListener(cbProductAutoComplete));

    }

    private void populateTableProduct() {

        colProductCode.setCellValueFactory(new PropertyValueFactory<>(Product.C_CODE));
        
        colProductName.setCellValueFactory(new PropertyValueFactory<>(Product.C_NAME));

        colQuantity.setStyle("-fx-alignment: center-right");
        colQuantity.setCellValueFactory(new PropertyValueFactory<>(Product.C_QUANTITY));
        colQuantity.setCellFactory(new NumberCellFactory<>(CommonConstants.BAHASA));

        colPrice.setStyle("-fx-alignment: center-right");
        colPrice.setCellValueFactory(new PropertyValueFactory<>(Product.C_PRICE));
        colPrice.setCellFactory(new NumberCellFactory<>(CommonConstants.ENGLISH));

        colSubtotal.setStyle("-fx-alignment: center-right");
        colSubtotal.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            BigDecimal result = product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity()));
            return new SimpleObjectProperty<>(result);
        });
        colSubtotal.setCellFactory(new NumberCellFactory<>(CommonConstants.BAHASA));

        ProductService productService = ApplicationContextHolder.get().getBean(ProductService.class);

        List<Product> products = productService.getAllProducts();
        ObservableList<Product> list = FXCollections.observableList(products);
        tableProduct.setItems(list);

    }

    @FXML
    private void onBtnAddClicked(MouseEvent event) {
        log.debug("Button Add clicked!");
    }

    @FXML
    private void onBtnNewClicked(MouseEvent event) {
        log.debug("Button New clicked!");
    }

    @FXML
    private void onBtnSaveAsDraftClicked(MouseEvent event) {
        log.debug("Button Save as Draft clicked!");
    }

    @FXML
    private void onBtnOpenDraftClicked(MouseEvent event) {
        log.debug("Button Open Draft clicked!");
    }

    @FXML
    private void onBtnPriceCheckClicked(MouseEvent event) {
        log.debug("Button Price Check clicked!");
    }

    @FXML
    private void onBtnPayClicked(MouseEvent event) {
        log.debug("Button Pay clicked!");
    }

    @FXML
    private void onBtnCancelClicked(MouseEvent event) {
        log.debug("Button Cancel clicked!");
    }

    @FXML
    private void onRbEnglishClicked(MouseEvent event) throws IOException {
        log.debug("Radio button English clicked!");
        refreshScene(CommonConstants.ENGLISH);
    }

    @FXML
    private void onRbBahasaClicked(MouseEvent event) throws IOException {
        log.debug("Radio button Bahasa clicked!");
        refreshScene(CommonConstants.BAHASA);
    }

    private void refreshScene(Locale locale) throws IOException {
        AnchorPane page = (AnchorPane) ViewLoader.load(ViewConstants.CASHIER, locale);
        Scene scene = rootPane.getScene();
        scene.setRoot(page);
    }

}

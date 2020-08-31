package com.gitlab.muhammadkholidb.bianglala.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.StyleConstants;
import com.gitlab.muhammadkholidb.bianglala.data.model.Product;
import com.gitlab.muhammadkholidb.bianglala.factory.DateCellFactory;
import com.gitlab.muhammadkholidb.bianglala.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.bianglala.service.ProductService;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductController implements Initializable {

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfName;

    @FXML
    private ComboBox<?> cbCategory;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnSearch;

    @FXML
    private TableView<Product> tableProduct;

    @FXML
    private TableColumn<Product, String> colCode;

    @FXML
    private TableColumn<Product, String> colName;

    @FXML
    private TableColumn<Product, String> colCategory;

    @FXML
    private TableColumn<Product, Integer> colQuantity;

    @FXML
    private TableColumn<Product, String> colUnit;

    @FXML
    private TableColumn<Product, BigDecimal> colPurchasePrice;

    @FXML
    private TableColumn<Product, BigDecimal> colSellingPrice;

    @FXML
    private TableColumn<Product, Date> colCreatedAt;

    @FXML
    private TableColumn<Product, Date> colUpdatedAt;

    @FXML
    private void onActionBtnClear(ActionEvent event) {
        tfCode.setText("");
        tfName.setText("");
        cbCategory.getSelectionModel().selectFirst();
    }

    @FXML
    private void onActionBtnSearch(ActionEvent event) {
        populateTableProduct();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTableProduct();
    }

    private void populateTableProduct() {

        colCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));

        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        colCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoryCode()));

        colQuantity.setStyle(StyleConstants.ALIGN_RIGHT);
        colQuantity.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        colQuantity.setCellFactory(new NumberCellFactory<>(CommonConstants.BAHASA));

        colUnit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUnitLabel()));

        colPurchasePrice.setStyle(StyleConstants.ALIGN_RIGHT);
        colPurchasePrice.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPurchasePrice()));
        colPurchasePrice.setCellFactory(new NumberCellFactory<>(CommonConstants.BAHASA));

        colSellingPrice.setStyle(StyleConstants.ALIGN_RIGHT);
        colSellingPrice.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSellingPrice()));
        colSellingPrice.setCellFactory(new NumberCellFactory<>(CommonConstants.BAHASA));

        colCreatedAt.setCellValueFactory(cellData -> new SimpleObjectProperty<Date>(cellData.getValue().getCreatedAt()));
        colCreatedAt.setCellFactory(new DateCellFactory<>(CommonConstants.DATETIME_PATTERN));
        
        colUpdatedAt.setCellValueFactory(cellData -> new SimpleObjectProperty<Date>(cellData.getValue().getUpdatedAt()));
        colUpdatedAt.setCellFactory(new DateCellFactory<>(CommonConstants.DATETIME_PATTERN));

        setTableProductActionHandler();

        ProductService productService = ApplicationContextHolder.get().getBean(ProductService.class);

        List<Product> products = productService.getAllProducts();
        ObservableList<Product> list = FXCollections.observableList(products);
        tableProduct.setItems(list);
    }

    private void setTableProductActionHandler() {

        tableProduct.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                handleActionTableProduct();
            }
        });

        tableProduct.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleActionTableProduct();
            }
        });

    }

    private void handleActionTableProduct() {
        Product selected = tableProduct.getSelectionModel().getSelectedItem();
        log.debug("Selected product: {}", selected);
    }

}

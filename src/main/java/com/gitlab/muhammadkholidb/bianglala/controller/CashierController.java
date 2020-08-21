package com.gitlab.muhammadkholidb.bianglala.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.data.model.Product;
import com.gitlab.muhammadkholidb.bianglala.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CashierController implements Initializable {

    @FXML
    private TableView<Product> tableProduct;

    @FXML
    private TableColumn<Product, String> colProductCode;

    @FXML
    private TableColumn<Product, String> colProductName;

    @FXML
    private TableColumn<Product, String> colQuantity;

    @FXML
    private TableColumn<Product, String> colPrice;

    @Autowired
    private ProductService productService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initialize");

        colProductCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        List<Product> products = productService.getAllProducts();
        log.debug("Products: {}", products);
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

}

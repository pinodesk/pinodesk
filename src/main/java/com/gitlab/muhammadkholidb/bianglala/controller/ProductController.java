package com.gitlab.muhammadkholidb.bianglala.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    private TableColumn<?, ?> colCode;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colCategory;

    @FXML
    private TableColumn<?, ?> colQuantity;

    @FXML
    private TableColumn<?, ?> colUnit;

    @FXML
    private TableColumn<?, ?> colPurchasePrice;

    @FXML
    private TableColumn<?, ?> colSellingPrice;

    @FXML
    private TableColumn<?, ?> colCreatedAt;

    @FXML
    private TableColumn<?, ?> colUpdatedAt;

    @FXML
    void onActionBtnClear(ActionEvent event) {
        log.debug("onActionBtnClear");
    }

    @FXML
    void onActionBtnSearch(ActionEvent event) {
        log.debug("onActionBtnSearch");
    }

    @FXML
    void initialize() {
        assert tfCode != null : "fx:id=\"tfCode\" was not injected: check your FXML file 'Products.fxml'.";
        assert tfName != null : "fx:id=\"tfName\" was not injected: check your FXML file 'Products.fxml'.";
        assert cbCategory != null : "fx:id=\"cbCategory\" was not injected: check your FXML file 'Products.fxml'.";
        assert btnClear != null : "fx:id=\"btnClear\" was not injected: check your FXML file 'Products.fxml'.";
        assert btnSearch != null : "fx:id=\"btnSearch\" was not injected: check your FXML file 'Products.fxml'.";
        assert colCode != null : "fx:id=\"colCode\" was not injected: check your FXML file 'Products.fxml'.";
        assert colName != null : "fx:id=\"colName\" was not injected: check your FXML file 'Products.fxml'.";
        assert colCategory != null : "fx:id=\"colCategory\" was not injected: check your FXML file 'Products.fxml'.";
        assert colQuantity != null : "fx:id=\"colQuantity\" was not injected: check your FXML file 'Products.fxml'.";
        assert colUnit != null : "fx:id=\"colUnit\" was not injected: check your FXML file 'Products.fxml'.";
        assert colPurchasePrice != null : "fx:id=\"colPurchasePrice\" was not injected: check your FXML file 'Products.fxml'.";
        assert colSellingPrice != null : "fx:id=\"colSellingPrice\" was not injected: check your FXML file 'Products.fxml'.";
        assert colCreatedAt != null : "fx:id=\"colCreatedAt\" was not injected: check your FXML file 'Products.fxml'.";
        assert colUpdatedAt != null : "fx:id=\"colUpdatedAt\" was not injected: check your FXML file 'Products.fxml'.";

    }
}

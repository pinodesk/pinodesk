package com.gitlab.muhammadkholidb.bianglala.controller.product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.ConfigurationConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.Page;
import com.gitlab.muhammadkholidb.bianglala.constant.StyleConstants;
import com.gitlab.muhammadkholidb.bianglala.controller.BaseController;
import com.gitlab.muhammadkholidb.bianglala.javafx.factory.BooleanImageCellFactory;
import com.gitlab.muhammadkholidb.bianglala.javafx.factory.DateCellFactory;
import com.gitlab.muhammadkholidb.bianglala.javafx.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.bianglala.service.ConfigurationService;
import com.gitlab.muhammadkholidb.bianglala.service.ProductService;
import com.gitlab.muhammadkholidb.bianglala.utility.Async;
import com.gitlab.muhammadkholidb.bianglala.utility.FXUtils;
import com.gitlab.muhammadkholidb.bianglala.utility.PageData;
import com.gitlab.muhammadkholidb.bianglala.utility.PageData.PageSet;
import com.gitlab.muhammadkholidb.bianglala.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilterVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductVM;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductMainController extends BaseController {

    @FXML
    private TableView<ProductVM> tableProduct;

    @FXML
    private TableColumn<ProductVM, String> colCode;

    @FXML
    private TableColumn<ProductVM, String> colName;

    @FXML
    private TableColumn<ProductVM, String> colCategory;

    @FXML
    private TableColumn<ProductVM, Integer> colQuantity;

    @FXML
    private TableColumn<ProductVM, String> colUnit;

    @FXML
    private TableColumn<ProductVM, BigDecimal> colPurchasePrice;

    @FXML
    private TableColumn<ProductVM, BigDecimal> colSellingPrice;

    @FXML
    private TableColumn<ProductVM, String> colIncludesVat;

    @FXML
    private TableColumn<ProductVM, Date> colCreatedAt;

    @FXML
    private TableColumn<ProductVM, Date> colUpdatedAt;

    @FXML
    private Label lblRows;

    private ProductService productService;

    private ConfigurationService configurationService;

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = ctx.getBean(ProductService.class);
        configurationService = ctx.getBean(ConfigurationService.class);
    }

    @Override
    protected void initControls() {
        initTableProduct();
        registerKeyListener();
        searchProducts();
    }

    private void initTableProduct() {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        Locale locale = languageCode == null ? CommonConstants.ENGLISH : new Locale(languageCode);
        TableViewUtils.setColumnValue(colCode, ProductVM::getCode);
        TableViewUtils.setColumnValue(colName, ProductVM::getName);
        TableViewUtils.setColumnValue(colCategory, ProductVM::getCategoryName);
        TableViewUtils.setColumnValue(colUnit, ProductVM::getUnitLabel);
        TableViewUtils.initTableColumn(colQuantity, new NumberCellFactory<>(locale), ProductVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colPurchasePrice, new NumberCellFactory<>(locale), ProductVM::getPurchasePrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colSellingPrice, new NumberCellFactory<>(locale), ProductVM::getSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colPurchasePrice, new NumberCellFactory<>(locale), ProductVM::getPurchasePrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colIncludesVat, new BooleanImageCellFactory<>(CommonConstants.YES::equals),
                ProductVM::getVatIncluded, StyleConstants.ALIGN_CENTER);
        TableViewUtils.initTableColumn(colCreatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                ProductVM::getCreatedAt);
        TableViewUtils.initTableColumn(colUpdatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                ProductVM::getUpdatedAt);
    }

    private void registerKeyListener() {
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
        ProductVM selected = tableProduct.getSelectionModel().getSelectedItem();
        PageSet pageSet = new PageSet(Page.MASTER_PRODUCT_MAIN, Page.MASTER_PRODUCT_EDIT);
        PageData.INSTANCE.set(pageSet, selected);
        try {
            FXUtils.show(Page.MASTER_PRODUCT_EDIT, false, event -> {
                if (Boolean.TRUE.equals(PageData.INSTANCE.get(pageSet.swap()))) {
                    searchProducts();
                }
            });
        } catch (IOException ex) {
            log.error("Failed to show window", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void searchProducts() {
        tableProduct.setPlaceholder(new Label(translate("lbl.loadingdata")));
        tableProduct.setItems(FXCollections.observableArrayList());
        Async.supply(() -> {
            ProductFilterVM filter = new ProductFilterVM();
            return productService.searchProduct(filter);
        }).thenAccept(products -> Platform.runLater(() -> {
            if (products.isEmpty()) {
                tableProduct.setPlaceholder(new Label(translate("lbl.nodata")));
                lblRows.setText("0");
            }
            tableProduct.setItems(FXCollections.observableList(products));
            tableProduct.getSortOrder().setAll(colName); // Always sort by name after searching
            lblRows.setText(products.size() + "");
        }));
    }

    @FXML
    void onActionBtnSearch(ActionEvent event) {
        searchProducts();
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        //
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }
}

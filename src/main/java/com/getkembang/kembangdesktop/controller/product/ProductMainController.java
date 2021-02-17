package com.getkembang.kembangdesktop.controller.product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.constant.StyleConstants;
import com.getkembang.kembangdesktop.controller.BaseController;
import com.getkembang.kembangdesktop.javafx.factory.BooleanImageCellFactory;
import com.getkembang.kembangdesktop.javafx.factory.DateCellFactory;
import com.getkembang.kembangdesktop.javafx.factory.NumberCellFactory;
import com.getkembang.kembangdesktop.service.ConfigurationService;
import com.getkembang.kembangdesktop.service.ProductService;
import com.getkembang.kembangdesktop.utility.Async;
import com.getkembang.kembangdesktop.utility.FXUtils;
import com.getkembang.kembangdesktop.utility.TableViewUtils;
import com.getkembang.kembangdesktop.viewmodel.AlertResult;
import com.getkembang.kembangdesktop.viewmodel.ProductFilterVM;
import com.getkembang.kembangdesktop.viewmodel.ProductVM;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
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
    private TableColumn<ProductVM, Date> colExpiredDate;

    @FXML
    private TableColumn<ProductVM, String> colRack;

    @FXML
    private TableColumn<ProductVM, Date> colCreatedAt;

    @FXML
    private TableColumn<ProductVM, Date> colUpdatedAt;

    @FXML
    private Label lblRows;

    private ProductFilterVM productFilter;

    private ProductService productService;

    private ConfigurationService configurationService;

    @FXML
    void onActionBtnAdd(ActionEvent event) throws IOException {
        setNextPage(Page.MASTER_PRODUCT_ADD);
        FXUtils.show(Page.MASTER_PRODUCT_ADD, false, we -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchProducts();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) throws IOException {
        Page nextPage = Page.MASTER_PRODUCT_FILTER;
        setNextPageData(nextPage, productFilter);
        FXUtils.show(nextPage, false, we -> {
            productFilter = getPageData();
            searchProducts();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<ProductVM> items = tableProduct.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_PRODUCTS);
            if (result.isConfirmed()) {
                productService.removeProducts(items.stream().map(ProductVM::getId).collect(Collectors.toList()));
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_PRODUCTS);
                searchProducts();
            }
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = ctx.getBean(ProductService.class);
        configurationService = ctx.getBean(ConfigurationService.class);
    }

    @Override
    protected void initControlActions() {
        initTableProduct();
        registerKeyListener();
    }

    @Override
    protected void initControlValues() {
        productFilter = new ProductFilterVM();
        searchProducts();
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_PRODUCT_MAIN;
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void initTableProduct() {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        Locale locale = languageCode == null ? CommonConstants.ENGLISH : new Locale(languageCode);
        TableViewUtils.setColumnValue(colCode, ProductVM::getCode);
        TableViewUtils.setColumnValue(colName, ProductVM::getName);
        TableViewUtils.setColumnValue(colCategory, ProductVM::getCategoryName);
        TableViewUtils.setColumnValue(colUnit, ProductVM::getUnitLabel);
        TableViewUtils.setColumnValue(colRack, ProductVM::getRackName);
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
        TableViewUtils.initTableColumn(colExpiredDate, new DateCellFactory<>(CommonConstants.DATE_PATTERN),
                ProductVM::getExpiredDate);
        TableViewUtils.initTableColumn(colCreatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                ProductVM::getCreatedAt);
        TableViewUtils.initTableColumn(colUpdatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                ProductVM::getUpdatedAt);
        tableProduct.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        Page nextPage = Page.MASTER_PRODUCT_EDIT;
        setNextPageData(nextPage, selected);
        FXUtils.show(nextPage, false, event -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchProducts();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void searchProducts() {
        tableProduct.setPlaceholder(new Label(translate("lbl.loadingdata")));
        tableProduct.setItems(FXCollections.observableArrayList());
        Async.supply(() -> productService.searchProduct(productFilter)).thenAccept(products -> Platform.runLater(() -> {
            if (products.isEmpty()) {
                tableProduct.setPlaceholder(new Label(translate("lbl.nodata")));
                lblRows.setText("0");
            }
            tableProduct.setItems(FXCollections.observableList(products));
            tableProduct.getSortOrder().setAll(colName); // Always sort by name after searching
            lblRows.setText(products.size() + "");
        }));
    }

}

package com.getkembang.kembangdesktop.controller.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Collectors;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.constant.StyleConstants;
import com.getkembang.kembangdesktop.controller.BaseController;
import com.getkembang.kembangdesktop.service.ConfigurationService;
import com.getkembang.kembangdesktop.service.ProductService;
import com.getkembang.kembangdesktop.viewmodel.ProductFilterVM;
import com.getkembang.kembangdesktop.viewmodel.ProductVM;
import com.gitlab.muhammadkholidb.pandora.factory.BooleanImageCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

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
    private TableColumn<ProductVM, LocalDate> colExpiredDate;

    @FXML
    private TableColumn<ProductVM, String> colRack;

    @FXML
    private TableColumn<ProductVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<ProductVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private ProductFilterVM productFilter;

    private ProductService productService;

    private ConfigurationService configurationService;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.MASTER_PRODUCT_ADD, false, we -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchProducts();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(productFilter);
        StageUtils.modal(Page.MASTER_PRODUCT_FILTER, false, we -> {
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
    protected Stage getCurrentStage() {
        return null;
    }

    private void initTableProduct() {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        Locale locale = new Locale(languageCode);
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
        TableViewUtils.initTableColumn(colExpiredDate, new LocalDateCellFactory<>(CommonConstants.DATE_PATTERN),
                ProductVM::getExpiredDate); 
        TableViewUtils.initTableColumn(colCreatedAt, new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_PATTERN),
                ProductVM::getCreatedAt);
        TableViewUtils.initTableColumn(colUpdatedAt, new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_PATTERN),
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
        setPageData(selected);
        StageUtils.modal(Page.MASTER_PRODUCT_EDIT, false, event -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchProducts();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void searchProducts() {
        tableProduct.setPlaceholder(new Label(translate("lbl.loadingdata")));
        tableProduct.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> productService.searchProduct(productFilter)).thenAccept(products -> Platform.runLater(() -> {
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

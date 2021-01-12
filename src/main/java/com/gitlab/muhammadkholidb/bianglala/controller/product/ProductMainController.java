package com.gitlab.muhammadkholidb.bianglala.controller.product;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.ConfigurationConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.Page;
import com.gitlab.muhammadkholidb.bianglala.constant.StyleConstants;
import com.gitlab.muhammadkholidb.bianglala.controller.BaseController;
import com.gitlab.muhammadkholidb.bianglala.javafx.converter.ProductCategoryComboBoxConverter;
import com.gitlab.muhammadkholidb.bianglala.javafx.factory.DateCellFactory;
import com.gitlab.muhammadkholidb.bianglala.javafx.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.bianglala.javafx.listener.ProductCategoryComboBoxKeyEventHandler;
import com.gitlab.muhammadkholidb.bianglala.service.ConfigurationService;
import com.gitlab.muhammadkholidb.bianglala.service.ProductService;
import com.gitlab.muhammadkholidb.bianglala.utility.Async;
import com.gitlab.muhammadkholidb.bianglala.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.bianglala.utility.FXUtils;
import com.gitlab.muhammadkholidb.bianglala.utility.PageData;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategoryVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilterVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductVM;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductMainController extends BaseController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label lblRows;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfName;

    @FXML
    private ComboBox<ProductCategoryVM> cbCategory;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnSearch;

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
    private TableColumn<ProductVM, Date> colCreatedAt;

    @FXML
    private TableColumn<ProductVM, Date> colUpdatedAt;

    private ProductService productService;

    private ConfigurationService configurationService;

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = ctx.getBean(ProductService.class);
        configurationService = ctx.getBean(ConfigurationService.class);
    }

    @Override
    protected void initControls() {
        ComboBoxUtils.initEditable(cbCategory, new ProductCategoryComboBoxKeyEventHandler(cbCategory),
                new ProductCategoryComboBoxConverter(cbCategory));
        initTableProduct();
        registerKeyListener();
        searchProducts();
    }

    private void initTableProduct() {

        colCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));

        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        colCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoryName()));

        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        Locale locale = languageCode == null ? CommonConstants.ENGLISH : new Locale(languageCode);

        colQuantity.setStyle(StyleConstants.ALIGN_RIGHT);
        colQuantity.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        colQuantity.setCellFactory(new NumberCellFactory<>(locale));

        colUnit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUnitLabel()));

        colPurchasePrice.setStyle(StyleConstants.ALIGN_RIGHT);
        colPurchasePrice
                .setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPurchasePrice()));
        colPurchasePrice.setCellFactory(new NumberCellFactory<>(locale));

        colSellingPrice.setStyle(StyleConstants.ALIGN_RIGHT);
        colSellingPrice.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSellingPrice()));
        colSellingPrice.setCellFactory(new NumberCellFactory<>(locale));

        colCreatedAt.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCreatedAt()));
        colCreatedAt.setCellFactory(new DateCellFactory<>(CommonConstants.DATETIME_PATTERN));

        colUpdatedAt.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUpdatedAt()));
        colUpdatedAt.setCellFactory(new DateCellFactory<>(CommonConstants.DATETIME_PATTERN));

    }

    private void registerKeyListener() {
        tfCode.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchProducts();
            }
        });
        tfName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchProducts();
            }
        });
        cbCategory.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchProducts();
            }
        });
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
        PageData.INSTANCE.set(Page.MASTER_PRODUCT_MAIN, Page.MASTER_PRODUCT_EDIT, selected);
        try {
            FXUtils.show(Page.MASTER_PRODUCT_EDIT, false, event -> {
                log.debug("source: {}", event.getSource());
                searchProducts();
            });
        } catch (IOException ex) {
            log.error("Failed to show window", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void searchProducts() {
        tableProduct.setPlaceholder(new Label(resources.getString("message.loadingdata")));
        tableProduct.setItems(FXCollections.observableArrayList());
        Async.supply(() -> {
            ProductCategoryVM selectedCategory = cbCategory.getSelectionModel().getSelectedItem();
            ProductFilterVM filter = new ProductFilterVM();
            filter.setCode(tfCode.getText());
            filter.setName(tfName.getText());
            filter.setCategoryCode(Objects.isNull(selectedCategory) ? null : selectedCategory.getCode());
            return productService.searchProduct(filter);
        }).thenAccept(products -> Platform.runLater(() -> {
            if (products.isEmpty()) {
                tableProduct.setPlaceholder(new Label(resources.getString("message.nodata")));
                lblRows.setText("0");
            }
            tableProduct.setItems(FXCollections.observableList(products));
            tableProduct.getSortOrder().setAll(colName); // Always sort by name after searching
            lblRows.setText(products.size() + "");
        }));
    }

    @FXML
    void onActionBtnClear(ActionEvent event) {
        tfCode.setText("");
        tfName.setText("");
        cbCategory.getSelectionModel().clearSelection();
        cbCategory.getEditor().clear();
    }

    @FXML
    void onActionBtnSearch(ActionEvent event) {
        searchProducts();
    }

}

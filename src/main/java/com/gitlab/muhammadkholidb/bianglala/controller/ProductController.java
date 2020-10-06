package com.gitlab.muhammadkholidb.bianglala.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.StyleConstants;
import com.gitlab.muhammadkholidb.bianglala.converter.ProductCategoryComboBoxConverter;
import com.gitlab.muhammadkholidb.bianglala.factory.DateCellFactory;
import com.gitlab.muhammadkholidb.bianglala.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.bianglala.listener.ProductCategoryComboBoxKeyEventHandler;
import com.gitlab.muhammadkholidb.bianglala.service.ProductService;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.Async;
import com.gitlab.muhammadkholidb.bianglala.utility.Settings;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategorySearchResult;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilter;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductSearchResult;
import java.util.Objects;

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
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductController {

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
    private ComboBox<ProductCategorySearchResult> cbCategory;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnSearch;

    @FXML
    private TableView<ProductSearchResult> tableProduct;

    @FXML
    private TableColumn<ProductSearchResult, String> colCode;

    @FXML
    private TableColumn<ProductSearchResult, String> colName;

    @FXML
    private TableColumn<ProductSearchResult, String> colCategory;

    @FXML
    private TableColumn<ProductSearchResult, Integer> colQuantity;

    @FXML
    private TableColumn<ProductSearchResult, String> colUnit;

    @FXML
    private TableColumn<ProductSearchResult, BigDecimal> colPurchasePrice;

    @FXML
    private TableColumn<ProductSearchResult, BigDecimal> colSellingPrice;

    @FXML
    private TableColumn<ProductSearchResult, Date> colCreatedAt;

    @FXML
    private TableColumn<ProductSearchResult, Date> colUpdatedAt;

    private ProductService productService;

    @FXML
    void initialize() {
        ApplicationContext ctx = ApplicationContextHolder.get();
        productService = ctx.getBean(ProductService.class);
        initComboBoxCategory();
        initTableProduct();
        registerKeyListener();
        searchProducts();
    }

    private void initComboBoxCategory() {
        ComboBoxListViewSkin<ProductCategorySearchResult> comboBoxListViewSkin = new ComboBoxListViewSkin<>(cbCategory);
        comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, (event) -> {
            if (event.getCode() == KeyCode.SPACE) {
                event.consume();
            }
        });
        cbCategory.setSkin(comboBoxListViewSkin);
        cbCategory.getEditor().setOnKeyReleased(new ProductCategoryComboBoxKeyEventHandler(cbCategory));
        cbCategory.setConverter(new ProductCategoryComboBoxConverter(cbCategory));
    }

    private void initTableProduct() {

        colCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));

        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        colCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoryName()));

        colQuantity.setStyle(StyleConstants.ALIGN_RIGHT);
        colQuantity.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        colQuantity.setCellFactory(new NumberCellFactory<>(Settings.CURRENT_LOCALE));

        colUnit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUnitLabel()));

        colPurchasePrice.setStyle(StyleConstants.ALIGN_RIGHT);
        colPurchasePrice.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPurchasePrice()));
        colPurchasePrice.setCellFactory(new NumberCellFactory<>(Settings.CURRENT_LOCALE));

        colSellingPrice.setStyle(StyleConstants.ALIGN_RIGHT);
        colSellingPrice.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSellingPrice()));
        colSellingPrice.setCellFactory(new NumberCellFactory<>(Settings.CURRENT_LOCALE));

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
        ProductSearchResult selected = tableProduct.getSelectionModel().getSelectedItem();
        log.debug("Selected product: {}", selected);
    }

    @SuppressWarnings("unchecked")
    private void searchProducts() {
        tableProduct.setPlaceholder(new Label("Loading data"));
        tableProduct.setItems(FXCollections.observableArrayList());
        Async.supply(() -> {
            ProductCategorySearchResult selectedCategory = cbCategory.getSelectionModel().getSelectedItem();
            log.debug("Selected category: {}", selectedCategory);
            ProductFilter filter = new ProductFilter();
            filter.setCode(tfCode.getText());
            filter.setName(tfName.getText());
            filter.setCategoryCode(Objects.isNull(selectedCategory) ? null : selectedCategory.getCode());
            return productService.searchProduct(filter);
        }).thenAccept(products -> {
            Platform.runLater(() -> {
                if (products.isEmpty()) {
                    tableProduct.setPlaceholder(new Label("No data to display"));
                    lblRows.setText("0");
                }
                tableProduct.setItems(FXCollections.observableList(products));
                tableProduct.getSortOrder().setAll(colName); // Always sort by name after searching
                lblRows.setText(products.size() + "");
            });
        });
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

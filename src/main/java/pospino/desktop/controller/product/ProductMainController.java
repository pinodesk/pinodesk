package pospino.desktop.controller.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import com.gitlab.mudiasoft.pandora.factory.LocalDateCellFactory;
import com.gitlab.mudiasoft.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.toolbox.data.StringNumberUtils;
import com.gitlab.mudiasoft.toolbox.future.AsyncUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.BaseController;
import pospino.desktop.service.ProductService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.ProductFilterVM;
import pospino.desktop.viewmodel.ProductVM;

public class ProductMainController extends BaseController {

    @FXML
    private TableView<ProductVM> tblProduct;

    @FXML
    private TableColumn<ProductVM, String> colCode;

    @FXML
    private TableColumn<ProductVM, String> colStatus;

    @FXML
    private TableColumn<ProductVM, String> colBarcode;

    @FXML
    private TableColumn<ProductVM, String> colName;

    @FXML
    private TableColumn<ProductVM, String> colCategory;

    @FXML
    private TableColumn<ProductVM, Integer> colQuantity;

    @FXML
    private TableColumn<ProductVM, String> colUnit;

    @FXML
    private TableColumn<ProductVM, BigDecimal> colPrescriptionSellingPrice;

    @FXML
    private TableColumn<ProductVM, BigDecimal> colGeneralSellingPrice;

    @FXML
    private TableColumn<ProductVM, BigDecimal> colAverageBuyingPrice;

    @FXML
    private TableColumn<ProductVM, LocalDate> colClosestExpiry;

    @FXML
    private TableColumn<ProductVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<ProductVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    @FXML
    private Button btnPackage;

    @FXML
    private Button btnImport;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    private ProductFilterVM productFilter;

    private ProductService productService;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_PRODUCT_ADD, we -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchProducts();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(productFilter);
        StageUtils.modal(Page.CATALOG_PRODUCT_FILTER, false, we -> {
            ProductFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            productFilter = result;
            searchProducts();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<ProductVM> items = tblProduct.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_PRODUCTS);
            if (result.isConfirmed()) {
                productService.removeProducts(items.stream().map(ProductVM::getId).toList());
                searchProducts();
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_PRODUCTS);
            }
        }
    }

    @FXML
    void onActionBtnImport(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_PRODUCT_IMPORT, false, we -> {
            // Remove the last data from the stack and ignore (if not used) to avoid such
            // this issue https://gitlab.com/pospino/pospino-desktop/-/issues/52
            getPageData();
            searchProducts();
        });
    }

    @FXML
    void onActionBtnPackage(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_PRODUCT_ADD_PACKAGE, false, we -> {
            getPageData();
            searchProducts();
        });
    }

    @Override
    protected void initServices() {
        productService = SpringUtils.getBean(ProductService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.CATALOG_PRODUCTS, btnAdd, btnRemove, btnImport, btnPackage);
        initTableProduct();
        registerKeyListener();
    }

    @Override
    protected void initControlValues() {
        productFilter = new ProductFilterVM();
        searchProducts();
        if (!isPharmacyFeatureEnabled()) {
            tblProduct.getColumns().remove(colPrescriptionSellingPrice);
        }
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void initTableProduct() {
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colCode, ProductVM::getCode);
        TableViewUtils.setColumnValue(colBarcode, ProductVM::getBarcode);
        TableViewUtils.setColumnValue(colName, ProductVM::getName);
        TableViewUtils.setColumnValue(colCategory, ProductVM::getCategoryName);
        TableViewUtils.setColumnValue(colUnit, ProductVM::getUnitLabel);
        TableViewUtils.setColumnValue(colStatus, ProductVM::getStatus);
        TableViewUtils.initTableColumn(
                colAverageBuyingPrice,
                new NumberCellFactory<>(locale),
                ProductVM::getAverageBuyingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                ProductVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colGeneralSellingPrice,
                new NumberCellFactory<>(locale),
                ProductVM::getGeneralSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPrescriptionSellingPrice,
                new NumberCellFactory<>(locale),
                ProductVM::getPrescriptionSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colClosestExpiry,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ProductVM::getClosestExpiredDate);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ProductVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ProductVM::getUpdatedAt);
        tblProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblProduct.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void registerKeyListener() {
        tblProduct.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableProduct();
            }
        });
        tblProduct.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableProduct();
            }
        });
    }

    private void handleActionTableProduct() {
        if (TableViewUtils.hasItemSelected(tblProduct)) {
            ProductVM product = TableViewUtils.getSelectedItem(tblProduct);
            setPageData(product);
            if (CommonConstants.PRODUCT_CATEGORY_CODE_CUSTOM_PACKAGE.equals(product.getCategoryCode())) {
                StageUtils.modal(Page.CATALOG_PRODUCT_EDIT_PACKAGE, event -> {
                    // Remove the last data from the stack and ignore (if not used) to avoid such
                    // this issue https://gitlab.com/pospino/pospino-desktop/-/issues/52
                    getPageData();
                    searchProducts();
                });
            } else {
                StageUtils.modal(Page.CATALOG_PRODUCT_EDIT, event -> {
                    getPageData();
                    searchProducts();
                });
            }
        }
    }

    private void searchProducts() {
        tblProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblProduct.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> productService.searchProductsByFilter(productFilter))
                .thenAccept(products -> Platform.runLater(() -> {
                    if (products.isEmpty()) {
                        tblProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                        setVisibleInLayout(true, btnImport);
                        return;
                    }
                    tblProduct.setItems(FXCollections.observableList(products));
                    TableViewUtils.sortDescending(tblProduct, colUpdatedAt);
                    lblRows.setText(StringNumberUtils.format(products.size(), resources.getLocale()));
                    setVisibleInLayout(false, btnImport);
                }));
    }

}

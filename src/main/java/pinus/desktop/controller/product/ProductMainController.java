package pinus.desktop.controller.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
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
import javafx.stage.Stage;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.constant.Page;
import pinus.desktop.constant.StyleConstants;
import pinus.desktop.controller.BaseController;
import pinus.desktop.service.ProductService;
import pinus.desktop.viewmodel.ProductFilterVM;
import pinus.desktop.viewmodel.SearchProductsByFilterVM;

public class ProductMainController extends BaseController {

    @FXML
    private TableView<SearchProductsByFilterVM> tblProduct;

    @FXML
    private TableColumn<SearchProductsByFilterVM, String> colCode;

    @FXML
    private TableColumn<SearchProductsByFilterVM, String> colStatus;

    @FXML
    private TableColumn<SearchProductsByFilterVM, String> colBarcode;

    @FXML
    private TableColumn<SearchProductsByFilterVM, String> colName;

    @FXML
    private TableColumn<SearchProductsByFilterVM, String> colCategory;

    @FXML
    private TableColumn<SearchProductsByFilterVM, Integer> colQuantity;

    @FXML
    private TableColumn<SearchProductsByFilterVM, String> colUnit;

    @FXML
    private TableColumn<SearchProductsByFilterVM, BigDecimal> colPrescriptionSellingPrice;

    @FXML
    private TableColumn<SearchProductsByFilterVM, BigDecimal> colGeneralSellingPrice;

    @FXML
    private TableColumn<SearchProductsByFilterVM, BigDecimal> colAverageBuyingPrice;

    @FXML
    private TableColumn<SearchProductsByFilterVM, LocalDate> colClosestExpiry;

    @FXML
    private TableColumn<SearchProductsByFilterVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private ProductFilterVM productFilter;

    private ProductService productService;

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
        ObservableList<SearchProductsByFilterVM> items = tblProduct.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_PRODUCTS);
            if (result.isConfirmed()) {
                productService.removeProducts(items.stream().map(SearchProductsByFilterVM::getId).toList());
                searchProducts();
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_PRODUCTS);
            }
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = ctx.getBean(ProductService.class);
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
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colCode, SearchProductsByFilterVM::getCode);
        TableViewUtils.setColumnValue(colBarcode, SearchProductsByFilterVM::getBarcode);
        TableViewUtils.setColumnValue(colName, SearchProductsByFilterVM::getName);
        TableViewUtils.setColumnValue(colCategory, SearchProductsByFilterVM::getCategoryName);
        TableViewUtils.setColumnValue(colUnit, SearchProductsByFilterVM::getUnitLabel);
        TableViewUtils.setColumnValue(colStatus, SearchProductsByFilterVM::getStatus);
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                SearchProductsByFilterVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colGeneralSellingPrice,
                new NumberCellFactory<>(locale),
                SearchProductsByFilterVM::getGeneralSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPrescriptionSellingPrice,
                new NumberCellFactory<>(locale),
                SearchProductsByFilterVM::getPrescriptionSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colClosestExpiry,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                SearchProductsByFilterVM::getClosestExpiredDate);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                SearchProductsByFilterVM::getUpdatedAt);
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
            setPageData(TableViewUtils.getSelectedItem(tblProduct));
            StageUtils.modal(Page.MASTER_PRODUCT_EDIT, event -> {
                searchProducts();
            });
        }
    }

    private void searchProducts() {
        tblProduct.setPlaceholder(new Label(translate(CommonLabel.LBL_LOADING_DATA)));
        tblProduct.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> productService.searchProductsByFilter(productFilter))
                .thenAccept(products -> Platform.runLater(() -> {
                    if (products.isEmpty()) {
                        tblProduct.setPlaceholder(new Label(translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblProduct.setItems(FXCollections.observableList(products));
                    TableViewUtils.sortDescending(tblProduct, colUpdatedAt); // Always sort by updated at after
                                                                             // searching
                    lblRows.setText(products.size() + "");
                }));
    }

}

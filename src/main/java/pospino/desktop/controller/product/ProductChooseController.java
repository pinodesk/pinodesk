package pospino.desktop.controller.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.CommonDataChooseController;
import pospino.desktop.service.ProductService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.ProductVM;

public class ProductChooseController extends CommonDataChooseController<ProductVM> {

    @FXML
    private TextField tfSearch;

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
    private TableColumn<ProductVM, LocalDateTime> colUpdatedAt;

    private ProductService productService;

    @Override
    protected void initDataChooseControlActions() {
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colCode, ProductVM::getCode);
        TableViewUtils.setColumnValue(colBarcode, ProductVM::getBarcode);
        TableViewUtils.setColumnValue(colName, ProductVM::getName);
        TableViewUtils.setColumnValue(colCategory, ProductVM::getCategoryName);
        TableViewUtils.setColumnValue(colUnit, ProductVM::getUnitLabel);
        TableViewUtils.setColumnValue(colStatus, ProductVM::getStatus);
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                ProductVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colAverageBuyingPrice,
                new NumberCellFactory<>(locale),
                ProductVM::getAverageBuyingPrice,
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
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                ProductVM::getUpdatedAt);
        tblProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblProduct.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblProduct.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                btnChoose.fire();
            }
        });
        tfSearch.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                searchProducts();
            }
        });
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        String keyword = getPageData();
        if (StringUtils.isNotBlank(keyword)) {
            tfSearch.setText(keyword);
            searchProducts();
        }
        if (!isPharmacyFeatureEnabled()) {
            tblProduct.getColumns().remove(colPrescriptionSellingPrice);
        }
    }

    @Override
    protected ProductVM getSelectedData() {
        return tblProduct.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices() {
        productService = SpringUtils.getBean(ProductService.class);
    }

    private void searchProducts() {
        tblProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblProduct.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> productService.searchProductsByKeyword(tfSearch.getText()))
                .thenAccept(products -> Platform.runLater(() -> {
                    if (products.isEmpty()) {
                        tblProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblProduct.setItems(FXCollections.observableList(products));
                    TableViewUtils.sortAscending(tblProduct, colName);
                }));
    }

}

package pinus.desktop.controller.product;

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

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.StyleConstants;
import pinus.desktop.controller.CommonDataChooseController;
import pinus.desktop.service.ProductService;
import pinus.desktop.util.SpringUtils;
import pinus.desktop.viewmodel.SearchProductsByFilterVM;

public class ProductChooseController extends CommonDataChooseController<SearchProductsByFilterVM> {

    @FXML
    private TextField tfSearch;

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

    private ProductService productService;

    @Override
    protected void initDataChooseControlActions() {
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
                colAverageBuyingPrice,
                new NumberCellFactory<>(locale),
                SearchProductsByFilterVM::getAverageBuyingPrice,
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
        // Nothing to do
    }

    @Override
    protected SearchProductsByFilterVM getSelectedData() {
        return tblProduct.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = SpringUtils.getBean(ProductService.class);
    }

    private void searchProducts() {
        tblProduct.setPlaceholder(new Label(translate("lbl.loadingdata")));
        tblProduct.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> productService.searchProductsByKeyword(tfSearch.getText()))
                .thenAccept(products -> Platform.runLater(() -> {
                    if (products.isEmpty()) {
                        tblProduct.setPlaceholder(new Label(translate("lbl.nodata")));
                    }
                    tblProduct.setItems(FXCollections.observableList(products));
                    TableViewUtils.sortAscending(tblProduct, colName);
                }));
    }

}

package pinodesk.controller.catalog.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import com.mudiatech.pandora.factory.LocalDateCellFactory;
import com.mudiatech.pandora.factory.LocalDateTimeCellFactory;
import com.mudiatech.pandora.factory.NumberCellFactory;
import com.mudiatech.pandora.utility.AlertResult;
import com.mudiatech.pandora.utility.EventUtils;
import com.mudiatech.pandora.utility.IMessage;
import com.mudiatech.pandora.utility.StageUtils;
import com.mudiatech.pandora.utility.TableViewUtils;
import com.mudiatech.toolbox.data.StringNumberUtils;
import com.mudiatech.toolbox.future.AsyncUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.MenuCodeConstants;
import pinodesk.constant.MessageCode;
import pinodesk.constant.Page;
import pinodesk.constant.ProductStatus;
import pinodesk.constant.StyleConstants;
import pinodesk.constant.SystemConstants;
import pinodesk.controller.BaseController;
import pinodesk.service.ProductService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.ProductFilterVM;
import pinodesk.viewmodel.ProductVM;

@Slf4j
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

    @FXML
    private Button btnExport;

    private static final IMessage[] PRODUCT_COLUMN_LABELS = new IMessage[] {
            CommonLabel.LBL_NAME,
            CommonLabel.LBL_QUANTITY,
            CommonLabel.LBL_UNIT,
            CommonLabel.LBL_CATEGORY,
            CommonLabel.LBL_GENERAL_SELLING_PRICE,
            CommonLabel.LBL_PRESCRIPTION_SELLING_PRICE,
            CommonLabel.LBL_AVERAGE_BUYING_PRICE,
            CommonLabel.LBL_CODE,
            CommonLabel.LBL_BARCODE,
            CommonLabel.LBL_EXPIRED_DATE,
            CommonLabel.LBL_STATUS,
            CommonLabel.LBL_CREATED_AT,
            CommonLabel.LBL_UPDATED_AT };

    private FileChooser fileChooser = new FileChooser();

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
            // this issue https://gitlab.com/pinodesk/pinodesk/-/issues/52
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

    @FXML
    void onActionBtnExport(ActionEvent event) {
        ObservableList<ProductVM> products = tblProduct.getItems();
        if (products.isEmpty()) {
            return;
        }
        File file = fileChooser.showSaveDialog(getCurrentStage());
        if (file == null) {
            return;
        }
        Stage loading = displayLoading();
        CompletableFuture.runAsync(() -> {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet(t.translate(CommonLabel.LBL_PRODUCTS));
                XSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_PRODUCTS));
                row = sheet.createRow(2);
                for (int i = 0; i < PRODUCT_COLUMN_LABELS.length; i++) {
                    IMessage lbl = PRODUCT_COLUMN_LABELS[i];
                    row.createCell(i).setCellValue(t.translate(lbl));
                }
                int rowNum = 3;
                for (ProductVM vm : products) {
                    row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(vm.getName());
                    if (vm.getQuantity() != null) {
                        row.createCell(1).setCellValue(vm.getQuantity());
                    }
                    row.createCell(2).setCellValue(vm.getUnitLabel());
                    row.createCell(3).setCellValue(vm.getCategoryName());
                    if (vm.getGeneralSellingPrice() != null) {
                        row.createCell(4).setCellValue(vm.getGeneralSellingPrice().intValue());
                    }
                    if (vm.getPrescriptionSellingPrice() != null) {
                        row.createCell(5).setCellValue(vm.getPrescriptionSellingPrice().intValue());
                    }
                    if (vm.getAverageBuyingPrice() != null) {
                        row.createCell(6).setCellValue(vm.getAverageBuyingPrice().intValue());
                    }
                    row.createCell(7).setCellValue(vm.getCode());
                    row.createCell(8).setCellValue(vm.getBarcode());
                    if (vm.getClosestExpiredDate() != null) {
                        row.createCell(9).setCellValue(dateFormatter.format(vm.getClosestExpiredDate()));
                    }
                    row.createCell(10).setCellValue(
                            ProductStatus.ACTIVE.toString().equals(vm.getStatus()) ?
                                    t.translate(CommonLabel.LBL_ACTIVE) : t.translate(CommonLabel.LBL_INACTIVE));
                    row.createCell(11).setCellValue(dateFormatter.format(vm.getCreatedAt()));
                    row.createCell(12).setCellValue(dateFormatter.format(vm.getUpdatedAt()));
                }
                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
                sheet.autoSizeColumn(2);
                sheet.autoSizeColumn(3);
                sheet.autoSizeColumn(4);
                sheet.autoSizeColumn(5);
                sheet.autoSizeColumn(6);
                sheet.autoSizeColumn(7);
                sheet.autoSizeColumn(8);
                sheet.autoSizeColumn(9);
                sheet.autoSizeColumn(10);
                sheet.autoSizeColumn(11);
                sheet.autoSizeColumn(12);
                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                workbook.close();
            } catch (Exception e) {
                log.error("Error on export products", e);
                throw new CompletionException(e);
            }
        }).whenComplete((result, ex) -> Platform.runLater(() -> {
            loading.hide();
            if (ex != null) {
                ex.printStackTrace();
                handleException(ex);
                return;
            }
            displayInfo(String.format(t.translate(MessageCode.SUCCESS_EXPORT_PRODUCTS), file.getAbsolutePath()));
        }));
    }

    @Override
    protected void initServices() {
        productService = SpringUtils.getBean(ProductService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.CATALOG_PRODUCTS, btnAdd, btnRemove, btnImport, btnPackage);
        initTableProduct();
        fileChooser.setInitialDirectory(new File(SystemConstants.USER_HOME_DIR));
        fileChooser.setInitialFileName("pinodesk-products.xlsx");
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
                    // this issue https://gitlab.com/pinodesk/pinodesk/-/issues/52
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

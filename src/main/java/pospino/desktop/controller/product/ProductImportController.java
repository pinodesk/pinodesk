package pospino.desktop.controller.product;

import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toIntegerOrNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gitlab.muhammadkholidb.pandora.utility.IMessage;
import com.gitlab.muhammadkholidb.toolbox.data.DateTimeUtils;
import com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.ProductStatus;
import pospino.desktop.controller.CommonContentPaneController;
import pospino.desktop.service.DrugClassificationService;
import pospino.desktop.service.ProductCategoryService;
import pospino.desktop.service.ProductService;
import pospino.desktop.service.UnitService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.DrugClassificationVM;
import pospino.desktop.viewmodel.ProductCategoryVM;
import pospino.desktop.viewmodel.ProductImportVM;
import pospino.desktop.viewmodel.UnitVM;

public class ProductImportController extends CommonContentPaneController {

    @FXML
    private TextField tfFile;

    @FXML
    private Button btnDownload;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnImport;

    private FileChooser fileChooser = new FileChooser();
    private File selectedTemplate;

    private ProductService productService;
    private UnitService unitService;
    private ProductCategoryService productCategoryService;
    private DrugClassificationService drugClassificationService;

    private static final IMessage[] PRODUCT_SHEET_COLUMN_LABELS = new IMessage[] {
            CommonLabel.LBL_NAME,
            CommonLabel.LBL_CODE,
            CommonLabel.LBL_BARCODE,
            CommonLabel.LBL_DESCRIPTION,
            CommonLabel.LBL_PRODUCT_CATEGORY_CODE,
            CommonLabel.LBL_UNIT_ID,
            CommonLabel.LBL_GENERAL_SELLING_PRICE,
            CommonLabel.LBL_PRESCRIPTION_SELLING_PRICE,
            CommonLabel.LBL_QUANTITY,
            CommonLabel.LBL_EXPIRED_DATE,
            CommonLabel.LBL_DRUG_CLASSIFICATION_CODE,
            CommonLabel.LBL_INDICATION,
            CommonLabel.LBL_CONTRAINDICATION, };

    private static final IMessage[] PRODUCT_CATEGORY_SHEET_COLUMN_LABELS = new IMessage[] {
            CommonLabel.LBL_CODE,
            CommonLabel.LBL_NAME };

    private static final IMessage[] UNIT_SHEET_COLUMN_LABELS = new IMessage[] {
            CommonLabel.LBL_ID,
            CommonLabel.LBL_NAME,
            CommonLabel.LBL_LABEL, };

    private static final IMessage[] DRUG_CLASSIFICATION_SHEET_COLUMN_LABELS = new IMessage[] {
            CommonLabel.LBL_CODE,
            CommonLabel.LBL_NAME };

    private String userHomeDir = System.getProperty("user.home");

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        close();
    }

    @FXML
    void onActionBtnDownload(ActionEvent event) {
        String location = userHomeDir + "/Downloads/" + CommonConstants.IMPORT_TEMPLATE_FILE_NAME;
        Stage stage = displayLoading();
        CompletableFuture.runAsync(() -> {
            XSSFWorkbook workbook = new XSSFWorkbook();
            createSheetProduct(workbook);
            createSheetProductCategory(workbook);
            createSheetUnit(workbook);
            createSheetDrugCategory(workbook);
            try {
                FileOutputStream fos = new FileOutputStream(location);
                workbook.write(fos);
                workbook.close();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).whenComplete((result, ex) -> Platform.runLater(() -> {
            stage.hide();
            if (ex != null) {
                handleException(ex);
                return;
            }
            displayInfo(String.format(t.translate(MessageCode.SUCCESS_DOWNLOAD_TEMPLATE), location));
        }));
        setFocusedToContentPane();
    }

    @FXML
    void onActionBtnImport(ActionEvent event) {
        if (selectedTemplate == null) {
            displayError(MessageCode.ERROR_EMPTY_FILE);
            return;
        }
        importProducts();
    }

    @Override
    protected void initContentPaneControlActions() {
        final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load
        fileChooser.setInitialDirectory(new File(userHomeDir));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter(t.translate(CommonLabel.LBL_XLSX_FILES), "*.xlsx"));
        tfFile.focusedProperty().addListener((o, ov, nv) -> {
            boolean isFocused = Boolean.TRUE.equals(nv);
            if (isFocused && firstTime.get()) {
                setFocusedToContentPane();
                firstTime.setValue(false);
                return;
            }
            if (isFocused) {
                setFocused(btnImport);
                selectedTemplate = fileChooser.showOpenDialog(getCurrentStage());
                if (selectedTemplate != null) {
                    tfFile.setText(selectedTemplate.getAbsolutePath());
                } else {
                    tfFile.setText("");
                }
            }
        });
    }

    @Override
    protected void initServices() {
        productService = SpringUtils.getBean(ProductService.class);
        unitService = SpringUtils.getBean(UnitService.class);
        productCategoryService = SpringUtils.getBean(ProductCategoryService.class);
        drugClassificationService = SpringUtils.getBean(DrugClassificationService.class);
    }

    @Override
    protected void initControlValues() {
        // Nothing to initialize
    }

    private void importProducts() {
        setFocusedToContentPane();
        Stage stage = displayLoading();
        CompletableFuture.runAsync(() -> {
            try {
                productService.importProducts(readProductsToImport());
            } catch (IOException | InvalidFormatException e) {
                throw new CompletionException(e);
            }
        }).whenComplete((result, ex) -> Platform.runLater(() -> {
            stage.hide();
            if (ex != null) {
                handleException(ex);
                return;
            }
            close();
        }));
    }

    private List<ProductImportVM> readProductsToImport() throws IOException, InvalidFormatException {
        List<ProductImportVM> productImports = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(selectedTemplate)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            sheet.forEach(r -> {
                XSSFRow row = (XSSFRow) r;
                if (row.getRowNum() == 0) {
                    return;
                }
                ProductImportVM productImport = new ProductImportVM();
                productImport.setName(getCellValue(row, 0));
                productImport.setCode(getCellValue(row, 1));
                productImport.setBarcode(getCellValue(row, 2));
                productImport.setDescription(getCellValue(row, 3));
                productImport.setProductCategoryCode(getCellValue(row, 4));
                productImport.setUnitId(StringNumberUtils.toLongOrNull(getCellValue(row, 5)));
                productImport.setGeneralSellingPrice(toBigDecimalOrNull(getCellValue(row, 6)));
                productImport.setPrescriptionSellingPrice(toBigDecimalOrNull(getCellValue(row, 7)));
                productImport.setQuantity(toIntegerOrNull(getCellValue(row, 8)));
                productImport.setExpiredDate(
                        DateTimeUtils
                                .parseLocalDateQuietly(getCellValue(row, 9), CommonConstants.DATE_DISPLAY_PATTERN));
                productImport.setDrugClassificationCode(getCellValue(row, 10));
                productImport.setIndication(getCellValue(row, 11));
                productImport.setContraindication(getCellValue(row, 12));
                productImport.setStatus(ProductStatus.ACTIVE);
                productImports.add(productImport);
            });
        }
        return productImports;
    }

    private String getCellValue(XSSFRow row, int index) {
        String value = "";
        XSSFCell cell = row.getCell(index, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                value = DateFormatUtils.format(cell.getDateCellValue(), CommonConstants.DATE_DISPLAY_PATTERN);
            } else {
                value += cell.getRawValue();
            }
        } else {
            value = cell.getStringCellValue();
        }
        return value;
    }

    private void createSheetProduct(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet(t.translate(CommonLabel.LBL_PRODUCT));
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < PRODUCT_SHEET_COLUMN_LABELS.length; i++) {
            row.createCell(i).setCellValue(t.translate(PRODUCT_SHEET_COLUMN_LABELS[i]));
            sheet.autoSizeColumn(i);
        }
        row = sheet.createRow(1);
        row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_SAMPLE_PRODUCT));
        row.createCell(1).setCellValue("XXX-0001");
        row.createCell(2).setCellValue("111122223333");
        row.createCell(3).setCellValue("");
        row.createCell(4).setCellValue(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
        row.createCell(5).setCellValue(1);
        row.createCell(6).setCellValue(10000);
        row.createCell(7).setCellValue(11000);
        row.createCell(8).setCellValue(10);
        row.createCell(9).setCellValue("2030-01-01");
        row.createCell(10).setCellValue("0001");
        row.createCell(11).setCellValue("");
        row.createCell(12).setCellValue("");
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void createSheetProductCategory(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet(t.translate(CommonLabel.LBL_PRODUCT_CATEGORY));
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < PRODUCT_CATEGORY_SHEET_COLUMN_LABELS.length; i++) {
            row.createCell(i).setCellValue(t.translate(PRODUCT_CATEGORY_SHEET_COLUMN_LABELS[i]));
        }
        int rowNum = 1;
        List<ProductCategoryVM> productCategories = productCategoryService.searchProductCategoryByKeyword("");
        for (ProductCategoryVM category : productCategories) {
            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(category.getCode());
            row.createCell(1).setCellValue(category.getName());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createSheetUnit(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet(t.translate(CommonLabel.LBL_UNIT));
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < UNIT_SHEET_COLUMN_LABELS.length; i++) {
            row.createCell(i).setCellValue(t.translate(UNIT_SHEET_COLUMN_LABELS[i]));
        }
        int rowNum = 1;
        List<UnitVM> units = unitService.getAllUnits();
        for (UnitVM unit : units) {
            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(unit.getId());
            row.createCell(1).setCellValue(unit.getName());
            row.createCell(2).setCellValue(unit.getLabel());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void createSheetDrugCategory(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet(t.translate(CommonLabel.LBL_DRUG_CLASSIFICATION));
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < DRUG_CLASSIFICATION_SHEET_COLUMN_LABELS.length; i++) {
            row.createCell(i).setCellValue(t.translate(DRUG_CLASSIFICATION_SHEET_COLUMN_LABELS[i]));
        }
        int rowNum = 1;
        List<DrugClassificationVM> categories = drugClassificationService
                .searchDrugClassificationsByKeyword("", resources.getLocale().getLanguage());
        for (DrugClassificationVM category : categories) {
            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(category.getCode());
            row.createCell(1).setCellValue(category.getName());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

}

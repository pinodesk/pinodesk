package pospino.desktop.controller.report.purchase;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gitlab.mudiasoft.pandora.factory.DefaultCellFactory;
import com.gitlab.mudiasoft.pandora.factory.LocalDateCellFactory;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.utility.IMessage;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.DiscountType;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.constant.SystemConstants;
import pospino.desktop.controller.BaseController;
import pospino.desktop.service.PurchaseService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.PurchaseReportFilterVM;
import pospino.desktop.viewmodel.PurchaseReportVM;

public class PurchaseReportMainController extends BaseController {

    @FXML
    private Button btnFilter;

    @FXML
    private TableColumn<PurchaseReportVM, String> colSupplierName;

    @FXML
    private TableColumn<PurchaseReportVM, LocalDate> colInvoiceDate;

    @FXML
    private TableColumn<PurchaseReportVM, String> colInvoiceNumber;

    @FXML
    private TableColumn<PurchaseReportVM, String> colPaymentStatus;

    @FXML
    private TableColumn<PurchaseReportVM, String> colProductName;

    @FXML
    private TableColumn<PurchaseReportVM, Integer> colQuantity;

    @FXML
    private TableColumn<PurchaseReportVM, BigDecimal> colBuyingPrice;

    @FXML
    private TableColumn<PurchaseReportVM, String> colDiscount;

    @FXML
    private TableColumn<PurchaseReportVM, BigDecimal> colBuyingPriceDiscount;

    @FXML
    private TableColumn<PurchaseReportVM, BigDecimal> colSubtotalDiscount;

    @FXML
    private TableColumn<PurchaseReportVM, BigDecimal> colSubtotalPrice;

    @FXML
    private TableColumn<PurchaseReportVM, String> colUnit;

    @FXML
    private TableView<PurchaseReportVM> tblPurchaseReport;

    @FXML
    private Label lblRows;

    @FXML
    private Label lblRevenue;

    @FXML
    private Label lblPaid;

    @FXML
    private Label lblTransactionCount;

    @FXML
    private Label lblUnpaid;

    private FileChooser fileChooser = new FileChooser();

    private PurchaseService purchaseService;

    private PurchaseReportFilterVM purchaseReportFilter;

    private static final IMessage[] PURCHASE_REPORT_FILE_COLUMNS = new IMessage[] {
            CommonLabel.LBL_INVOICE_DATE,
            CommonLabel.LBL_INVOICE_NUMBER,
            CommonLabel.LBL_SUPPLIER_NAME,
            CommonLabel.LBL_PRODUCT_NAME,
            CommonLabel.LBL_QUANTITY,
            CommonLabel.LBL_UNIT,
            CommonLabel.LBL_BUYING_PRICE,
            CommonLabel.LBL_DISCOUNT,
            CommonLabel.LBL_BUYING_PRICE_DISCOUNT,
            CommonLabel.LBL_SUBTOTAL_DISCOUNT,
            CommonLabel.LBL_SUBTOTAL_PRICE,
            CommonLabel.LBL_PAYMENT_STATUS };

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(purchaseReportFilter);
        StageUtils.modal(Page.REPORT_PURCHASE_FILTER, false, we -> {
            PurchaseReportFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            purchaseReportFilter = result;
            searchPurchaseReport();
        });
    }

    @FXML
    void onActionBtnExport(ActionEvent event) {
        Locale locale = resources.getLocale();
        ObservableList<PurchaseReportVM> report = tblPurchaseReport.getItems();
        if (report.isEmpty()) {
            return;
        }
        File file = fileChooser.showSaveDialog(getCurrentStage());
        if (file == null) {
            return;
        }
        Stage loading = displayLoading();
        CompletableFuture.runAsync(() -> {
            try {
                PurchaseGroup pg = getPurchaseReportSummary(report);
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet(t.translate(CommonLabel.LBL_PURCHASE_REPORT));
                XSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_PURCHASE_REPORT));
                row = sheet.createRow(2);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_TRANSACTION_COUNT));
                row.createCell(1).setCellValue(pg.transactionCount);
                row = sheet.createRow(3);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_EXPENSE));
                row.createCell(1).setCellValue(pg.totalPayment.doubleValue());
                row = sheet.createRow(4);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_PAID));
                row.createCell(1).setCellValue(pg.totalPaid.doubleValue());
                row = sheet.createRow(5);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_UNPAID));
                row.createCell(1).setCellValue(pg.totalUnpaid.doubleValue());
                row = sheet.createRow(7);
                for (int i = 0; i < PURCHASE_REPORT_FILE_COLUMNS.length; i++) {
                    IMessage lbl = PURCHASE_REPORT_FILE_COLUMNS[i];
                    row.createCell(i).setCellValue(t.translate(lbl));
                }
                int rowNum = 8;
                for (PurchaseReportVM vm : report) {
                    row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(dateFormatter.format(vm.getCreatedAt()));
                    row.createCell(1).setCellValue(vm.getInvoiceNumber());
                    row.createCell(2).setCellValue(vm.getSupplierName());
                    row.createCell(3).setCellValue(vm.getProductName());
                    row.createCell(4).setCellValue(vm.getQuantity());
                    row.createCell(5).setCellValue(vm.getUnit());
                    row.createCell(6).setCellValue(vm.getBuyingPrice().doubleValue());

                    XSSFCell discountCell = row.createCell(7);
                    discountCell.setCellValue(toDiscountString(vm, locale, true));
                    discountCell.setCellStyle(createRightAlignment(workbook));

                    if (vm.getBuyingPriceDiscount() != null) {
                        row.createCell(8).setCellValue(vm.getBuyingPriceDiscount().doubleValue());
                    }
                    if (vm.getSubtotalDiscount() != null) {
                        row.createCell(9).setCellValue(vm.getSubtotalDiscount().doubleValue());
                    }
                    row.createCell(10).setCellValue(vm.getSubtotalPrice().doubleValue());
                    row.createCell(11).setCellValue(
                            PaymentStatus.PAID.toString().equals(vm.getPaymentStatus()) ?
                                    t.translate(CommonLabel.LBL_PAID) : t.translate(CommonLabel.LBL_UNPAID));
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
                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                workbook.close();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).whenComplete((result, ex) -> Platform.runLater(() -> {
            loading.hide();
            if (ex != null) {
                ex.printStackTrace();
                handleException(ex);
                return;
            }
            displayInfo(String.format(t.translate(MessageCode.SUCCESS_EXPORT_PURCHASE_REPORT), file.getAbsolutePath()));
        }));
    }

    @Override
    protected void initServices() {
        purchaseService = SpringUtils.getBean(PurchaseService.class);
    }

    @Override
    protected void initControlActions() {
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colSupplierName, PurchaseReportVM::getSupplierName);
        TableViewUtils.setColumnValue(colProductName, PurchaseReportVM::getProductName);
        TableViewUtils.setColumnValue(colUnit, PurchaseReportVM::getUnit);
        TableViewUtils.setColumnValue(colInvoiceNumber, PurchaseReportVM::getInvoiceNumber);
        TableViewUtils.setColumnValue(
                colPaymentStatus,
                vm -> PaymentStatus.PAID.toString().equals(vm.getPaymentStatus()) ?
                        t.translate(CommonLabel.LBL_PAID) : t.translate(CommonLabel.LBL_UNPAID));
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                PurchaseReportVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colInvoiceDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PurchaseReportVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colBuyingPrice,
                new NumberCellFactory<>(locale),
                PurchaseReportVM::getBuyingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colDiscount,
                new DefaultCellFactory<>(),
                (vm) -> toDiscountString(vm, locale),
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colBuyingPriceDiscount,
                new NumberCellFactory<>(CommonConstants.DECIMAL_SCALE, locale),
                PurchaseReportVM::getBuyingPriceDiscount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSubtotalDiscount,
                new NumberCellFactory<>(CommonConstants.DECIMAL_SCALE, locale),
                (vm) -> vm.getDiscountType() == null ? null : vm.getSubtotalDiscount(),
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSubtotalPrice,
                new NumberCellFactory<>(CommonConstants.DECIMAL_SCALE, locale),
                PurchaseReportVM::getSubtotalPrice,
                StyleConstants.ALIGN_RIGHT);
        tblPurchaseReport.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblPurchaseReport.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileChooser.setInitialDirectory(new File(SystemConstants.USER_HOME_DIR));
        fileChooser.setInitialFileName("pospino-purchase-report.xlsx");
    }

    @Override
    protected void initControlValues() {
        purchaseReportFilter = getPageData();
        if (purchaseReportFilter == null) {
            purchaseReportFilter = new PurchaseReportFilterVM();
        }
        searchPurchaseReport();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchPurchaseReport() {
        tblPurchaseReport.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblPurchaseReport.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(
                () -> purchaseService.searchPurchaseReport(purchaseReportFilter, resources.getLocale().getLanguage()))
                .thenAccept(purchaseReport -> Platform.runLater(() -> {
                    if (purchaseReport.isEmpty()) {
                        tblPurchaseReport.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblPurchaseReport.setItems(FXCollections.observableList(purchaseReport));
                    TableViewUtils.enableSort(false, tblPurchaseReport);
                    lblRows.setText(StringNumberUtils.format(purchaseReport.size(), resources.getLocale()));
                    calculateSummary(purchaseReport);
                }));
    }

    private void calculateSummary(List<PurchaseReportVM> report) {
        lblTransactionCount.setText("-");
        lblRevenue.setText("-");
        lblPaid.setText("-");
        lblUnpaid.setText("-");
        if (report.isEmpty()) {
            return;
        }
        PurchaseGroup pg = getPurchaseReportSummary(report);
        Locale locale = resources.getLocale();
        lblTransactionCount
                .setText(StringNumberUtils.format(pg.transactionCount, locale, CommonConstants.DECIMAL_SCALE));
        lblRevenue.setText(StringNumberUtils.format(pg.totalPayment, locale, CommonConstants.DECIMAL_SCALE));
        lblPaid.setText(StringNumberUtils.format(pg.totalPaid, locale, CommonConstants.DECIMAL_SCALE));
        lblUnpaid.setText(StringNumberUtils.format(pg.totalUnpaid, locale, CommonConstants.DECIMAL_SCALE));
    }

    private PurchaseGroup getPurchaseReportSummary(List<PurchaseReportVM> report) {
        Set<String> invoices = new HashSet<>();
        PurchaseGroup sg = new PurchaseGroup();
        for (PurchaseReportVM vm : report) {
            BigDecimal totalPayment = vm.getTotalPayment();
            String invoice = vm.getInvoiceNumber();
            if (!invoices.contains(invoice)) {
                sg.totalPayment = sg.totalPayment.add(totalPayment);
                if (vm.getPaymentStatus().equals(PaymentStatus.PAID.toString())) {
                    sg.totalPaid = sg.totalPaid.add(totalPayment);
                } else {
                    sg.totalUnpaid = sg.totalUnpaid.add(totalPayment);
                }
                invoices.add(invoice);
            }
        }
        sg.transactionCount = invoices.size();
        return sg;
    }

    private class PurchaseGroup {
        private Integer transactionCount = 0;
        private BigDecimal totalPayment = BigDecimal.ZERO;
        private BigDecimal totalPaid = BigDecimal.ZERO;
        private BigDecimal totalUnpaid = BigDecimal.ZERO;
    }

    private String toDiscountString(PurchaseReportVM vm, Locale locale) {
        return toDiscountString(vm, locale, false);
    }

    private String toDiscountString(PurchaseReportVM vm, Locale locale, boolean asFloat) {
        BigDecimal discountAmount = vm.getDiscountAmount();
        if (discountAmount == null) {
            return null;
        }
        String strDiscount = StringNumberUtils.format(discountAmount, locale);
        if (DiscountType.PERCENTAGE.toString().equals(vm.getDiscountType())) {
            BigDecimal realAmount = vm.getBuyingPrice().subtract(vm.getBuyingPriceDiscount());
            String strRealAmount = asFloat ?
                    String.valueOf(realAmount.doubleValue()) : StringNumberUtils.format(realAmount, locale);
            strDiscount = String.format("%s%% = %s", strDiscount, strRealAmount);
        }
        return strDiscount;
    }

    private XSSFCellStyle createRightAlignment(XSSFWorkbook workbook) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        return cellStyle;
    }

}

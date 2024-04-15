package pinodesk.controller.report.sale;

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

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mudiatech.pandora.factory.LocalDateCellFactory;
import com.mudiatech.pandora.factory.NumberCellFactory;
import com.mudiatech.pandora.utility.IMessage;
import com.mudiatech.pandora.utility.StageUtils;
import com.mudiatech.pandora.utility.TableViewUtils;
import com.mudiatech.toolbox.data.StringNumberUtils;
import com.mudiatech.toolbox.future.AsyncUtils;

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
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.MessageCode;
import pinodesk.constant.Page;
import pinodesk.constant.PaymentStatus;
import pinodesk.constant.SellingMode;
import pinodesk.constant.StyleConstants;
import pinodesk.constant.SystemConstants;
import pinodesk.controller.BaseController;
import pinodesk.service.SaleService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.SaleReportFilterVM;
import pinodesk.viewmodel.SaleReportVM;

@Slf4j
public class SaleReportMainController extends BaseController {

    @FXML
    private Button btnFilter;

    @FXML
    private TableColumn<SaleReportVM, String> colCustomerName;

    @FXML
    private TableColumn<SaleReportVM, LocalDate> colInvoiceDate;

    @FXML
    private TableColumn<SaleReportVM, String> colInvoiceNumber;

    @FXML
    private TableColumn<SaleReportVM, String> colPaymentStatus;

    @FXML
    private TableColumn<SaleReportVM, String> colProductName;

    @FXML
    private TableColumn<SaleReportVM, Integer> colQuantity;

    @FXML
    private TableColumn<SaleReportVM, String> colSellingMode;

    @FXML
    private TableColumn<SaleReportVM, BigDecimal> colSellingPrice;

    @FXML
    private TableColumn<SaleReportVM, BigDecimal> colSubtotal;

    @FXML
    private TableColumn<SaleReportVM, String> colUnit;

    @FXML
    private TableView<SaleReportVM> tblSaleReport;

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

    private SaleService saleService;

    private SaleReportFilterVM saleReportFilter;

    private static final IMessage[] SALE_REPORT_FILE_COLUMNS = new IMessage[] {
            CommonLabel.LBL_INVOICE_DATE,
            CommonLabel.LBL_INVOICE_NUMBER,
            CommonLabel.LBL_SELLING_MODE,
            CommonLabel.LBL_CUSTOMER_NAME,
            CommonLabel.LBL_PRODUCT_NAME,
            CommonLabel.LBL_QUANTITY,
            CommonLabel.LBL_UNIT,
            CommonLabel.LBL_SELLING_PRICE,
            CommonLabel.LBL_SUBTOTAL,
            CommonLabel.LBL_PAYMENT_STATUS };

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(saleReportFilter);
        StageUtils.modal(Page.REPORT_SALE_FILTER, false, we -> {
            SaleReportFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            saleReportFilter = result;
            searchSaleReport();
        });
    }

    @FXML
    void onActionBtnExport(ActionEvent event) {
        ObservableList<SaleReportVM> report = tblSaleReport.getItems();
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
                SaleGroup sg = getSaleReportSummary(report);
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet(t.translate(CommonLabel.LBL_SALE_REPORT));
                XSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_SALE_REPORT));
                row = sheet.createRow(2);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_TRANSACTION_COUNT));
                row.createCell(1).setCellValue(sg.transactionCount);
                row = sheet.createRow(3);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_REVENUE));
                row.createCell(1).setCellValue(sg.totalPayment.doubleValue());
                row = sheet.createRow(4);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_PAID));
                row.createCell(1).setCellValue(sg.totalPaid.doubleValue());
                row = sheet.createRow(5);
                row.createCell(0).setCellValue(t.translate(CommonLabel.LBL_UNPAID));
                row.createCell(1).setCellValue(sg.totalUnpaid.doubleValue());
                row = sheet.createRow(7);
                for (int i = 0; i < SALE_REPORT_FILE_COLUMNS.length; i++) {
                    IMessage lbl = SALE_REPORT_FILE_COLUMNS[i];
                    row.createCell(i).setCellValue(t.translate(lbl));
                }
                int rowNum = 8;
                for (SaleReportVM vm : report) {
                    row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(dateFormatter.format(vm.getCreatedAt()));
                    row.createCell(1).setCellValue(vm.getInvoiceNumber());
                    row.createCell(2).setCellValue(
                            SellingMode.GENERAL.toString().equals(vm.getSellingMode()) ?
                                    t.translate(CommonLabel.LBL_GENERAL) : t.translate(CommonLabel.LBL_PRESCRIPTION));
                    row.createCell(3).setCellValue(vm.getCustomerName());
                    row.createCell(4).setCellValue(vm.getProductName());
                    row.createCell(5).setCellValue(vm.getQuantity());
                    row.createCell(6).setCellValue(vm.getUnit());
                    row.createCell(7).setCellValue(vm.getSellingPrice().doubleValue());
                    row.createCell(8).setCellValue(vm.getSubtotal().doubleValue());
                    row.createCell(9).setCellValue(
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
                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                workbook.close();
            } catch (Exception e) {
                log.error("Error on export sales", e);
                throw new CompletionException(e);
            }
        }).whenComplete((result, ex) -> Platform.runLater(() -> {
            loading.hide();
            if (ex != null) {
                ex.printStackTrace();
                handleException(ex);
                return;
            }
            displayInfo(String.format(t.translate(MessageCode.SUCCESS_EXPORT_SALE_REPORT), file.getAbsolutePath()));
        }));
    }

    @Override
    protected void initServices() {
        saleService = SpringUtils.getBean(SaleService.class);
    }

    @Override
    protected void initControlActions() {
        Locale locale = resources.getLocale();
        TableViewUtils.setColumnValue(colCustomerName, SaleReportVM::getCustomerName);
        TableViewUtils.setColumnValue(colProductName, SaleReportVM::getProductName);
        TableViewUtils.setColumnValue(colUnit, SaleReportVM::getUnit);
        TableViewUtils.setColumnValue(colInvoiceNumber, SaleReportVM::getInvoiceNumber);
        TableViewUtils.setColumnValue(
                colPaymentStatus,
                vm -> PaymentStatus.PAID.toString().equals(vm.getPaymentStatus()) ?
                        t.translate(CommonLabel.LBL_PAID) : t.translate(CommonLabel.LBL_UNPAID));
        TableViewUtils.setColumnValue(
                colSellingMode,
                vm -> SellingMode.GENERAL.toString().equals(vm.getSellingMode()) ?
                        t.translate(CommonLabel.LBL_GENERAL) : t.translate(CommonLabel.LBL_PRESCRIPTION));
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                SaleReportVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colInvoiceDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                SaleReportVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colSellingPrice,
                new NumberCellFactory<>(locale),
                SaleReportVM::getSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSubtotal,
                new NumberCellFactory<>(locale),
                SaleReportVM::getSubtotal,
                StyleConstants.ALIGN_RIGHT);
        tblSaleReport.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblSaleReport.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileChooser.setInitialDirectory(new File(SystemConstants.USER_HOME_DIR));
        fileChooser.setInitialFileName("pinodesk-sale-report.xlsx");
    }

    @Override
    protected void initControlValues() {
        saleReportFilter = getPageData();
        if (saleReportFilter == null) {
            saleReportFilter = new SaleReportFilterVM();
        }
        searchSaleReport();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchSaleReport() {
        tblSaleReport.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblSaleReport.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> saleService.searchSalesReport(saleReportFilter, resources.getLocale().getLanguage()))
                .thenAccept(saleReport -> Platform.runLater(() -> {
                    if (saleReport.isEmpty()) {
                        tblSaleReport.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblSaleReport.setItems(FXCollections.observableList(saleReport));
                    TableViewUtils.enableSort(false, tblSaleReport);
                    lblRows.setText(StringNumberUtils.format(saleReport.size(), resources.getLocale()));
                    calculateSummary(saleReport);
                }));
    }

    private void calculateSummary(List<SaleReportVM> report) {
        lblTransactionCount.setText("-");
        lblRevenue.setText("-");
        lblPaid.setText("-");
        lblUnpaid.setText("-");
        if (report.isEmpty()) {
            return;
        }
        SaleGroup sg = getSaleReportSummary(report);
        Locale locale = resources.getLocale();
        lblTransactionCount.setText(StringNumberUtils.format(sg.transactionCount, locale));
        lblRevenue.setText(StringNumberUtils.format(sg.totalPayment, locale));
        lblPaid.setText(StringNumberUtils.format(sg.totalPaid, locale));
        lblUnpaid.setText(StringNumberUtils.format(sg.totalUnpaid, locale));
    }

    private SaleGroup getSaleReportSummary(List<SaleReportVM> report) {
        Set<String> invoices = new HashSet<>();
        SaleGroup sg = new SaleGroup();
        for (SaleReportVM vm : report) {
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

    private class SaleGroup {
        private Integer transactionCount = 0;
        private BigDecimal totalPayment = BigDecimal.ZERO;
        private BigDecimal totalPaid = BigDecimal.ZERO;
        private BigDecimal totalUnpaid = BigDecimal.ZERO;
    }

}

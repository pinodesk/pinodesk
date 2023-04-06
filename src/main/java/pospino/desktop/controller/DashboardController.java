package pospino.desktop.controller;

import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.formatOrDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.gitlab.mudiasoft.pandora.factory.LocalDateCellFactory;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.model.SimpleComboBoxModel;
import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.ScrollPaneUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.service.DashboardService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.BestSellingProductVM;
import pospino.desktop.viewmodel.LowestSellingProductVM;
import pospino.desktop.viewmodel.MonthlyPurchaseTransactionVM;
import pospino.desktop.viewmodel.MonthlySaleTransactionVM;
import pospino.desktop.viewmodel.PayableClosestDueDateVM;
import pospino.desktop.viewmodel.ProductClosestExpiryVM;
import pospino.desktop.viewmodel.ProductOutOfStockVM;
import pospino.desktop.viewmodel.ReceivableClosestDueDateVM;
import pospino.desktop.viewmodel.TotalPurchaseTransactionVM;
import pospino.desktop.viewmodel.TotalSaleTransactionVM;

public class DashboardController extends BaseController {

    @FXML
    private ScrollPane dashboardScrollPane;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbYear;

    @FXML
    private Label lblAverageMonthlyExpense;

    @FXML
    private Label lblAverageMonthlyRevenue;

    @FXML
    private Label lblTotalExpense;

    @FXML
    private Label lblTotalPurchaseTrx;

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private Label lblTotalSaleTrx;

    @FXML
    private BarChart<String, Number> chartMonthlyExpenseAndRevenue;

    @FXML
    private TableView<BestSellingProductVM> tblBestSellingProducts;

    @FXML
    private TableColumn<BestSellingProductVM, String> colBestSellingProductCategory;

    @FXML
    private TableColumn<BestSellingProductVM, String> colBestSellingProductName;

    @FXML
    private TableColumn<BestSellingProductVM, Integer> colBestSellingProductSold;

    @FXML
    private TableColumn<BestSellingProductVM, String> colBestSellingProductUnit;

    @FXML
    private TableView<LowestSellingProductVM> tblLowestSellingProducts;

    @FXML
    private TableColumn<LowestSellingProductVM, String> colLowestSellingProductCategory;

    @FXML
    private TableColumn<LowestSellingProductVM, String> colLowestSellingProductName;

    @FXML
    private TableColumn<LowestSellingProductVM, Integer> colLowestSellingProductSold;

    @FXML
    private TableColumn<LowestSellingProductVM, String> colLowestSellingProductUnit;

    @FXML
    private TableView<PayableClosestDueDateVM> tblPayableClosestDueDate;

    @FXML
    private TableColumn<PayableClosestDueDateVM, LocalDate> colPayableClosestDueDateDueDate;

    @FXML
    private TableColumn<PayableClosestDueDateVM, LocalDate> colPayableClosestDueDateInvoiceDate;

    @FXML
    private TableColumn<PayableClosestDueDateVM, String> colPayableClosestDueDateInvoiceNumber;

    @FXML
    private TableColumn<PayableClosestDueDateVM, String> colPayableClosestDueDateSupplierName;

    @FXML
    private TableView<ProductClosestExpiryVM> tblProductClosestExpiry;

    @FXML
    private TableColumn<ProductClosestExpiryVM, String> colProductClosestExpiryCategory;

    @FXML
    private TableColumn<ProductClosestExpiryVM, LocalDate> colProductClosestExpiryDate;

    @FXML
    private TableColumn<ProductClosestExpiryVM, String> colProductClosestExpiryName;

    @FXML
    private TableView<ProductOutOfStockVM> tblProductOutOfStock;

    @FXML
    private TableColumn<ProductOutOfStockVM, String> colProductOutOfStockCategory;

    @FXML
    private TableColumn<ProductOutOfStockVM, String> colProductOutOfStockName;

    @FXML
    private TableColumn<ProductOutOfStockVM, Integer> colProductOutOfStockQuantity;

    @FXML
    private TableView<ReceivableClosestDueDateVM> tblReceivableClosestDueDate;

    @FXML
    private TableColumn<ReceivableClosestDueDateVM, String> colReceivableClosestDueDateCustomerName;

    @FXML
    private TableColumn<ReceivableClosestDueDateVM, LocalDate> colReceivableClosestDueDateDueDate;

    @FXML
    private TableColumn<ReceivableClosestDueDateVM, LocalDate> colReceivableClosestDueDateInvoiceDate;

    @FXML
    private TableColumn<ReceivableClosestDueDateVM, String> colReceivableClosestDueDateInvoiceNumber;

    @FXML
    private Label lblCurrentMonthExpense;

    @FXML
    private Label lblCurrentMonthRevenue;

    private DashboardService dashboardService;

    @Override
    protected void initServices() {
        dashboardService = SpringUtils.getBean(DashboardService.class);
    }

    @Override
    protected void initControlActions() {
        Locale locale = resources.getLocale();

        TableViewUtils.setColumnValue(colBestSellingProductName, BestSellingProductVM::getProductName);
        TableViewUtils.setColumnValue(colBestSellingProductCategory, BestSellingProductVM::getCategoryName);
        TableViewUtils.setColumnValue(colBestSellingProductUnit, BestSellingProductVM::getUnitLabel);
        TableViewUtils.initTableColumn(
                colBestSellingProductSold,
                new NumberCellFactory<>(locale),
                BestSellingProductVM::getSoldQuantity,
                StyleConstants.ALIGN_RIGHT);

        TableViewUtils.setColumnValue(colLowestSellingProductName, LowestSellingProductVM::getProductName);
        TableViewUtils.setColumnValue(colLowestSellingProductCategory, LowestSellingProductVM::getCategoryName);
        TableViewUtils.setColumnValue(colLowestSellingProductUnit, LowestSellingProductVM::getUnitLabel);
        TableViewUtils.initTableColumn(
                colLowestSellingProductSold,
                new NumberCellFactory<>(locale),
                LowestSellingProductVM::getSoldQuantity,
                StyleConstants.ALIGN_RIGHT);

        TableViewUtils.setColumnValue(colProductClosestExpiryName, ProductClosestExpiryVM::getProductName);
        TableViewUtils.setColumnValue(colProductClosestExpiryCategory, ProductClosestExpiryVM::getCategoryName);
        TableViewUtils.initTableColumn(
                colProductClosestExpiryDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ProductClosestExpiryVM::getExpiredDate);

        TableViewUtils.setColumnValue(colProductOutOfStockName, ProductOutOfStockVM::getProductName);
        TableViewUtils.setColumnValue(colProductOutOfStockCategory, ProductOutOfStockVM::getCategoryName);
        TableViewUtils.initTableColumn(
                colProductOutOfStockQuantity,
                new NumberCellFactory<>(locale),
                ProductOutOfStockVM::getQuantity,
                StyleConstants.ALIGN_RIGHT);

        TableViewUtils.setColumnValue(colPayableClosestDueDateSupplierName, PayableClosestDueDateVM::getSupplierName);
        TableViewUtils.setColumnValue(colPayableClosestDueDateInvoiceNumber, PayableClosestDueDateVM::getInvoiceNumber);
        TableViewUtils.initTableColumn(
                colPayableClosestDueDateInvoiceDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PayableClosestDueDateVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colPayableClosestDueDateDueDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PayableClosestDueDateVM::getDueDate);

        TableViewUtils
                .setColumnValue(colReceivableClosestDueDateCustomerName, ReceivableClosestDueDateVM::getCustomerName);
        TableViewUtils
                .setColumnValue(colReceivableClosestDueDateInvoiceNumber, ReceivableClosestDueDateVM::getInvoiceNumber);
        TableViewUtils.initTableColumn(
                colReceivableClosestDueDateInvoiceDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivableClosestDueDateVM::getInvoiceDate);
        TableViewUtils.initTableColumn(
                colReceivableClosestDueDateDueDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivableClosestDueDateVM::getDueDate);

        TableViewUtils.enableSort(false, tblBestSellingProducts);
        TableViewUtils.enableSort(false, tblLowestSellingProducts);
        TableViewUtils.enableSort(false, tblProductClosestExpiry);
        TableViewUtils.enableSort(false, tblProductOutOfStock);
        TableViewUtils.enableSort(false, tblPayableClosestDueDate);
        TableViewUtils.enableSort(false, tblReceivableClosestDueDate);

        List<SimpleComboBoxModel> years = dashboardService.getYears().stream()
                .map(y -> new SimpleComboBoxModel(y, Integer.toString(y))).toList();
        ComboBoxUtils.initSimple(cbYear, years);
        ComboBoxUtils.onSelectedItemChanged(cbYear, (ov, nv) -> {
            loadDashboard();
        });
    }

    @Override
    protected void initControlValues() {
        ComboBoxUtils.selectIndex(cbYear, 0);
        loadDashboard();
        Platform.runLater(() -> {
            ScrollPaneUtils.fixBlur(dashboardScrollPane);
        });
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void loadDashboard() {
        Integer year = ComboBoxUtils.getSelectedItem(cbYear).getValue();
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);

        Locale locale = resources.getLocale();

        // Sale
        TotalSaleTransactionVM totalSaleTransaction = dashboardService.getTotalSaleTransaction(start, end);
        lblTotalSaleTrx.setText(formatOrDefault(totalSaleTransaction.getTotalTransaction(), locale, "0"));
        lblTotalRevenue.setText(formatOrDefault(totalSaleTransaction.getTotalPayment(), locale, "0"));

        BigDecimal averageMonthlyRevenue = dashboardService.getAverageMonthlyRevenue(start, end);
        lblAverageMonthlyRevenue.setText(formatOrDefault(averageMonthlyRevenue, locale, 0, "0"));

        BigDecimal currentMonthRevenue = dashboardService.getCurrentMonthRevenue(start, end);
        lblCurrentMonthRevenue.setText(formatOrDefault(currentMonthRevenue, locale, 0, "0"));

        // Purchase
        TotalPurchaseTransactionVM totalPurchaseTransaction = dashboardService.getTotalPurchaseTransaction(start, end);
        lblTotalPurchaseTrx.setText(formatOrDefault(totalPurchaseTransaction.getTotalTransaction(), locale, "0"));
        lblTotalExpense.setText(formatOrDefault(totalPurchaseTransaction.getTotalPayment(), locale, "0"));

        BigDecimal averageMonthlyExpense = dashboardService.getAverageMonthlyExpense(start, end);
        lblAverageMonthlyExpense.setText(formatOrDefault(averageMonthlyExpense, locale, 0, "0"));

        BigDecimal currentMonthExpense = dashboardService.getCurrentMonthExpense(start, end);
        lblCurrentMonthExpense.setText(formatOrDefault(currentMonthExpense, locale, 0, "0"));

        loadMonthlyTransactionChart(locale, start, end);
        loadBestSellingProducts(locale, start, end);
        loadLowestSellingProducts(locale, start, end);
        loadProductClosestExpiries(locale);
        loadProductsOutOfStock(locale);
        loadPayableClosestDueDates(locale);
        loadReceivableClosestDueDates(locale);
    }

    private void loadMonthlyTransactionChart(Locale locale, LocalDate start, LocalDate end) {
        setVisibleInLayout(false, chartMonthlyExpenseAndRevenue);
        ObservableList<String> monthNames = FXCollections.observableArrayList();
        Calendar cal = Calendar.getInstance();
        Map<String, Integer> months = cal.getDisplayNames(Calendar.MONTH, Calendar.LONG, locale);
        Map<String, Integer> sorted = months.entrySet().stream().sorted(Entry.comparingByValue())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            monthNames.add(entry.getKey());
        }

        ((CategoryAxis) chartMonthlyExpenseAndRevenue.getXAxis()).setCategories(monthNames);

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<String, Number>();
        expenseSeries.setName(t.translate(CommonLabel.LBL_EXPENSE));
        CompletableFuture<Void> cfExpense = CompletableFuture
                .supplyAsync(() -> dashboardService.getMonthlyPurchaseTransactions(start, end))
                .thenAccept(purchases -> {
                    expenseSeries.setData(buildExpenseSeriesData(purchases, sorted));
                });

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<String, Number>();
        revenueSeries.setName(t.translate(CommonLabel.LBL_REVENUE));
        CompletableFuture<Void> cfRevenue = CompletableFuture
                .supplyAsync(() -> dashboardService.getMonthlySaleTransactions(start, end)).thenAccept(data -> {
                    revenueSeries.setData(buildRevenueSeriesData(data, sorted));
                });

        CompletableFuture.allOf(cfExpense, cfRevenue).thenRun(() -> Platform.runLater(() -> {
            ObservableList<XYChart.Series<String, Number>> seriesData = FXCollections.observableArrayList();
            seriesData.add(expenseSeries);
            seriesData.add(revenueSeries);
            chartMonthlyExpenseAndRevenue.setData(seriesData);
            for (XYChart.Series<String, Number> series : chartMonthlyExpenseAndRevenue.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    String text = String.format(
                            "%s\n%s: %s",
                            data.getXValue(),
                            series.getName(),
                            formatOrDefault(data.getYValue().doubleValue() * 1000, locale, 0, "0"));
                    Tooltip tooltip = new Tooltip(text);
                    Tooltip.install(data.getNode(), tooltip);
                }
            }
            setVisibleInLayout(true, chartMonthlyExpenseAndRevenue);
        }));
    }

    private ObservableList<XYChart.Data<String, Number>> buildExpenseSeriesData(
            List<MonthlyPurchaseTransactionVM> purchases,
            Map<String, Integer> months) {
        ObservableList<XYChart.Data<String, Number>> list = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : months.entrySet()) {
            BigDecimal totalPayment = purchases.stream().filter(vm -> vm.getMonthNumber() - 1 == entry.getValue())
                    .map(MonthlyPurchaseTransactionVM::getTotalPayment).findAny().orElse(BigDecimal.ZERO);
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    entry.getKey(),
                    totalPayment.divide(BigDecimal.valueOf(1000)));
            list.add(data);
        }
        return list;
    }

    private ObservableList<XYChart.Data<String, Number>> buildRevenueSeriesData(
            List<MonthlySaleTransactionVM> sales,
            Map<String, Integer> months) {
        ObservableList<XYChart.Data<String, Number>> list = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : months.entrySet()) {
            String monthName = entry.getKey();
            Integer monthNumber = entry.getValue();
            BigDecimal totalPayment = sales.stream().filter(vm -> vm.getMonthNumber() - 1 == monthNumber)
                    .map(MonthlySaleTransactionVM::getTotalPayment).findAny().orElse(BigDecimal.ZERO);
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    monthName,
                    totalPayment.divide(BigDecimal.valueOf(1000)));
            list.add(data);
        }
        return list;
    }

    private void loadBestSellingProducts(Locale locale, LocalDate start, LocalDate end) {
        tblBestSellingProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblBestSellingProducts.setItems(FXCollections.observableArrayList());
        CompletableFuture.supplyAsync(() -> dashboardService.getBestSellingProducts(start, end, locale.getLanguage()))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblBestSellingProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        return;
                    }
                    tblBestSellingProducts.setItems(FXCollections.observableList(list));
                }));
    }

    private void loadLowestSellingProducts(Locale locale, LocalDate start, LocalDate end) {
        tblLowestSellingProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblLowestSellingProducts.setItems(FXCollections.observableArrayList());
        CompletableFuture.supplyAsync(() -> dashboardService.getLowestSellingProducts(start, end, locale.getLanguage()))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblLowestSellingProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        return;
                    }
                    tblLowestSellingProducts.setItems(FXCollections.observableList(list));
                }));
    }

    private void loadProductClosestExpiries(Locale locale) {
        tblProductClosestExpiry.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblProductClosestExpiry.setItems(FXCollections.observableArrayList());
        CompletableFuture.supplyAsync(() -> dashboardService.getProductClosestExpiries(locale.getLanguage()))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblProductClosestExpiry.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        return;
                    }
                    tblProductClosestExpiry.setItems(FXCollections.observableList(list));
                }));
    }

    private void loadProductsOutOfStock(Locale locale) {
        tblProductOutOfStock.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblProductOutOfStock.setItems(FXCollections.observableArrayList());
        CompletableFuture.supplyAsync(() -> dashboardService.getProductsOutOfStock(locale.getLanguage()))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblProductOutOfStock.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        return;
                    }
                    tblProductOutOfStock.setItems(FXCollections.observableList(list));
                }));
    }

    private void loadPayableClosestDueDates(Locale locale) {
        tblPayableClosestDueDate.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblPayableClosestDueDate.setItems(FXCollections.observableArrayList());
        CompletableFuture.supplyAsync(() -> dashboardService.getPayableClosestDueDates(locale.getLanguage()))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblPayableClosestDueDate.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        return;
                    }
                    tblPayableClosestDueDate.setItems(FXCollections.observableList(list));
                }));
    }

    private void loadReceivableClosestDueDates(Locale locale) {
        tblReceivableClosestDueDate.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblReceivableClosestDueDate.setItems(FXCollections.observableArrayList());
        CompletableFuture.supplyAsync(() -> dashboardService.getReceivableClosestDueDates(locale.getLanguage()))
                .thenAccept(list -> Platform.runLater(() -> {
                    if (list.isEmpty()) {
                        tblReceivableClosestDueDate.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        return;
                    }
                    tblReceivableClosestDueDate.setItems(FXCollections.observableList(list));
                }));
    }

}

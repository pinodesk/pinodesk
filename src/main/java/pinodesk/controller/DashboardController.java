package pinodesk.controller;

import static com.pinodesk.toolbox.data.StringNumberUtils.formatOrDefault;

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

import com.pinodesk.pandora.factory.LocalDateCellFactory;
import com.pinodesk.pandora.factory.NumberCellFactory;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.EventUtils;
import com.pinodesk.pandora.utility.ScrollPaneUtils;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.pandora.utility.TableViewUtils;

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
import lombok.extern.slf4j.Slf4j;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.Page;
import pinodesk.constant.StyleConstants;
import pinodesk.service.DashboardService;
import pinodesk.service.PayableService;
import pinodesk.service.ProductService;
import pinodesk.service.ReceivableService;
import pinodesk.util.SpringUtils;
import pinodesk.util.TaskUtils;
import pinodesk.viewmodel.BestSellingProductVM;
import pinodesk.viewmodel.LowestSellingProductVM;
import pinodesk.viewmodel.MonthlyPurchaseTransactionVM;
import pinodesk.viewmodel.MonthlySaleTransactionVM;
import pinodesk.viewmodel.PayableClosestDueDateVM;
import pinodesk.viewmodel.PayableVM;
import pinodesk.viewmodel.ProductClosestExpiryVM;
import pinodesk.viewmodel.ProductOutOfStockVM;
import pinodesk.viewmodel.ProductVM;
import pinodesk.viewmodel.ReceivableClosestDueDateVM;
import pinodesk.viewmodel.ReceivableVM;
import pinodesk.viewmodel.TotalPurchaseTransactionVM;
import pinodesk.viewmodel.TotalSaleTransactionVM;

@Slf4j
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

    private ProductService productService;

    private PayableService payableService;

    private ReceivableService receivableService;

    @Override
    protected void initServices() {
        dashboardService = SpringUtils.getBean(DashboardService.class);
        productService = SpringUtils.getBean(ProductService.class);
        payableService = SpringUtils.getBean(PayableService.class);
        receivableService = SpringUtils.getBean(ReceivableService.class);
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

        registerKeyListenersOnTableProductClosestExpiry(locale);
        registerKeyListenersOnTableProductOutOfStock(locale);
        registerKeyListenersOnTablePayableClosestDueDate();
        registerKeyListenersOnTableReceivableClosestDueDate();

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
        loadPayableClosestDueDates();
        loadReceivableClosestDueDates();
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
                            "%s%n%s: %s",
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
        TaskUtils.runTask("Load best selling products", () -> {
            List<BestSellingProductVM> list = dashboardService.getBestSellingProducts(start, end, locale.getLanguage());
            Platform.runLater(() -> {
                if (list.isEmpty()) {
                    tblBestSellingProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    return;
                }
                tblBestSellingProducts.setItems(FXCollections.observableList(list));
            });
        });
    }

    private void loadLowestSellingProducts(Locale locale, LocalDate start, LocalDate end) {
        tblLowestSellingProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblLowestSellingProducts.setItems(FXCollections.observableArrayList());
        TaskUtils.runTask("Load lowest selling products", () -> {
            List<LowestSellingProductVM> list = dashboardService
                    .getLowestSellingProducts(start, end, locale.getLanguage());
            Platform.runLater(() -> {
                if (list.isEmpty()) {
                    tblLowestSellingProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    return;
                }
                tblLowestSellingProducts.setItems(FXCollections.observableList(list));
            });
        });
    }

    private void loadProductClosestExpiries(Locale locale) {
        tblProductClosestExpiry.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblProductClosestExpiry.setItems(FXCollections.observableArrayList());
        TaskUtils.runTask("Load product closests expiries", () -> {
            List<ProductClosestExpiryVM> list = dashboardService.getProductClosestExpiries(locale.getLanguage());
            Platform.runLater(() -> {
                if (list.isEmpty()) {
                    tblProductClosestExpiry.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    return;
                }
                tblProductClosestExpiry.setItems(FXCollections.observableList(list));
            });
        });
    }

    private void loadProductsOutOfStock(Locale locale) {
        tblProductOutOfStock.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblProductOutOfStock.setItems(FXCollections.observableArrayList());
        TaskUtils.runTask("Load products out of stock", () -> {
            List<ProductOutOfStockVM> list = dashboardService.getProductsOutOfStock(locale.getLanguage());
            Platform.runLater(() -> {
                if (list.isEmpty()) {
                    tblProductOutOfStock.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    return;
                }
                tblProductOutOfStock.setItems(FXCollections.observableList(list));
            });
        });
    }

    private void loadPayableClosestDueDates() {
        tblPayableClosestDueDate.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblPayableClosestDueDate.setItems(FXCollections.observableArrayList());
        TaskUtils.runTask("Load payable closest due dates", () -> {
            List<PayableClosestDueDateVM> list = dashboardService.getPayableClosestDueDates();
            Platform.runLater(() -> {
                if (list.isEmpty()) {
                    tblPayableClosestDueDate.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    return;
                }
                tblPayableClosestDueDate.setItems(FXCollections.observableList(list));
            });
        });
    }

    private void loadReceivableClosestDueDates() {
        tblReceivableClosestDueDate.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblReceivableClosestDueDate.setItems(FXCollections.observableArrayList());
        TaskUtils.runTask("Load receivable closest due dates", () -> {
            List<ReceivableClosestDueDateVM> list = dashboardService.getReceivableClosestDueDates();
            Platform.runLater(() -> {
                if (list.isEmpty()) {
                    tblReceivableClosestDueDate.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    return;
                }
                tblReceivableClosestDueDate.setItems(FXCollections.observableList(list));
            });
        });
    }

    private void registerKeyListenersOnTableProductClosestExpiry(Locale locale) {
        tblProductClosestExpiry.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableProductClosestExpiry(locale);
            }
        });
        tblProductClosestExpiry.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableProductClosestExpiry(locale);
            }
        });
    }

    private void registerKeyListenersOnTableProductOutOfStock(Locale locale) {
        tblProductOutOfStock.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableProductOutOfStock(locale);
            }
        });
        tblProductOutOfStock.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableProductOutOfStock(locale);
            }
        });
    }

    private void handleActionTableProductClosestExpiry(Locale locale) {
        if (TableViewUtils.hasItemSelected(tblProductClosestExpiry)) {
            ProductClosestExpiryVM product = TableViewUtils.getSelectedItem(tblProductClosestExpiry);
            handleEditProduct(locale, product.getProductId());
        }
    }

    private void handleActionTableProductOutOfStock(Locale locale) {
        if (TableViewUtils.hasItemSelected(tblProductOutOfStock)) {
            ProductOutOfStockVM product = TableViewUtils.getSelectedItem(tblProductOutOfStock);
            handleEditProduct(locale, product.getProductId());
        }
    }

    private void handleEditProduct(Locale locale, Long productId) {
        TaskUtils.runTask("Load product by id", () -> {
            ProductVM p = productService.getProductById(productId);
            setPageData(p);
            Platform.runLater(() -> {
                StageUtils.modal(Page.CATALOG_PRODUCT_EDIT, event -> {
                    getPageData();
                    loadProductsOutOfStock(locale);
                    loadProductClosestExpiries(locale);
                });
            });
        }, throwable -> Platform.runLater(() -> {
            handleException(throwable);
        }));
    }

    private void registerKeyListenersOnTablePayableClosestDueDate() {
        tblPayableClosestDueDate.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTablePayableClosestDueDate();
            }
        });
        tblPayableClosestDueDate.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTablePayableClosestDueDate();
            }
        });
    }

    private void handleActionTablePayableClosestDueDate() {
        if (TableViewUtils.hasItemSelected(tblPayableClosestDueDate)) {
            PayableClosestDueDateVM payable = TableViewUtils.getSelectedItem(tblPayableClosestDueDate);
            handleEditPayable(payable.getPayableId());
        }
    }

    private void handleEditPayable(Long payableId) {
        TaskUtils.runTask("Load payable by id", () -> {
            PayableVM p = payableService.getPayableById(payableId);
            setPageData(p);
            Platform.runLater(() -> {
                StageUtils.modal(Page.TRANSACTION_PAYABLE_EDIT, event -> {
                    getPageData();
                    loadPayableClosestDueDates();
                });
            });
        }, throwable -> Platform.runLater(() -> {
            handleException(throwable);
        }));
    }

    private void registerKeyListenersOnTableReceivableClosestDueDate() {
        tblReceivableClosestDueDate.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableReceivableClosestDueDate();
            }
        });
        tblReceivableClosestDueDate.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableReceivableClosestDueDate();
            }
        });
    }

    private void handleActionTableReceivableClosestDueDate() {
        if (TableViewUtils.hasItemSelected(tblReceivableClosestDueDate)) {
            ReceivableClosestDueDateVM receivable = TableViewUtils.getSelectedItem(tblReceivableClosestDueDate);
            handleEditReceivable(receivable.getReceivableId());
        }
    }

    private void handleEditReceivable(Long receivableId) {
        TaskUtils.runTask("Load receivable by id", () -> {
            ReceivableVM r = receivableService.getReceivableById(receivableId);
            setPageData(r);
            Platform.runLater(() -> {
                StageUtils.modal(Page.TRANSACTION_RECEIVABLE_EDIT, event -> {
                    getPageData();
                    loadReceivableClosestDueDates();
                });
            });
        }, throwable -> Platform.runLater(() -> {
            handleException(throwable);
        }));
    }

}

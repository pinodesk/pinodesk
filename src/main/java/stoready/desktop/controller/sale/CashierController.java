package stoready.desktop.controller.sale;

import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.formatOrDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;
import com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.CommonLabel;
import stoready.desktop.constant.ConfigurationConstants;
import stoready.desktop.constant.MessageCode;
import stoready.desktop.constant.Page;
import stoready.desktop.constant.SellingMode;
import stoready.desktop.constant.StyleConstants;
import stoready.desktop.controller.CommonContentPaneController;
import stoready.desktop.controller.sale.CashierPayController.PaymentData;
import stoready.desktop.properties.ApplicationProperties;
import stoready.desktop.service.ConfigurationService;
import stoready.desktop.service.ProductService;
import stoready.desktop.util.SpringUtils;
import stoready.desktop.viewmodel.ChooseResultVM;
import stoready.desktop.viewmodel.CurrentSessionVM;
import stoready.desktop.viewmodel.CustomerVM;
import stoready.desktop.viewmodel.GroupedProductExpiryVM;
import stoready.desktop.viewmodel.ProductVM;
import stoready.desktop.viewmodel.SaleProductVM;

@Slf4j
public class CashierController extends CommonContentPaneController {

    @FXML
    private Label lblUser;

    @FXML
    private Label lblUserGroup;

    @FXML
    private Label lblStoreName;

    @FXML
    private Label lblVersion;

    @FXML
    private TextField tfProduct;

    @FXML
    private TextField tfQuantity;

    @FXML
    private Button btnAddProduct;

    @FXML
    private Label lblTotal;

    @FXML
    private TableView<SaleProductVM> tblSaleProducts;

    @FXML
    private TableColumn<SaleProductVM, String> colProductName;

    @FXML
    private TableColumn<SaleProductVM, String> colProductCategory;

    @FXML
    private TableColumn<SaleProductVM, String> colUnit;

    @FXML
    private TableColumn<SaleProductVM, Integer> colQuantity;

    @FXML
    private TableColumn<SaleProductVM, BigDecimal> colSellingPrice;

    @FXML
    private TableColumn<SaleProductVM, BigDecimal> colSubtotal;

    @FXML
    private TableColumn<SaleProductVM, LocalDate> colExpiredDate;

    @FXML
    private Label lblSellingMode;

    @FXML
    private Label lblCustomer;

    @FXML
    private Label lblTotalProduct;

    @FXML
    private Label lblTotalSale;

    @FXML
    private Button btnCustomer;

    @FXML
    private Button btnPay;

    @FXML
    private RadioButton rbGeneral;

    @FXML
    private RadioButton rbPrescription;

    @FXML
    ToggleGroup toggleSellingMode;

    private ProductVM selectedProduct;
    private CustomerVM selectedCustomer;
    private Integer totalProduct;
    private BigDecimal totalSale;

    private ProductService productService;
    private ConfigurationService configurationService;
    private ApplicationProperties applicationProperties;

    @FXML
    void onActionBtnAddProduct(ActionEvent event) {
        if (selectedProduct != null) {
            handleAddProduct(selectedProduct);
            return;
        }
        String keyword = tfProduct.getText();
        if (StringUtils.isNotBlank(keyword)) {
            Optional<ProductVM> op = productService.searchProductByCode(keyword);
            if (op.isPresent()) {
                handleAddProduct(op.get());
                return;
            }
            setPageData(keyword);
        }
        setFocused(tfQuantity);
        StageUtils.modal(Page.MASTER_PRODUCT_CHOOSE, false, we -> {
            ChooseResultVM<ProductVM> result = getPageData();
            handleSelectedProduct(result);
        });
    }

    @FXML
    void onActionBtnCustomer(ActionEvent event) {
        StageUtils.modal(Page.MASTER_CUSTOMER_CHOOSE, false, we -> {
            ChooseResultVM<CustomerVM> result = getPageData();
            handleSelectedCustomer(result);
        });
        setFocused(btnPay);
    }

    @FXML
    void onActionBtnPay(ActionEvent event) {
        List<SaleProductVM> saleProducts = tblSaleProducts.getItems();
        if (saleProducts.isEmpty()) {
            return;
        }
        SaleData saleData = new SaleData();
        saleData.setCustomer(Optional.ofNullable(selectedCustomer));
        saleData.setSaleProducts(saleProducts);
        saleData.setSellingMode(getSelectedSellingMode());
        saleData.setTotalProduct(totalProduct);
        saleData.setTotalSale(totalSale);
        setPageData(saleData);
        StageUtils.modal(Page.TRANSACTION_SALE_CASHIER_PAY, false, we -> {
            PaymentData paymentData = getPageData();
            if (paymentData == null) {
                return;
            }
            printReceipt(saleData, paymentData, false);
            Runnable printFn = () -> printReceipt(saleData, paymentData, true);
            setPageData(List.of(saleData, paymentData, printFn));
            StageUtils.modal(Page.TRANSACTION_SALE_CASHIER_SALE_COMPLETE, false);
            reset();
            toggleSellingMode.selectToggle(rbGeneral);
            tblSaleProducts.setItems(FXCollections.observableArrayList());
            handleSelectedSellingMode(rbGeneral);
        });
    }

    @FXML
    void onActionTfProduct(ActionEvent event) {
        btnAddProduct.fire();
    }

    @FXML
    void onActionTfQuantity(ActionEvent event) {
        btnAddProduct.fire();
    }

    @FXML
    protected void onActionBtnCancel(ActionEvent event) {
        close();
    }

    @Override
    protected void initContentPaneControlActions() {
        Locale locale = resources.getLocale();
        toggleSellingMode.selectedToggleProperty()
                .addListener((o, ov, nv) -> handleSelectedSellingMode((RadioButton) nv));
        TextFieldUtils.setDigitTextFields(tfQuantity);
        TableViewUtils.setColumnValue(colProductName, SaleProductVM::getProductName);
        TableViewUtils.setColumnValue(colUnit, SaleProductVM::getProductUnitLabel);
        TableViewUtils.setColumnValue(colProductCategory, SaleProductVM::getProductCategoryName);
        TableViewUtils.initTableColumn(
                colSellingPrice,
                new NumberCellFactory<>(locale),
                SaleProductVM::getSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                SaleProductVM::getSaleQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSubtotal,
                new NumberCellFactory<>(locale),
                SaleProductVM::getSubtotal,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colExpiredDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                SaleProductVM::getExpiredDate);
        tblSaleProducts.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblSaleProducts.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableSaleProduct();
            }
        });
        tblSaleProducts.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTableSaleProduct();
            }
        });
        setFocused(tfProduct);
    }

    @Override
    protected void initControlValues() {
        lblVersion.setText(String.format("%s %s", CommonConstants.APP_TITLE, applicationProperties.getVersion()));
        CurrentSessionVM currentSession = sessionService.get().getCurrentSession();
        String storeName = configurationService.getConfiguration(ConfigurationConstants.STORE_NAME);
        lblStoreName.setText(storeName);
        lblUser.setText(currentSession.getUser().getFullName());
        lblUserGroup.setText(currentSession.getUserGroup().getName());
        RadioButton selectedSellingMode = (RadioButton) toggleSellingMode.getSelectedToggle();
        handleSelectedSellingMode(selectedSellingMode);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        productService = SpringUtils.getBean(ProductService.class);
        configurationService = SpringUtils.getBean(ConfigurationService.class);
        applicationProperties = SpringUtils.getBean(ApplicationProperties.class);
    }

    private void handleSelectedSellingMode(RadioButton rb) {
        if (rbGeneral.equals(rb)) {
            lblSellingMode.setText(t.translate(CommonLabel.LBL_GENERAL));
            tblSaleProducts.getItems().forEach(item -> {
                BigDecimal sellingPrice = item.getGeneralSellingPrice();
                item.setSellingPrice(sellingPrice);
                item.setSubtotal(sellingPrice.multiply(BigDecimal.valueOf(item.getSaleQuantity())));
            });
        } else if (rbPrescription.equals(rb)) {
            lblSellingMode.setText(t.translate(CommonLabel.LBL_PRESCRIPTION));
            tblSaleProducts.getItems().forEach(item -> {
                BigDecimal prescriptionPrice = item.getPrescriptionSellingPrice();
                BigDecimal sellingPrice = prescriptionPrice == null ? item.getGeneralSellingPrice() : prescriptionPrice;
                item.setSellingPrice(sellingPrice);
                item.setSubtotal(sellingPrice.multiply(BigDecimal.valueOf(item.getSaleQuantity())));
            });
        }
        tblSaleProducts.refresh();
        calculateSaleSummary();
    }

    public void handleSelectedProduct(ChooseResultVM<ProductVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(product -> {
            selectedProduct = product;
            tfProduct.setText(product.getName());
        }, () -> {
            selectedProduct = null;
            tfProduct.setText("");
        });
    }

    public void handleSelectedCustomer(ChooseResultVM<CustomerVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(customer -> {
            selectedCustomer = customer;
            lblCustomer.setText(customer.getName());
        }, () -> {
            selectedCustomer = null;
            lblCustomer.setText("-");
        });
    }

    private void handleActionTableSaleProduct() {
        if (!TableViewUtils.hasItemSelected(tblSaleProducts)) {
            return;
        }
        int selectedIdx = TableViewUtils.getSelectedIndex(tblSaleProducts);
        SaleProductVM selected = tblSaleProducts.getItems().get(selectedIdx);
        List<GroupedProductExpiryVM> productExpiries = productService
                .getRemainingProductExpiry(selected.getProductId());
        ConfirmProduct confirmProduct = prepareConfirmProduct(null, selected, null, productExpiries);
        setPageData(confirmProduct);
        StageUtils.modal(Page.TRANSACTION_SALE_CASHIER_CONFIRM_PRODUCT, false, we -> {
            ConfirmProduct result = getPageData();
            if (result == null) {
                return;
            }
            SaleProductVM sp = result.getSaleProduct();
            if (sp == null) {
                return;
            }
            tblSaleProducts.getItems().remove(selectedIdx);
            if (result.isDelete()) {
                // Stop when the product is marked to delete
                return;
            }
            int idx = getProductIndexInTable(sp.getProductId(), sp.getExpiredDate(), tblSaleProducts);
            if (idx != -1) {
                tblSaleProducts.getItems().remove(idx);
            }
            tblSaleProducts.getItems().add(sp);
            tblSaleProducts.refresh();
            calculateSaleSummary();
        });
    }

    public void handleAddProduct(ProductVM product) {
        // Show product confirmation page if price, quantity, or expiry date is empty
        int saleQty = StringNumberUtils.toIntegerOrDefault(tfQuantity.getText(), 1);
        List<GroupedProductExpiryVM> productExpiries = productService.getRemainingProductExpiry(product.getId());
        if (isRequiredToConfirmProduct(product, productExpiries)) {
            ConfirmProduct confirmProduct = prepareConfirmProduct(product, null, saleQty, productExpiries);
            setPageData(confirmProduct);
            StageUtils.modal(Page.TRANSACTION_SALE_CASHIER_CONFIRM_PRODUCT, false, we -> {
                ConfirmProduct result = getPageData();
                if (result == null) {
                    selectedProduct = null;
                    setFocused(tfProduct);
                    return;
                }
                SaleProductVM sp = result.getSaleProduct();
                if (sp == null) {
                    selectedProduct = null;
                    setFocused(tfProduct);
                    return;
                }
                int idx = getProductIndexInTable(sp.getProductId(), sp.getExpiredDate(), tblSaleProducts);
                if (idx != -1) {
                    SaleProductVM removed = tblSaleProducts.getItems().remove(idx);
                    int newQty = sp.getSaleQuantity() + removed.getSaleQuantity();
                    sp.setSaleQuantity(newQty);
                    sp.setSubtotal(sp.getSellingPrice().multiply(new BigDecimal(newQty)));
                }
                tblSaleProducts.getItems().add(sp);
                calculateSaleSummary();
                reset();
            });
            return;
        }
        SaleProductVM saleProduct = new SaleProductVM();
        saleProduct.setProductId(product.getId());
        saleProduct.setProductName(product.getName());
        saleProduct.setProductCode(product.getCode());
        saleProduct.setProductBarcode(product.getBarcode());
        saleProduct.setProductCategoryCode(product.getCategoryCode());
        saleProduct.setProductCategoryName(product.getCategoryName());
        saleProduct.setProductUnitLabel(product.getUnitLabel());
        saleProduct.setCurrentQuantity(product.getQuantity());
        saleProduct.setGeneralSellingPrice(product.getGeneralSellingPrice());
        saleProduct.setPrescriptionSellingPrice(product.getPrescriptionSellingPrice());
        BigDecimal sellingPrice = product.getGeneralSellingPrice();
        SellingMode sellingMode = getSelectedSellingMode();
        if (SellingMode.PRESCRIPTION.equals(sellingMode) && !isNullOrZero(product.getPrescriptionSellingPrice())) {
            sellingPrice = product.getPrescriptionSellingPrice();
        }
        int idx = getProductIndexInTable(product.getId(), null, tblSaleProducts);
        if (idx != -1) {
            SaleProductVM removed = tblSaleProducts.getItems().remove(idx);
            saleQty = removed.getSaleQuantity() + saleQty;
            // If the same product already exists in the table, then the quantity and the
            // price should use the last value set to it for the currently selected product
            // might have empty or zero quantity or price.
            sellingPrice = removed.getSellingPrice();
            saleProduct.setCurrentQuantity(removed.getCurrentQuantity());
            saleProduct.setGeneralSellingPrice(removed.getGeneralSellingPrice());
            saleProduct.setPrescriptionSellingPrice(removed.getPrescriptionSellingPrice());
        }
        saleProduct.setSaleQuantity(saleQty);
        saleProduct.setSellingPrice(sellingPrice);
        saleProduct.setSubtotal(sellingPrice.multiply(BigDecimal.valueOf(saleQty)));
        tblSaleProducts.getItems().add(saleProduct);
        calculateSaleSummary();
        reset();
    }

    /**
     * Returns true if the product has properties or conditions that need to be
     * confirmed, such as: 1. The product has expiration date(s). 2. The product
     * quantity is empty or zero. 3. The product price is empty or zero.
     * 
     * @param product
     * @param productExpiries
     * 
     * @return
     */
    private boolean isRequiredToConfirmProduct(ProductVM product, List<GroupedProductExpiryVM> productExpiries) {
        boolean emptyQtyOrPrice = isNullOrZero(product.getQuantity()) || isNullOrZero(product.getGeneralSellingPrice());
        boolean notAdded = getProductIndexInTable(product.getId(), null, tblSaleProducts) == -1;
        return !productExpiries.isEmpty() || (emptyQtyOrPrice && notAdded);
    }

    @SuppressWarnings("null")
    private void calculateSaleSummary() {
        Locale locale = resources.getLocale();
        ObservableList<SaleProductVM> items = tblSaleProducts.getItems();
        totalProduct = items.stream().map(SaleProductVM::getSaleQuantity).reduce(0, Integer::sum);
        totalSale = items.stream().map(SaleProductVM::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotalProduct.setText(formatOrDefault(totalProduct, locale, "0"));
        lblTotalSale.setText(formatOrDefault(totalSale, locale, "0"));
        lblTotal.setText(formatOrDefault(totalSale, locale, "0"));
    }

    private SellingMode getSelectedSellingMode() {
        RadioButton selectedSellingMode = (RadioButton) toggleSellingMode.getSelectedToggle();
        return selectedSellingMode.equals(rbGeneral) ? SellingMode.GENERAL : SellingMode.PRESCRIPTION;
    }

    private int getProductIndexInTable(Long productId, LocalDate expiredDate, TableView<SaleProductVM> table) {
        Predicate<SaleProductVM> productExists = item -> {
            boolean equalsProductId = item.getProductId().equals(productId);
            boolean equalsExpiredDate = expiredDate == null ?
                    item.getExpiredDate() == null : expiredDate.equals(item.getExpiredDate());
            return equalsProductId && equalsExpiredDate;
        };
        return TableViewUtils.getItemIndex(productExists, table);
    }

    private void reset() {
        tfProduct.setText("");
        tfQuantity.setText("1");
        selectedProduct = null;
        setFocused(tfProduct);
    }

    // https://examples.javacodegeeks.com/desktop-java/javafx/javafx-print-api/
    // https://stackoverflow.com/questions/38470568/javafx-doesnt-detect-changes-of-available-printers
    private void printReceipt(SaleData saleData, PaymentData paymentData, boolean isCopy) {
        String printerName = configurationService.getConfiguration(ConfigurationConstants.PRINTER_NAME);
        if (StringUtils.isBlank(printerName)) {
            log.debug("Printer name is empty");
            return;
        }
        Optional<Printer> printer = Printer.getAllPrinters().stream().filter(p -> p.getName().equals(printerName))
                .findAny();
        if (printer.isEmpty()) {
            displayWarning(MessageCode.ERROR_PRINTER_NOT_FOUND);
            return;
        }
        PrinterJob job = PrinterJob.createPrinterJob(printer.get());
        if (job == null) {
            displayWarning(MessageCode.ERROR_PRINTER_UNAVAILABLE);
            return;
        }
        job.getJobSettings().setJobName("Stoready Print Job");
        PageLayout pl = job.getJobSettings().getPageLayout();
        PageLayout pageLayout = job.getPrinter().createPageLayout(pl.getPaper(), pl.getPageOrientation(), 0, 0, 0, 0);
        double scale = 0.6;
        Node node = prepareReceipt(saleData, paymentData, isCopy);
        node.getTransforms().add(new Scale(scale, scale));
        boolean printed = job.printPage(pageLayout, node);
        if (printed) {
            job.endJob();
        }
    }

    public Node prepareReceipt(SaleData saleData, PaymentData paymentData, boolean isCopy) {
        Locale locale = resources.getLocale();
        Map<String, String> config = configurationService.getConfigurationMap();

        String sep = "--------------------------------------------------";

        Label lblStoreName = new Label(config.get(ConfigurationConstants.STORE_NAME));
        lblStoreName.setWrapText(true);
        lblStoreName.setTextAlignment(TextAlignment.CENTER);

        Label lblStoreAddress = new Label(config.get(ConfigurationConstants.STORE_ADDRESS));
        lblStoreAddress.setTextAlignment(TextAlignment.CENTER);
        lblStoreAddress.setWrapText(true);

        Label lblFooter = new Label(config.get(ConfigurationConstants.PRINTER_FOOTER));
        lblFooter.setTextAlignment(TextAlignment.CENTER);

        Label lblPowered = new Label(t.translate(CommonLabel.LBL_POWERED_BY_STOREADY));
        lblPowered.setTextAlignment(TextAlignment.CENTER);

        Label lblWww = new Label(t.translate(CommonLabel.LBL_WWW_STOREADY));
        lblWww.setTextAlignment(TextAlignment.CENTER);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(5d);
        vbox.setMaxWidth(240);
        if (isCopy) {
            Label lblCopyReceipt = new Label("--- " + t.translate(CommonLabel.LBL_COPY_RECEIPT) + " ---");
            lblCopyReceipt.setTextAlignment(TextAlignment.CENTER);
            vbox.getChildren().add(lblCopyReceipt);
            vbox.getChildren().add(new Label());
        }
        vbox.getChildren().addAll(
                lblStoreName,
                lblStoreAddress,
                new Label(sep),
                topGridPane(paymentData, locale),
                new Label(sep),
                mainGridPane(saleData, locale),
                new Label(sep),
                bottomGridPane(saleData, paymentData, locale),
                new Label(sep),
                lblFooter,
                new Label(),
                lblPowered,
                lblWww,
                new Label(),
                new Label());
        return vbox;
    }

    private GridPane bottomGridPane(SaleData saleData, PaymentData paymentData, Locale locale) {
        GridPane gp = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(10);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(50);
        gp.getColumnConstraints().addAll(col1, col2, col3);
        gp.setHgap(5);
        Label lblTotalProduct = new Label(formatOrDefault(saleData.getTotalProduct(), locale, "0"));
        GridPane.setHalignment(lblTotalProduct, HPos.RIGHT);
        Label lblTotalSale = new Label(formatOrDefault(saleData.getTotalSale(), locale, "0"));
        GridPane.setHalignment(lblTotalSale, HPos.RIGHT);
        Label lblPaymentAmount = new Label(formatOrDefault(paymentData.getPaymentAmount(), locale, "0"));
        GridPane.setHalignment(lblPaymentAmount, HPos.RIGHT);
        Label lblChangeAmount = new Label(formatOrDefault(paymentData.getChangeAmount(), locale, "0"));
        GridPane.setHalignment(lblChangeAmount, HPos.RIGHT);
        VBox.setMargin(gp, new Insets(0, 15, 0, 0));
        gp.add(new Label(t.translate(CommonLabel.LBL_TOTAL)), 0, 0);
        gp.add(new Label(":"), 1, 0);
        gp.add(lblTotalSale, 2, 0);
        gp.add(new Label(t.translate(CommonLabel.LBL_PAY)), 0, 1);
        gp.add(new Label(":"), 1, 1);
        gp.add(lblPaymentAmount, 2, 1);
        gp.add(new Label(t.translate(CommonLabel.LBL_CHANGE)), 0, 2);
        gp.add(new Label(":"), 1, 2);
        gp.add(lblChangeAmount, 2, 2);
        return gp;
    }

    private GridPane mainGridPane(SaleData saleData, Locale locale) {
        GridPane gp = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(50);
        gp.getColumnConstraints().addAll(col1, col2, col3);
        gp.setHgap(5);
        VBox.setMargin(gp, new Insets(0, 15, 0, 0));
        int row = 0;
        List<SaleProductVM> saleProducts = new ArrayList<>();
        saleData.getSaleProducts().forEach(sp -> {
            boolean found = false;
            for (SaleProductVM sp1 : saleProducts) {
                if (sp1.getProductId().equals(sp.getProductId())) {
                    sp1.setSaleQuantity(sp1.getSaleQuantity() + sp.getSaleQuantity());
                    sp1.setSubtotal(sp1.getSubtotal().add(sp.getSubtotal()));
                    found = true;
                    break;
                }
            }
            if (!found) {
                saleProducts.add(sp);
            }
        });
        for (SaleProductVM sp : saleProducts) {
            Label prod = new Label(sp.getProductName());
            Label qty = new Label(sp.getSaleQuantity() + " x");
            Label price = new Label(formatOrDefault(sp.getSellingPrice(), locale, "0"));
            Label total = new Label(formatOrDefault(sp.getSubtotal(), locale, "0"));
            GridPane.setColumnSpan(prod, 3);
            GridPane.setHalignment(total, HPos.RIGHT);
            gp.add(prod, 0, row);
            row++;
            gp.add(qty, 0, row);
            gp.add(price, 1, row);
            gp.add(total, 2, row);
            row++;
        }
        return gp;
    }

    private GridPane topGridPane(PaymentData paymentData, Locale locale) {
        GridPane gp = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(10);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(50);
        gp.getColumnConstraints().addAll(col1, col2, col3);
        gp.setHgap(5);
        String date = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", locale).format(LocalDateTime.now());
        Label lblDate = new Label(date);
        GridPane.setHalignment(lblDate, HPos.RIGHT);
        Label lblInvoiceNumber = new Label(paymentData.getInvoiceNumber());
        GridPane.setHalignment(lblInvoiceNumber, HPos.RIGHT);
        VBox.setMargin(gp, new Insets(0, 15, 0, 0));
        gp.add(new Label(t.translate(CommonLabel.LBL_DATE)), 0, 0);
        gp.add(new Label(":"), 1, 0);
        gp.add(lblDate, 2, 0);
        gp.add(new Label(t.translate(CommonLabel.LBL_INVOICE_NUMBER)), 0, 1);
        gp.add(new Label(":"), 1, 1);
        gp.add(lblInvoiceNumber, 2, 1);
        return gp;
    }

    private ConfirmProduct prepareConfirmProduct(
            ProductVM product,
            SaleProductVM saleProduct,
            Integer saleQty,
            List<GroupedProductExpiryVM> productExpiries) {
        ConfirmProduct cp = new ConfirmProduct();
        cp.setProduct(product);
        cp.setSaleProduct(saleProduct);
        cp.setSaleQuantity(saleQty);
        cp.setEdit(saleProduct != null);
        cp.setProductExpiries(productExpiries);
        cp.setCurrentSaleProducts(tblSaleProducts.getItems());
        cp.setSellingMode(getSelectedSellingMode());
        return cp;
    }

    @Data
    class ConfirmProduct {
        private ProductVM product;
        private SaleProductVM saleProduct;
        private Integer saleQuantity;
        private List<GroupedProductExpiryVM> productExpiries;
        private SellingMode sellingMode;
        private List<SaleProductVM> currentSaleProducts;
        private boolean isEdit = false;
        private boolean isDelete = false;
    }

    @Data
    class SaleData {
        private List<SaleProductVM> saleProducts;
        private Optional<CustomerVM> customer;
        private SellingMode sellingMode;
        private Integer totalProduct;
        private BigDecimal totalSale;
    }

}

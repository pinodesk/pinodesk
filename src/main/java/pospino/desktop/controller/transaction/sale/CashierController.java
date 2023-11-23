package pospino.desktop.controller.transaction.sale;

import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.formatOrDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.mudiasoft.pandora.factory.LocalDateCellFactory;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;
import com.gitlab.mudiasoft.toolbox.data.StringNumberUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import lombok.Data;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.SellingMode;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.CommonContentPaneController;
import pospino.desktop.properties.ApplicationProperties;
import pospino.desktop.service.ConfigurationService;
import pospino.desktop.service.ProductService;
import pospino.desktop.util.ProductUtils;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.CurrentSessionVM;
import pospino.desktop.viewmodel.CustomerVM;
import pospino.desktop.viewmodel.GroupedProductExpiryVM;
import pospino.desktop.viewmodel.PackageProductVM;
import pospino.desktop.viewmodel.PaymentDataVM;
import pospino.desktop.viewmodel.ProductVM;
import pospino.desktop.viewmodel.SaleDataVM;
import pospino.desktop.viewmodel.SaleProductVM;

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
    private Button btnCancel;

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
        StageUtils.modal(Page.CATALOG_PRODUCT_CHOOSE, false, we -> {
            ChooseResultVM<ProductVM> result = getPageData();
            handleSelectedProduct(result);
        });
    }

    @FXML
    void onActionBtnCustomer(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_CUSTOMER_CHOOSE, false, we -> {
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
        SaleDataVM saleData = new SaleDataVM();
        saleData.setCustomer(Optional.ofNullable(selectedCustomer));
        saleData.setSaleProducts(saleProducts);
        saleData.setSellingMode(getSelectedSellingMode());
        saleData.setTotalProduct(totalProduct);
        saleData.setTotalSale(totalSale);
        setPageData(saleData);
        StageUtils.modal(Page.TRANSACTION_SALE_CASHIER_PAY, false, we -> {
            PaymentDataVM paymentData = getPageData();
            if (paymentData == null) {
                return;
            }
            setPageData(List.of(saleData, paymentData));
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
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                if (btnCustomer.isFocused()) {
                    btnCustomer.fire();
                    return;
                }
                if (btnPay.isFocused()) {
                    btnPay.fire();
                    return;
                }
                if (btnCancel.isFocused()) {
                    btnCancel.fire();
                    return;
                }
            }
        });
    }

    @Override
    protected void initControlValues() {
        lblVersion.setText(String.format("%s %s", CommonConstants.APP_TITLE, applicationProperties.getAppVersion()));
        CurrentSessionVM currentSession = sessionService.getCurrentSession();
        String storeName = configurationService.getConfiguration(ConfigurationConstants.STORE_NAME);
        lblStoreName.setText(storeName);
        lblUser.setText(currentSession.getUser().getFullName());
        lblUserGroup.setText(currentSession.getUserGroup().getName());
        RadioButton selectedSellingMode = (RadioButton) toggleSellingMode.getSelectedToggle();
        handleSelectedSellingMode(selectedSellingMode);
        if (!isPharmacyFeatureEnabled()) {
            setVisibleInLayout(false, rbPrescription);
        }
    }

    @Override
    protected void initServices() {
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
                calculateSaleSummary();
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

    private void showConfirmationPage(ProductVM product, int saleQty, List<GroupedProductExpiryVM> productExpiries) {
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
    }

    private String validateCustomPackageProduct(ProductVM product, int saleQty) {
        List<PackageProductVM> packageProducts = productService.getPackageProductsByProductId(product.getId());
        for (PackageProductVM pp : packageProducts) {
            int expectedQty = pp.getQuantityInPackage() * saleQty;
            if (pp.getQuantity() < expectedQty) {
                return String.format(
                        t.translate(MessageCode.ERROR_INSUFFICIENT_PACKAGE_QUANTITY),
                        pp.getName(),
                        expectedQty,
                        pp.getQuantity());
            }
            int idx = getProductIndexInTable(pp.getId(), null, tblSaleProducts);
            if (idx != -1) {
                SaleProductVM sp = tblSaleProducts.getItems().get(idx);
                int availableQty = sp.getCurrentQuantity() - sp.getSaleQuantity();
                if (availableQty < expectedQty) {
                    return String.format(
                            t.translate(MessageCode.ERROR_INSUFFICIENT_PACKAGE_QUANTITY),
                            pp.getName(),
                            expectedQty,
                            availableQty);
                }
            }
        }
        return null;
    }

    public void handleAddProduct(ProductVM product) {
        int saleQty = StringNumberUtils.toIntegerOrDefault(tfQuantity.getText(), 1);
        List<GroupedProductExpiryVM> productExpiries = new ArrayList<>();
        if (ProductUtils.isProductCategoryCustomPackage(product.getCategoryCode())) {
            String errorMessage = validateCustomPackageProduct(product, saleQty);
            if (errorMessage != null) {
                displayError(errorMessage);
                selectedProduct = null;
                setFocused(tfProduct);
                return;
            }
            // Validate package must have available products
            PackageProductVM lowestQtyProduct = productService.getLowestQuantityPackageProduct(product.getId());
            int lowestQty = lowestQtyProduct.getQuantity();
            int idx = getProductIndexInTable(lowestQtyProduct.getId(), null, tblSaleProducts);
            if (idx != -1) {
                SaleProductVM sp = tblSaleProducts.getItems().get(idx);
                lowestQty = lowestQty - sp.getSaleQuantity();
            }
            if (lowestQty < saleQty) {
                displayError(MessageCode.ERROR_INSUFFICIENT_PACKAGE_QUANTITY);
                selectedProduct = null;
                setFocused(tfProduct);
                return;
            }
            if (isNullOrZero(product.getQuantity())) {
                product.setQuantity(lowestQty);
            }
        } else {
            productExpiries = productService.getRemainingProductExpiry(product.getId());
        }
        if (isRequiredToConfirmProduct(product, productExpiries)) {
            showConfirmationPage(product, saleQty, productExpiries);
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

}

package toscabox.desktop.controller.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.springframework.context.ApplicationContext;

import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import toscabox.desktop.constant.CommonConstants;
import toscabox.desktop.constant.MessageCode;
import toscabox.desktop.constant.Page;
import toscabox.desktop.constant.PaymentMethod;
import toscabox.desktop.constant.PaymentPeriodUnit;
import toscabox.desktop.constant.StyleConstants;
import toscabox.desktop.controller.CommonDataSaveController;
import toscabox.desktop.service.PurchaseService;
import toscabox.desktop.utility.SpringUtils;
import toscabox.desktop.viewmodel.ProductVM;
import toscabox.desktop.viewmodel.PurchaseOrderVM;
import toscabox.desktop.viewmodel.PurchaseOrderVM.PurchaseProductVM;
import toscabox.desktop.viewmodel.SupplierVM;

public class PurchaseAddController extends CommonDataSaveController {

    @FXML
    private TextField tfOrderNumber;

    @FXML
    private MaskedTextField tfOrderDate;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentMethod;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPeriodCount;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPeriodUnit;

    @FXML
    private TextField tfDueDate;

    @FXML
    private Label lblPeriod;

    @FXML
    private Label lblDueDate;

    @FXML
    private TextField tfDiscount;

    @FXML
    private TextField tfTax;

    @FXML
    private TextField tfSupplierName;

    @FXML
    private TextField tfSupplierCode;

    @FXML
    private TextField tfSupplierPhone;

    @FXML
    private TextField tfSupplierEmail;

    @FXML
    private TextField tfSupplierAddress;

    @FXML
    private TextField tfSupplierWebsite;

    @FXML
    private Button btnNewSupplier;

    @FXML
    private TextField tfProductName;

    @FXML
    private TextField tfProductCode;

    @FXML
    private TextField tfProductBarcode;

    @FXML
    private TextField tfProductCategory;

    @FXML
    private TextField tfProductQuantity;

    @FXML
    private TextField tfProductUnit;

    @FXML
    private TextField tfPurchasePrice;

    @FXML
    private TextField tfSellingPrice;

    @FXML
    private Button btnNewProduct;

    @FXML
    private Button btnAddProduct;

    @FXML
    private TableView<PurchaseProductVM> tblPurchaseProduct;

    @FXML
    private TableColumn<PurchaseProductVM, String> colProductName;

    @FXML
    private TableColumn<PurchaseProductVM, String> colProductCategory;

    @FXML
    private TableColumn<PurchaseProductVM, Integer> colQuantity;

    @FXML
    private TableColumn<PurchaseProductVM, String> colUnit;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colPurchasePrice;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colSubtotal;

    @FXML
    private TableColumn<PurchaseProductVM, BigDecimal> colSellingPrice;

    @FXML
    private Label lblCurrencySymbol;

    @FXML
    private Label lblTotalPurchase;

    @FXML
    private Label lblTotalProduct;

    @FXML
    private Label lblDiscount;

    @FXML
    private Label lblTax;

    @FXML
    private Label lblTotalPayment;

    @FXML
    private Button btnSaveAndAdd;

    private SupplierVM selectedSupplier;
    private ProductVM selectedProduct;
    private Integer totalProduct = 0;
    private BigDecimal totalPurchase = BigDecimal.ZERO;
    private BigDecimal totalPayment = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;

    private PurchaseService purchaseService;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_PURCHASE);
            resetControls();
        }
    }

    @FXML
    void onActionBtnAddProduct(ActionEvent event) {
        BigDecimal purchasePrice = toBigDecimalOrZero(tfPurchasePrice.getText());
        Integer purchaseQuantity = toIntegerOrZero(tfProductQuantity.getText());
        BigDecimal sellingPrice = toBigDecimalOrNull(tfSellingPrice.getText());
        if (isValidProductValues(purchaseQuantity, purchasePrice, sellingPrice)) {
            BigDecimal subtotalPurchase = purchasePrice.multiply(new BigDecimal(purchaseQuantity));
            PurchaseProductVM purchaseProduct = new PurchaseProductVM();
            purchaseProduct.setProduct(selectedProduct);
            purchaseProduct.setPurchasePrice(purchasePrice);
            purchaseProduct.setPurchaseQuantity(purchaseQuantity);
            purchaseProduct.setSellingPrice(toBigDecimalOrDefault(tfSellingPrice.getText(), null));
            purchaseProduct.setSubtotalPurchase(subtotalPurchase);
            int indexProduct = getProductIndexInTable(selectedProduct, tblPurchaseProduct);
            if (indexProduct != -1) {
                tblPurchaseProduct.getItems().remove(indexProduct);
            }
            tblPurchaseProduct.getItems().add(purchaseProduct);
            this.selectedProduct = null;
            setSelectedProduct(null);
            calculatePurchase();
        }
    }

    @FXML
    void onActionBtnNewProduct(ActionEvent event) {
        StageUtils.modal(Page.MASTER_PRODUCT_ADD, false, we -> {
            if (Boolean.TRUE.equals(getPageData())) {
                displayInfo(MessageCode.SUCCESS_ADD_PRODUCT);
            }
        });
    }

    @FXML
    void onActionBtnNewSupplier(ActionEvent event) {
        StageUtils.modal(Page.MASTER_SUPPLIER_ADD, false, we -> {
            if (getPageData() != null) {
                displayInfo(MessageCode.SUCCESS_ADD_SUPPLIER);
            }
        });
    }

    @FXML
    void onActionBtnRemoveProduct(ActionEvent event) {
        PurchaseProductVM selected = tblPurchaseProduct.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tblPurchaseProduct.getItems().remove(selected);
            totalProduct = totalProduct - selected.getPurchaseQuantity();
            totalPayment = totalPayment.subtract(selected.getSubtotalPurchase());
            lblTotalPurchase.setText(toStringOrDefault(totalPayment, "0"));
            lblTotalProduct.setText(toStringOrDefault(totalProduct, "0"));
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        purchaseService = SpringUtils.getBean(PurchaseService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        Locale locale = resources.getLocale();
        TextFieldUtils.onTextChanged((ov, nv) -> calculateDueDate(), tfOrderDate);
        ComboBoxUtils.initSimple(
                cbPaymentMethod,
                new SimpleComboBoxModel(PaymentMethod.CASH.name(), translate("lbl.cash")),
                new SimpleComboBoxModel(PaymentMethod.CREDIT.name(), translate("lbl.credit")));
        ComboBoxUtils.initSimple(
                cbPeriodUnit,
                new SimpleComboBoxModel(PaymentPeriodUnit.DAY.name(), translate("lbl.day")),
                new SimpleComboBoxModel(PaymentPeriodUnit.WEEK.name(), translate("lbl.week")),
                new SimpleComboBoxModel(PaymentPeriodUnit.MONTH.name(), translate("lbl.month")));
        List<SimpleComboBoxModel> periodCountModels = new ArrayList<>();
        IntStream.rangeClosed(1, 30).forEach(num -> {
            String str = String.valueOf(num);
            periodCountModels.add(new SimpleComboBoxModel(str, str));
        });
        ComboBoxUtils.initSimple(
                cbPeriodCount,
                periodCountModels.toArray(new SimpleComboBoxModel[periodCountModels.size()]));
        ComboBoxUtils.selectIndex(cbPaymentMethod, 0);
        ComboBoxUtils.selectIndex(cbPeriodUnit, 0);
        ComboBoxUtils.selectIndex(cbPeriodCount, 0);
        ComboBoxUtils.onSelectedItemChanged(cbPaymentMethod, (ov, nv) -> {
            PaymentMethod pm = PaymentMethod.valueOf(nv.getValue());
            boolean isCreditPayment = PaymentMethod.CREDIT.equals(pm);
            togglePaymentPeriodControls(!isCreditPayment);
            if (isCreditPayment) {
                calculateDueDate();
            }
        });
        ComboBoxUtils.onSelectedItemChanged(cbPeriodCount, (ov, nv) -> calculateDueDate());
        ComboBoxUtils.onSelectedItemChanged(cbPeriodUnit, (ov, nv) -> calculateDueDate());
        tfSupplierName.focusedProperty().addListener((o, ov, nv) -> {
            if (Boolean.TRUE.equals(nv)) {
                StageUtils.modal(Page.MASTER_SUPPLIER_CHOOSE, false, we -> setSelectedSupplier(getPageData()));
                setFocused(btnNewSupplier);
            }
        });
        tfProductName.focusedProperty().addListener((o, ov, nv) -> {
            if (Boolean.TRUE.equals(nv)) {
                StageUtils.modal(Page.MASTER_PRODUCT_CHOOSE, false, we -> setSelectedProduct(getPageData()));
                setFocused(tfProductQuantity);
            }
        });
        TextFieldUtils.setDigitTextFields(tfDiscount, tfTax, tfSellingPrice, tfPurchasePrice, tfProductQuantity);
        TableViewUtils.setColumnValue(colProductName, purchaseProduct -> purchaseProduct.getProduct().getName());
        TableViewUtils.setColumnValue(colUnit, purchaseProduct -> purchaseProduct.getProduct().getUnitLabel());
        TableViewUtils
                .setColumnValue(colProductCategory, purchaseProduct -> purchaseProduct.getProduct().getCategoryName());
        TableViewUtils.initTableColumn(
                colPurchasePrice,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getPurchasePrice,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colQuantity,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getPurchaseQuantity,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSubtotal,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getSubtotalPurchase,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colSellingPrice,
                new NumberCellFactory<>(locale),
                PurchaseProductVM::getSellingPrice,
                StyleConstants.ALIGN_RIGHT);
        disableOnValidationError(btnSaveAndAdd);
        TextFieldUtils.onTextChanged((ov, nv) -> calculatePurchase(), tfTax, tfDiscount);
    }

    @Override
    protected void initDataSaveControlValues() {
        calculateDueDate();
    }

    @Override
    protected void registerValidator(ValidationSupport vs) {
        registerRequiredFields(tfOrderNumber, tfOrderDate, tfSupplierName);
        vs.registerValidator(
                tblPurchaseProduct,
                (c, v) -> ValidationResult.fromErrorIf(
                        c,
                        translate(MessageCode.ERROR_REQUIRED),
                        tblPurchaseProduct.getItems().isEmpty()));
        vs.registerValidator(tfOrderDate, (c, v) -> {
            LocalDate orderDate = parseDateQuietly(tfOrderDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN);
            return ValidationResult.fromErrorIf(
                    c,
                    translate(MessageCode.ERROR_PURCHASE_ORDER_DATE_GREATER_THAN_TODAY),
                    orderDate != null && orderDate.isAfter(LocalDate.now()));
        });
        revalidateOnChange(vs);
    }

    private void revalidateOnChange(ValidationSupport vs) {
        tblPurchaseProduct.getItems()
                .addListener((Change<? extends PurchaseProductVM> listener) -> vs.revalidate(tblPurchaseProduct));
    }

    @Override
    protected Object save() {
        PaymentMethod paymentMethod = PaymentMethod.valueOf(ComboBoxUtils.getSelectedItem(cbPaymentMethod).getValue());
        PurchaseOrderVM po = new PurchaseOrderVM();
        po.setOrderNumber(tfOrderNumber.getText().trim());
        po.setOrderDate(
                LocalDate.parse(
                        tfOrderDate.getText(),
                        DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN)));
        po.setSupplierId(selectedSupplier.getId());
        po.setPaymentMethod(paymentMethod);
        if (PaymentMethod.CREDIT.equals(paymentMethod)) {
            po.setPaymentPeriodCount(Integer.valueOf(ComboBoxUtils.getSelectedItem(cbPeriodCount).getValue()));
            po.setPaymentPeriodUnit(PaymentPeriodUnit.valueOf(ComboBoxUtils.getSelectedItem(cbPeriodUnit).getValue()));
            po.setDueDate(
                    LocalDate.parse(
                            tfDueDate.getText(),
                            DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN)));
        }
        po.setTotalProduct(totalProduct);
        po.setTotalPayment(totalPayment);
        po.setPurchaseProducts(tblPurchaseProduct.getItems());
        po.setDiscount(discount);
        po.setTax(tax);
        po.setTotalPurchase(totalPurchase);
        return purchaseService.createPurchase(po);
    }

    private void togglePaymentPeriodControls(boolean disable) {
        lblPeriod.disableProperty().set(disable);
        lblDueDate.disableProperty().set(disable);
        cbPeriodCount.disableProperty().set(disable);
        cbPeriodUnit.disableProperty().set(disable);
        tfDueDate.disableProperty().set(disable);
    }

    private void calculateDueDate() {
        LocalDate orderDate = parseDateQuietly(tfOrderDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN);
        if (orderDate == null) {
            tfDueDate.setText(null);
            return;
        }
        Integer periodCount = Integer.valueOf(ComboBoxUtils.getSelectedItem(cbPeriodCount).getValue());
        PaymentPeriodUnit periodUnit = PaymentPeriodUnit
                .valueOf(ComboBoxUtils.getSelectedItem(cbPeriodUnit).getValue());
        LocalDate dueDate = null;
        switch (periodUnit) {
            case DAY:
                dueDate = orderDate.plusDays(periodCount);
                break;
            case WEEK:
                dueDate = orderDate.plusWeeks(periodCount);
                break;
            case MONTH:
                dueDate = orderDate.plusMonths(periodCount);
                break;
        }
        tfDueDate.setText(
                dueDate == null ?
                        null : dueDate.format(DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN)));
    }

    private void setSelectedSupplier(SupplierVM vm) {
        if (vm != null) {
            this.selectedSupplier = vm;
            tfSupplierName.setText(vm.getName());
            tfSupplierCode.setText(vm.getCode());
            tfSupplierEmail.setText(vm.getEmail());
            tfSupplierPhone.setText(vm.getPhone());
            tfSupplierAddress.setText(vm.getAddress());
            tfSupplierWebsite.setText(vm.getWebsite());
        } else if (selectedSupplier == null) {
            TextFieldUtils.setTextEmpty(
                    tfSupplierName,
                    tfSupplierCode,
                    tfSupplierEmail,
                    tfSupplierPhone,
                    tfSupplierAddress,
                    tfSupplierWebsite);
        }
    }

    private void setSelectedProduct(ProductVM vm) {
        if (vm != null) {
            this.selectedProduct = vm;
            tfProductName.setText(vm.getName());
            tfProductBarcode.setText(vm.getBarcode());
            tfProductCategory.setText(vm.getCategoryName());
            tfProductCode.setText(vm.getCode());
            tfProductUnit.setText(vm.getUnitLabel());
            tfSellingPrice.setText(toStringOrNull(vm.getSellingPrice()));
            tfPurchasePrice.setText(toStringOrNull(vm.getPurchasePrice()));
        } else if (selectedProduct == null) {
            TextFieldUtils.setTextEmpty(
                    tfProductName,
                    tfProductBarcode,
                    tfProductCategory,
                    tfProductCode,
                    tfProductUnit,
                    tfSellingPrice,
                    tfPurchasePrice,
                    tfProductQuantity);
        }
    }

    private void resetControls() {
        this.selectedSupplier = null;
        this.selectedProduct = null;
        setSelectedProduct(null);
        setSelectedSupplier(null);
        ComboBoxUtils.selectIndex(cbPaymentMethod, 0);
        ComboBoxUtils.selectIndex(cbPeriodCount, 0);
        ComboBoxUtils.selectIndex(cbPeriodUnit, 0);
        TextFieldUtils.setTextEmpty(tfOrderNumber, tfDueDate);
        tfOrderDate.setPlainText("");
        tblPurchaseProduct.getItems().clear();
        lblTotalProduct.setText("0");
        lblTotalPurchase.setText("0");
    }

    private int getProductIndexInTable(ProductVM product, TableView<PurchaseProductVM> table) {
        Predicate<PurchaseProductVM> productExists = item -> item.getProduct().equals(product);
        return TableViewUtils.getItemIndex(productExists, table);
    }

    private boolean isValidProductValues(Integer purchaseQuantity, BigDecimal purchasePrice, BigDecimal sellingPrice) {
        return selectedProduct != null && purchasePrice.compareTo(BigDecimal.ZERO) > 0 && purchaseQuantity > 0
                && (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) > 0);
    }

    private void calculatePurchase() {
        tax = toBigDecimalOrZero(tfTax.getText());
        discount = toBigDecimalOrZero(tfDiscount.getText());
        totalPurchase = BigDecimal.ZERO;
        totalPayment = BigDecimal.ZERO;
        totalProduct = 0;
        tblPurchaseProduct.getItems().forEach(item -> {
            totalProduct = totalProduct + item.getPurchaseQuantity();
            totalPurchase = totalPurchase.add(item.getSubtotalPurchase());
        });
        totalPayment = totalPurchase.subtract(discount).add(tax);
        lblTotalPurchase.setText(formatNumber(totalPurchase));
        lblTotalPayment.setText(formatNumber(totalPayment));
        lblTotalProduct.setText(formatNumber(totalProduct));
        lblTax.setText(formatNumber(tax));
        lblDiscount.setText(formatNumber(discount));
    }

}

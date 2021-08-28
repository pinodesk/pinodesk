package toscabox.desktop.controller.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.controlsfx.validation.ValidationSupport;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import toscabox.desktop.constant.ConfigurationConstants;
import toscabox.desktop.constant.MessageCode;
import toscabox.desktop.constant.Page;
import toscabox.desktop.constant.PaymentMethod;
import toscabox.desktop.constant.PaymentPeriodUnit;
import toscabox.desktop.constant.StyleConstants;
import toscabox.desktop.controller.CommonDataSaveController;
import toscabox.desktop.service.ConfigurationService;
import toscabox.desktop.utility.SpringUtils;
import toscabox.desktop.viewmodel.ProductVM;
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
    private Label lblProductCount;

    private SupplierVM selectedSupplier;
    private ProductVM selectedProduct;

    private ConfigurationService configurationService;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {

    }

    @FXML
    void onActionBtnAddProduct(ActionEvent event) {
        if (selectedProduct != null) {
            BigDecimal purchasePrice = new BigDecimal(tfPurchasePrice.getText());
            Integer purchaseQuantity = Integer.valueOf(tfProductQuantity.getText());
            BigDecimal subtotalPurchase = purchasePrice.multiply(new BigDecimal(purchaseQuantity));
            PurchaseProductVM purchaseProduct = new PurchaseProductVM();
            purchaseProduct.setProduct(selectedProduct);
            purchaseProduct.setPurchasePrice(purchasePrice);
            purchaseProduct.setPurchaseQuantity(purchaseQuantity);
            purchaseProduct.setSellingPrice(new BigDecimal(tfSellingPrice.getText()));
            purchaseProduct.setSubtotalPurchase(subtotalPurchase);
            tblPurchaseProduct.getItems().add(purchaseProduct);
            this.selectedProduct = null;
            setSelectedProduct(null);
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

    @Override
    protected void initServices(ApplicationContext ctx) {
        configurationService = SpringUtils.getBean(ConfigurationService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        ComboBoxUtils.initSimple(cbPaymentMethod,
                new SimpleComboBoxModel(PaymentMethod.CASH.name(), translate("lbl.cash")),
                new SimpleComboBoxModel(PaymentMethod.CREDIT.name(), translate("lbl.credit")));
        ComboBoxUtils.initSimple(cbPeriodUnit,
                new SimpleComboBoxModel(PaymentPeriodUnit.DAY.name(), translate("lbl.day")),
                new SimpleComboBoxModel(PaymentPeriodUnit.WEEK.name(), translate("lbl.week")),
                new SimpleComboBoxModel(PaymentPeriodUnit.MONTH.name(), translate("lbl.month")));
        List<SimpleComboBoxModel> periodCountModels = new ArrayList<>();
        IntStream.rangeClosed(1, 30).forEach(num -> {
            String str = String.valueOf(num);
            periodCountModels.add(new SimpleComboBoxModel(str, str));
        });
        ComboBoxUtils.initSimple(cbPeriodCount,
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
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        Locale locale = new Locale(languageCode);
        TextFieldUtils.setDigitTextFields(tfSellingPrice, tfPurchasePrice, tfProductQuantity);
        TableViewUtils.setColumnValue(colProductName, purchaseProduct -> purchaseProduct.getProduct().getName());
        TableViewUtils.setColumnValue(colUnit, purchaseProduct -> purchaseProduct.getProduct().getUnitLabel());
        TableViewUtils.setColumnValue(colProductCategory,
                purchaseProduct -> purchaseProduct.getProduct().getCategoryName());
        TableViewUtils.initTableColumn(colPurchasePrice, new NumberCellFactory<>(locale),
                PurchaseProductVM::getPurchasePrice, StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colQuantity, new NumberCellFactory<>(locale),
                PurchaseProductVM::getPurchaseQuantity, StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colSubtotal, new NumberCellFactory<>(locale),
                PurchaseProductVM::getSubtotalPurchase, StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(colSellingPrice, new NumberCellFactory<>(locale),
                PurchaseProductVM::getSellingPrice, StyleConstants.ALIGN_RIGHT);
    }

    @Override
    protected void initDataSaveControlValues() {
        calculateDueDate();
    }

    @Override
    protected void registerValidator(ValidationSupport vs) {

    }

    @Override
    protected Object save() {
        return null;
    }

    private void togglePaymentPeriodControls(boolean disable) {
        lblPeriod.disableProperty().set(disable);
        lblDueDate.disableProperty().set(disable);
        cbPeriodCount.disableProperty().set(disable);
        cbPeriodUnit.disableProperty().set(disable);
        tfDueDate.disableProperty().set(disable);
    }

    private void calculateDueDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        Integer periodCount = Integer.valueOf(ComboBoxUtils.getSelectedItem(cbPeriodCount).getValue());
        PaymentPeriodUnit periodUnit = PaymentPeriodUnit
                .valueOf(ComboBoxUtils.getSelectedItem(cbPeriodUnit).getValue());
        String dueDate = "";
        switch (periodUnit) {
            case DAY:
                dueDate = formatter.format(today.plusDays(periodCount));
                break;
            case WEEK:
                dueDate = formatter.format(today.plusWeeks(periodCount));
                break;
            case MONTH:
                dueDate = formatter.format(today.plusMonths(periodCount));
                break;
        }
        tfDueDate.setText(dueDate);
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
            TextFieldUtils.setTextNull(tfSupplierName, tfSupplierCode, tfSupplierEmail, tfSupplierPhone,
                    tfSupplierAddress, tfSupplierWebsite);
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
            TextFieldUtils.setTextNull(tfProductName, tfProductBarcode, tfProductCategory, tfProductCode, tfProductUnit,
                    tfSellingPrice, tfPurchasePrice, tfProductQuantity);
        }
    }

}

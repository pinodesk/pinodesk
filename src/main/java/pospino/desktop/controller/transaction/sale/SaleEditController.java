package pospino.desktop.controller.transaction.sale;

import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.formatOrDefault;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toIntegerOrZero;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toStringOrEmpty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import com.gitlab.mudiasoft.pandora.factory.LocalDateCellFactory;
import com.gitlab.mudiasoft.pandora.factory.NumberCellFactory;
import com.gitlab.mudiasoft.pandora.model.SimpleComboBoxModel;
import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.EventUtils;
import com.gitlab.mudiasoft.pandora.utility.ScrollPaneUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;
import com.gitlab.mudiasoft.pandora.utility.ValidationResult;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.SellingMode;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.javafx.converter.GroupedProductExpiryComboBoxConverter;
import pospino.desktop.service.ProductService;
import pospino.desktop.service.SaleService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.CustomerVM;
import pospino.desktop.viewmodel.DoctorVM;
import pospino.desktop.viewmodel.GroupedProductExpiryVM;
import pospino.desktop.viewmodel.ProductVM;
import pospino.desktop.viewmodel.SaleEditVM;
import pospino.desktop.viewmodel.SaleProductVM;
import pospino.desktop.viewmodel.SaleVM;

public class SaleEditController extends CommonDataSaveController {

    @FXML
    private ScrollPane scrollPaneSaleEdit;

    @FXML
    private Button btnRemove;

    @FXML
    private TextField tfCustomer;

    @FXML
    private Button btnNewCustomer;

    @FXML
    private HBox hboxDoctor;

    @FXML
    private TextField tfDoctor;

    @FXML
    private Button tfNewDoctor;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbSellingMode;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private VBox vboxDueDate;

    @FXML
    private DatePicker dpDueDate;

    @FXML
    private TextField tfProduct;

    @FXML
    private Button btnNewProduct;

    @FXML
    private TextField tfProductCategory;

    @FXML
    private VBox vboxUnit;

    @FXML
    private TextField tfProductUnit;

    @FXML
    private TextField tfSaleQuantity;

    @FXML
    private TextField tfSellingPrice;

    @FXML
    private TextField tfCurrentQuantity;

    @FXML
    private ComboBox<GroupedProductExpiryVM> cbExpiredDate;

    @FXML
    private Button btnAddProduct;

    @FXML
    private TableView<SaleProductVM> tblSaleProduct;

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
    private Label lblTotalProduct;

    @FXML
    private Label lblTotalSale;

    @FXML
    private Label lblDiscount;

    @FXML
    private Label lblTax;

    @FXML
    private Label lblTotalPayment;

    @FXML
    private Button btnNewDoctor;

    private ProductVM selectedProduct;
    private CustomerVM selectedCustomer;
    private DoctorVM selectedDoctor;
    private Integer totalProduct;
    private BigDecimal totalSale;
    private BigDecimal totalPayment;

    private SaleService saleService;
    private ProductService productService;

    private int idxSelectedSaleProduct = -1;

    private SaleVM currentSale;

    @FXML
    void onActionBtnNewProduct(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_PRODUCT_ADD, false, we -> {
            if (Boolean.TRUE.equals(getPageData())) {
                displayInfo(MessageCode.SUCCESS_ADD_PRODUCT);
            }
        });
    }

    @FXML
    void onActionBtnNewCustomer(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_CUSTOMER_ADD, false, we -> {
            if (getPageData() != null) {
                displayInfo(MessageCode.SUCCESS_ADD_CUSTOMER);
            }
        });
    }

    @FXML
    void onActionBtnNewDoctor(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_DOCTOR_ADD, false, we -> {
            if (getPageData() != null) {
                displayInfo(MessageCode.SUCCESS_ADD_DOCTOR);
            }
        });
    }

    @FXML
    void onActionBtnRemoveProduct(ActionEvent event) {
        if (TableViewUtils.hasItemSelected(tblSaleProduct)) {
            tblSaleProduct.getItems().remove(TableViewUtils.getSelectedItem(tblSaleProduct));
            calculateSaleSummary();
        }
        if (tblSaleProduct.getItems().isEmpty()) {
            tblSaleProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        }
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SALE);
        if (result.isConfirmed()) {
            saleService.removeSales(List.of(currentSale.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_SALE);
            setPageData(Boolean.TRUE);
            close();
        }
    }

    @FXML
    void onActionBtnAddProduct(ActionEvent event) {
        ValidationResult validationResult = validateAddProduct(selectedProduct);
        if (!validationResult.isValid()) {
            displayError(validationResult.getMessages());
            return;
        }
        Integer saleQuantity = toIntegerOrNull(tfSaleQuantity.getText());
        BigDecimal sellingPrice = toBigDecimalOrNull(tfSellingPrice.getText());
        SaleProductVM saleProduct = new SaleProductVM();
        saleProduct.setProductId(selectedProduct.getId());
        saleProduct.setProductName(selectedProduct.getName());
        saleProduct.setProductCategoryCode(selectedProduct.getCategoryCode());
        saleProduct.setProductCategoryName(selectedProduct.getCategoryName());
        saleProduct.setProductUnitLabel(selectedProduct.getUnitLabel());
        saleProduct.setCurrentQuantity(toIntegerOrNull(tfCurrentQuantity.getText()));
        saleProduct.setSaleQuantity(saleQuantity);
        saleProduct.setSellingPrice(sellingPrice);
        saleProduct.setGeneralSellingPrice(selectedProduct.getGeneralSellingPrice());
        saleProduct.setPrescriptionSellingPrice(selectedProduct.getPrescriptionSellingPrice());
        saleProduct.setSubtotal(sellingPrice.multiply(BigDecimal.valueOf(saleQuantity)));
        GroupedProductExpiryVM productExpiry = ComboBoxUtils.getSelectedItem(cbExpiredDate);
        if (productExpiry != null) {
            saleProduct.setExpiredDate(productExpiry.getExpiredDate());
        }
        if (idxSelectedSaleProduct != -1) {
            tblSaleProduct.getItems().remove(idxSelectedSaleProduct);
            idxSelectedSaleProduct = -1;
        }
        int idx = getProductIndexInTable(selectedProduct, productExpiry, tblSaleProduct);
        if (idx != -1) {
            tblSaleProduct.getItems().remove(idx);
        }
        tblSaleProduct.getItems().add(saleProduct);
        calculateSaleSummary();
        handleSelectedProduct(new ChooseResultVM<>(false, Optional.empty()));
    }

    @Override
    protected void onActionBtnSave(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_EDIT_SALE);
        if (!result.isConfirmed()) {
            return;
        }
        super.onActionBtnSave(event);
    }

    @Override
    protected void initDataSaveControlActions() {
        disableWriteAction(
                MenuCodeConstants.TRANSACTION_SALES,
                btnSave,
                btnRemove,
                btnNewCustomer,
                btnNewProduct,
                btnNewDoctor);
        initCustomDatePicker(dpDueDate);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
        ComboBoxUtils.initSimple(
                cbSellingMode,
                new SimpleComboBoxModel(SellingMode.GENERAL, t.translate(CommonLabel.LBL_GENERAL)),
                new SimpleComboBoxModel(SellingMode.PRESCRIPTION, t.translate(CommonLabel.LBL_PRESCRIPTION)));
        Locale locale = resources.getLocale();
        TextFieldUtils.setDigitTextFields(tfSellingPrice, tfCurrentQuantity, tfSaleQuantity);
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
        tblSaleProduct.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblSaleProduct.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblSaleProduct.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTableSaleProduct();
            }
        });
        setCustomerChooser(tfCustomer, true, this::handleSelectedCustomer, tfDoctor.getParent());
        setDoctorChooser(tfDoctor, this::handleSelectedDoctor, tfInvoiceNumber);
        setProductChooser(tfProduct, this::handleSelectedProduct, tfSaleQuantity);
        ComboBoxUtils.onSelectedItemChanged(cbPaymentStatus, (ov, nv) -> {
            boolean isPaid = PaymentStatus.PAID.equals(nv.getValue());
            if (isPaid) {
                dpDueDate.setValue(null);
            }
            vboxDueDate.setDisable(isPaid);
        });
        ComboBoxUtils.onSelectedItemChanged(cbSellingMode, (ov, nv) -> updateDisplaySellingPrice(nv.getValue()));
    }

    @Override
    protected void initDataSaveControlValues() {
        Locale locale = resources.getLocale();
        currentSale = getPageData();
        if (currentSale.getDoctorId() != null) {
            selectedDoctor = new DoctorVM();
            selectedDoctor.setId(currentSale.getId());
            selectedDoctor.setName(currentSale.getDoctorName());
            tfDoctor.setText(selectedDoctor.getName());
        }
        if (currentSale.getCustomerId() != null) {
            selectedCustomer = new CustomerVM();
            selectedCustomer.setId(currentSale.getCustomerId());
            selectedCustomer.setName(currentSale.getCustomerName());
            tfCustomer.setText(selectedCustomer.getName());
        }
        ComboBoxUtils.select(
                cbSellingMode,
                () -> cbSellingMode.getItems().stream()
                        .filter(vm -> vm.getValue().toString().equals(currentSale.getSellingMode())).findAny()
                        .orElseThrow());
        ComboBoxUtils.select(
                cbPaymentStatus,
                () -> cbPaymentStatus.getItems().stream()
                        .filter(vm -> vm.getValue().toString().equals(currentSale.getPaymentStatus())).findAny()
                        .orElseThrow());
        List<SaleProductVM> products = saleService.getSaleProducts(currentSale.getId());
        tblSaleProduct.getItems().addAll(products);
        LocalDate paymentDueDate = currentSale.getPaymentDueDate();
        totalProduct = currentSale.getTotalProduct();
        totalSale = currentSale.getTotalSale();
        totalPayment = currentSale.getTotalPayment();
        tfInvoiceNumber.setText(currentSale.getInvoiceNumber());
        if (paymentDueDate != null) {
            dpDueDate.setValue(paymentDueDate);
        }
        lblTotalPayment.setText(formatOrDefault(totalPayment, locale, "0"));
        lblTotalProduct.setText(formatOrDefault(totalProduct, locale, "0"));
        lblTotalSale.setText(formatOrDefault(totalSale, locale, "0"));
        if (!isPharmacyFeatureEnabled()) {
            setVisibleInLayout(false, hboxDoctor);
            cbSellingMode.getItems().remove(1);
        }
        Platform.runLater(() -> {
            ScrollPaneUtils.fixBlur(scrollPaneSaleEdit);
        });
    }

    @Override
    protected Object save() {
        SaleEditVM saleEdit = new SaleEditVM();
        saleEdit.setCustomerId(selectedCustomer == null ? null : selectedCustomer.getId());
        saleEdit.setDoctorId(selectedDoctor == null ? null : selectedDoctor.getId());
        saleEdit.setInvoiceNumber(tfInvoiceNumber.getText().trim());
        PaymentStatus paymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        saleEdit.setPaymentStatus(paymentStatus);
        if (PaymentStatus.UNPAID.equals(paymentStatus)) {
            saleEdit.setPaymentDueDate(dpDueDate.getValue());
        }
        saleEdit.setSellingMode(ComboBoxUtils.getSelectedItem(cbSellingMode).getValue());
        saleEdit.setTotalPayment(totalPayment);
        saleEdit.setTotalProduct(totalProduct);
        saleEdit.setTotalSale(totalSale);
        saleEdit.setSaleProducts(tblSaleProduct.getItems());
        saleService.updateSale(saleEdit, currentSale.getId());
        return true;
    }

    @Override
    protected void validate(ControlValidator validator) {
        LocalDate dueDate = dpDueDate.getValue();
        PaymentStatus selected = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        boolean isUnpaid = PaymentStatus.UNPAID.equals(selected);
        validator.validateBlank(tfInvoiceNumber, MessageCode.ERROR_INVALID_INVOICE_NUMBER);
        validator.validateCustom(
                () -> isUnpaid && selectedCustomer == null,
                MessageCode.ERROR_UNPAID_PAYMENT_WITH_EMPTY_CUSTOMER);
        LocalDate today = LocalDate.now();
        validator.validateCustom(() -> isUnpaid && dueDate == null, MessageCode.ERROR_INVALID_DUE_DATE);
        validator.validateCustom(
                () -> isUnpaid && dueDate != null && dueDate.isBefore(today),
                MessageCode.ERROR_DUE_DATE_BEFORE_TODAY);
        validator.validateCustom(() -> tblSaleProduct.getItems().isEmpty(), MessageCode.ERROR_EMPTY_PRODUCT);
    }

    @Override
    protected void initServices() {
        saleService = SpringUtils.getBean(SaleService.class);
        productService = SpringUtils.getBean(ProductService.class);
    }

    public void handleSelectedProduct(ChooseResultVM<ProductVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(product -> {
            selectedProduct = product;
            tfProduct.setText(product.getName());
            tfProductCategory.setText(product.getCategoryName());
            tfProductUnit.setText(product.getUnitLabel());
            tfCurrentQuantity.setText(toStringOrEmpty(product.getQuantity()));
            tfSellingPrice.setText(toStringOrEmpty(product.getGeneralSellingPrice()));
            SellingMode mode = ComboBoxUtils.getSelectedItem(cbSellingMode).getValue();
            BigDecimal prescriptionSellingPrice = product.getPrescriptionSellingPrice();
            if (SellingMode.PRESCRIPTION.equals(mode) && prescriptionSellingPrice != null) {
                tfSellingPrice.setText(toStringOrEmpty(prescriptionSellingPrice));
            }
            List<GroupedProductExpiryVM> productExpiries = productService.getRemainingProductExpiry(product.getId());
            if (!productExpiries.isEmpty()) {
                productExpiries.add(0, null);
                ComboBoxUtils
                        .init(cbExpiredDate, new GroupedProductExpiryComboBoxConverter(cbExpiredDate), productExpiries);
            }
            if (product.getQuantity() == null) {
                tfCurrentQuantity.setEditable(true);
            }
        }, () -> {
            selectedProduct = null;
            TextFieldUtils.setTextEmpty(
                    tfProduct,
                    tfProductCategory,
                    tfProductUnit,
                    tfSaleQuantity,
                    tfSellingPrice,
                    tfCurrentQuantity);
            cbExpiredDate.setItems(FXCollections.observableArrayList());
            tfCurrentQuantity.setEditable(false);
        });
    }

    public void handleSelectedCustomer(ChooseResultVM<CustomerVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(customer -> {
            selectedCustomer = customer;
            tfCustomer.setText(customer.getName());
        }, () -> {
            selectedCustomer = null;
            tfCustomer.setText("");
        });
    }

    public void handleSelectedDoctor(ChooseResultVM<DoctorVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(doctor -> {
            selectedDoctor = doctor;
            tfDoctor.setText(doctor.getName());
        }, () -> {
            selectedDoctor = null;
            tfDoctor.setText("");
        });
    }

    private int getProductIndexInTable(
            ProductVM product,
            GroupedProductExpiryVM productExpiry,
            TableView<SaleProductVM> table) {
        Predicate<SaleProductVM> productExists = item -> {
            boolean equalsProductId = item.getProductId().equals(product.getId());
            boolean equalsExpiryId = productExpiry == null ?
                    item.getExpiredDate() == null : productExpiry.getExpiredDate().equals(item.getExpiredDate());
            return equalsProductId && equalsExpiryId;
        };
        return TableViewUtils.getItemIndex(productExists, table);
    }

    private int getIndexBySameProductAndDifferentExpiry(
            ProductVM product,
            GroupedProductExpiryVM productExpiry,
            TableView<SaleProductVM> table) {
        Predicate<SaleProductVM> productExists = item -> {
            boolean equalsProductId = item.getProductId().equals(product.getId());
            boolean equalsExpiryId = productExpiry == null ?
                    item.getExpiredDate() != null : !productExpiry.getExpiredDate().equals(item.getExpiredDate());
            return equalsProductId && equalsExpiryId;
        };
        return TableViewUtils.getItemIndex(productExists, table);
    }

    private void calculateSaleSummary() {
        Locale locale = resources.getLocale();
        ObservableList<SaleProductVM> items = tblSaleProduct.getItems();
        totalProduct = items.stream().map(SaleProductVM::getSaleQuantity).reduce(0, Integer::sum);
        totalSale = items.stream().map(SaleProductVM::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalPayment = totalSale;
        lblTotalProduct.setText(formatOrDefault(totalProduct, locale, "0"));
        lblTotalSale.setText(formatOrDefault(totalSale, locale, "0"));
        lblTotalPayment.setText(formatOrDefault(totalPayment, locale, "0"));
    }

    private void handleActionTableSaleProduct() {
        if (TableViewUtils.hasItemSelected(tblSaleProduct)) {
            idxSelectedSaleProduct = TableViewUtils.getSelectedIndex(tblSaleProduct);
            SaleProductVM selected = TableViewUtils.getItemByIndex(idxSelectedSaleProduct, tblSaleProduct);
            ProductVM product = new ProductVM();
            product.setName(selected.getProductName());
            product.setId(selected.getProductId());
            product.setCategoryCode(selected.getProductCategoryCode());
            product.setCategoryName(selected.getProductCategoryName());
            product.setUnitLabel(selected.getProductUnitLabel());
            product.setGeneralSellingPrice(selected.getGeneralSellingPrice());
            product.setPrescriptionSellingPrice(selected.getPrescriptionSellingPrice());
            product.setQuantity(selected.getCurrentQuantity());
            handleSelectedProduct(new ChooseResultVM<>(false, Optional.of(product)));
            if (selected.getExpiredDate() != null) {
                ComboBoxUtils.select(
                        cbExpiredDate,
                        () -> cbExpiredDate.getItems().stream()
                                .filter(vm -> vm != null && vm.getExpiredDate().equals(selected.getExpiredDate()))
                                .findAny().orElseThrow());
            }
            tfSaleQuantity.setText(toStringOrEmpty(selected.getSaleQuantity()));
            tfSellingPrice.setText(toStringOrEmpty(selected.getSellingPrice()));
        }
    }

    private ValidationResult validateAddProduct(ProductVM selectedProduct) {
        boolean isProductSelected = selectedProduct != null;
        ControlValidator cv = new ControlValidator(resources);
        cv.validateCustom(() -> !isProductSelected, MessageCode.ERROR_EMPTY_PRODUCT);
        cv.validatePositive(tfCurrentQuantity, MessageCode.ERROR_EMPTY_CURRENT_QUANTITY);
        cv.validatePositive(tfSaleQuantity, MessageCode.ERROR_EMPTY_QUANTITY);
        cv.validateCustom(() -> {
            if (selectedProduct == null) {
                return false;
            }
            Integer selectedProductQuantity = selectedProduct.getQuantity() == null ?
                    toIntegerOrZero(tfCurrentQuantity.getText()) : selectedProduct.getQuantity();
            Integer saleQuantity = toIntegerOrZero(tfSaleQuantity.getText());
            GroupedProductExpiryVM px = ComboBoxUtils.getSelectedItem(cbExpiredDate);
            if (px != null) {
                return saleQuantity > px.getQuantity();
            }
            int idx = getIndexBySameProductAndDifferentExpiry(selectedProduct, px, tblSaleProduct);
            if (idx != -1) {
                SaleProductVM otherSaleProduct = TableViewUtils.getItemByIndex(idx, tblSaleProduct);
                return otherSaleProduct.getSaleQuantity() + saleQuantity > selectedProductQuantity;
            }
            return saleQuantity > selectedProductQuantity;
        }, MessageCode.ERROR_SALE_QUANTITY_GREATER_THAN_PRODUCT_QUANTITY);
        cv.validatePositive(tfSellingPrice, MessageCode.ERROR_EMPTY_SELLING_PRICE);
        return cv.getResult();
    }

    private void updateDisplaySellingPrice(SellingMode mode) {
        if (selectedProduct != null) {
            tfSellingPrice.setText(toStringOrEmpty(selectedProduct.getGeneralSellingPrice()));
            if (SellingMode.PRESCRIPTION.equals(mode) && selectedProduct.getPrescriptionSellingPrice() != null) {
                tfSellingPrice.setText(toStringOrEmpty(selectedProduct.getPrescriptionSellingPrice()));
            }
        }
        tblSaleProduct.getItems().forEach(saleProduct -> {
            BigDecimal gsp = saleProduct.getGeneralSellingPrice();
            BigDecimal psp = saleProduct.getPrescriptionSellingPrice();
            if (gsp != null) {
                saleProduct.setSellingPrice(saleProduct.getGeneralSellingPrice());
            }
            if (SellingMode.PRESCRIPTION.equals(mode) && psp != null) {
                saleProduct.setSellingPrice(psp);
            }
            saleProduct.setSubtotal(
                    saleProduct.getSellingPrice().multiply(BigDecimal.valueOf(saleProduct.getSaleQuantity())));
        });
        tblSaleProduct.refresh();
        calculateSaleSummary();
        if (SellingMode.GENERAL.equals(mode) && selectedDoctor != null) {
            selectedDoctor = null;
            tfDoctor.setText("");
        }
    }

}

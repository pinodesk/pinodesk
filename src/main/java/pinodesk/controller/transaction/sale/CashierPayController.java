package pinodesk.controller.transaction.sale;

import static com.mudiatech.toolbox.data.StringNumberUtils.formatOrDefault;
import static com.mudiatech.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.mudiatech.toolbox.data.StringNumberUtils.toBigDecimalOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.mudiatech.pandora.model.SimpleComboBoxModel;
import com.mudiatech.pandora.utility.ComboBoxUtils;
import com.mudiatech.pandora.utility.ControlValidator;
import com.mudiatech.pandora.utility.TextFieldUtils;
import com.mudiatech.toolbox.data.StringNumberUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.MessageCode;
import pinodesk.constant.PaymentStatus;
import pinodesk.constant.SellingMode;
import pinodesk.constant.StringConstants;
import pinodesk.controller.CommonDataSaveController;
import pinodesk.service.SaleService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.CustomerVM;
import pinodesk.viewmodel.PaymentDataVM;
import pinodesk.viewmodel.SaleAddVM;
import pinodesk.viewmodel.SaleDataVM;

public class CashierPayController extends CommonDataSaveController {

    @FXML
    private Label lblSellingMode;

    @FXML
    private Label lblCustomer;

    @FXML
    private Label lblTotalProduct;

    @FXML
    private Label lblTotalSale;

    @FXML
    private TextField tfPaymentAmount;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private VBox vboxDueDate;

    @FXML
    private DatePicker dpDueDate;

    @FXML
    private Label lblChange;

    private BigDecimal changeAmount = BigDecimal.ZERO;

    private SaleDataVM saleData;

    private SaleService saleService;

    @Override
    protected void initDataSaveControlActions() {
        initCustomDatePicker(dpDueDate);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
        tfPaymentAmount.textProperty().addListener((o, ov, nv) -> {
            changeAmount = BigDecimal.ZERO;
            BigDecimal paymentAmount = toBigDecimalOrZero(nv);
            if (paymentAmount.compareTo(saleData.getTotalSale()) > 0) {
                changeAmount = paymentAmount.subtract(saleData.getTotalSale());
            }
            lblChange.setText(StringNumberUtils.formatOrDefault(changeAmount, resources.getLocale(), "0"));
        });
        ComboBoxUtils.onSelectedItemChanged(cbPaymentStatus, (ov, nv) -> {
            boolean isPaid = PaymentStatus.PAID.equals(nv.getValue());
            if (isPaid) {
                dpDueDate.setValue(null);
            }
            vboxDueDate.setDisable(isPaid);
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        Locale locale = resources.getLocale();
        saleData = getPageData();
        lblTotalSale.setText(formatOrDefault(saleData.getTotalSale(), locale, "0"));
        lblTotalProduct.setText(formatOrDefault(saleData.getTotalProduct(), locale, "0"));
        lblCustomer.setText(saleData.getCustomer().map(CustomerVM::getName).orElse(StringConstants.MINUS));
        lblSellingMode.setText(
                SellingMode.GENERAL.equals(saleData.getSellingMode()) ?
                        t.translate(CommonLabel.LBL_GENERAL) : t.translate(CommonLabel.LBL_PRESCRIPTION));
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        TextFieldUtils.setDigitTextFields(tfPaymentAmount);
    }

    @Override
    protected Object save() {
        String invoiceNumber = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS"));
        SaleAddVM saleAdd = new SaleAddVM();
        saleData.getCustomer().ifPresent(customer -> saleAdd.setCustomerId(customer.getId()));
        saleAdd.setInvoiceNumber(invoiceNumber);
        PaymentStatus paymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        saleAdd.setPaymentStatus(paymentStatus);
        LocalDate paymentDueDate = null;
        if (PaymentStatus.UNPAID.equals(paymentStatus)) {
            saleAdd.setPaymentDueDate(dpDueDate.getValue());
        }
        saleAdd.setSellingMode(saleData.getSellingMode());
        saleAdd.setTotalPayment(saleData.getTotalSale());
        saleAdd.setTotalProduct(saleData.getTotalProduct());
        saleAdd.setTotalSale(saleData.getTotalSale());
        saleAdd.setSaleProducts(saleData.getSaleProducts());
        saleService.createSaleCashier(saleAdd);
        PaymentDataVM paymentData = new PaymentDataVM();
        paymentData.setChangeAmount(changeAmount);
        paymentData.setInvoiceNumber(invoiceNumber);
        paymentData.setPaymentAmount(toBigDecimalOrNull(tfPaymentAmount.getText()));
        paymentData.setPaymentStatus(paymentStatus);
        paymentData.setPaymentDueDate(paymentDueDate);
        return paymentData;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateCustom(() -> {
            BigDecimal paymentAmount = toBigDecimalOrZero(tfPaymentAmount.getText());
            return paymentAmount.compareTo(saleData.getTotalSale()) < 0;
        }, MessageCode.ERROR_PAYMENT_AMOUNT_LOWER_THAN_SALE_AMOUNT);
        LocalDate dueDate = dpDueDate.getValue();
        PaymentStatus selected = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        boolean isUnpaid = PaymentStatus.UNPAID.equals(selected);
        validator.validateCustom(
                () -> isUnpaid && saleData.getCustomer().isEmpty(),
                MessageCode.ERROR_UNPAID_PAYMENT_WITH_EMPTY_CUSTOMER);
        LocalDate today = LocalDate.now();
        validator.validateCustom(() -> isUnpaid && dueDate == null, MessageCode.ERROR_INVALID_DUE_DATE);
        validator.validateCustom(
                () -> isUnpaid && dueDate != null && dueDate.isBefore(today),
                MessageCode.ERROR_DUE_DATE_BEFORE_TODAY);
    }

    @Override
    protected void initServices() {
        saleService = SpringUtils.getBean(SaleService.class);
    }

}

package pinus.desktop.controller.receivable;

import static com.gitlab.muhammadkholidb.toolbox.data.DateTimeUtils.parseLocalDateQuietly;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toStringOrNull;

import java.time.format.DateTimeFormatter;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.PaymentStatus;
import pinus.desktop.constant.StringConstants;
import pinus.desktop.controller.CommonDataFilterController;
import pinus.desktop.viewmodel.ChooseResultVM;
import pinus.desktop.viewmodel.CustomerVM;
import pinus.desktop.viewmodel.ReceivableFilterVM;

public class ReceivableFilterController extends CommonDataFilterController<ReceivableFilterVM> {

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private MaskedTextField tfInvoiceDateMin;

    @FXML
    private MaskedTextField tfInvoiceDateMax;

    @FXML
    private TextField tfCustomer;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private MaskedTextField tfDueDateMin;

    @FXML
    private MaskedTextField tfDueDateMax;

    @FXML
    private TextField tfPaymentAmountMin;

    @FXML
    private TextField tfPaymentAmountMax;

    @FXML
    private MaskedTextField tfPaymentDateMin;

    @FXML
    private MaskedTextField tfPaymentDateMax;

    @FXML
    private TextField tfRemarks;

    private CustomerVM selectedCustomer;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        if (currentFilter != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN);
            tfInvoiceNumber.setText(currentFilter.getInvoiceNumber());
            tfCustomer.setText(currentFilter.getCustomerName());
            tfRemarks.setText(currentFilter.getRemarks());
            tfPaymentAmountMax.setText(toStringOrNull(currentFilter.getPaymentAmountMax()));
            tfPaymentAmountMin.setText(toStringOrNull(currentFilter.getPaymentAmountMin()));
            if (currentFilter.getInvoiceDateMin() != null) {
                tfInvoiceDateMin.setText(currentFilter.getInvoiceDateMin().format(dateFormatter));
            }
            if (currentFilter.getInvoiceDateMax() != null) {
                tfInvoiceDateMax.setText(currentFilter.getInvoiceDateMax().format(dateFormatter));
            }
            if (currentFilter.getPaymentDueDateMax() != null) {
                tfDueDateMax.setText(currentFilter.getPaymentDueDateMax().format(dateFormatter));
            }
            if (currentFilter.getPaymentDueDateMin() != null) {
                tfDueDateMin.setText(currentFilter.getPaymentDueDateMin().format(dateFormatter));
            }
            if (currentFilter.getPaymentDateMax() != null) {
                tfPaymentDateMax.setText(currentFilter.getPaymentDateMax().format(dateFormatter));
            }
            if (currentFilter.getPaymentDateMin() != null) {
                tfPaymentDateMin.setText(currentFilter.getPaymentDateMin().format(dateFormatter));
            }
            if (currentFilter.getPaymentStatus() != null) {
                ComboBoxUtils.select(
                        cbPaymentStatus,
                        () -> cbPaymentStatus.getItems().stream()
                                .filter(vm -> currentFilter.getPaymentStatus().equals(vm.getValue())).findAny()
                                .orElseThrow());
            }
        }
    }

    @Override
    protected ReceivableFilterVM getFreshFilterValues() {
        ReceivableFilterVM filter = new ReceivableFilterVM();
        filter.setInvoiceNumber(tfInvoiceNumber.getText());
        filter.setInvoiceDateMax(
                parseLocalDateQuietly(tfInvoiceDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setInvoiceDateMin(
                parseLocalDateQuietly(tfInvoiceDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setPaymentDueDateMax(
                parseLocalDateQuietly(tfDueDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setPaymentDueDateMin(
                parseLocalDateQuietly(tfDueDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setPaymentDateMax(
                parseLocalDateQuietly(tfPaymentDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setPaymentDateMin(
                parseLocalDateQuietly(tfPaymentDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        PaymentStatus selectedPaymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        filter.setPaymentStatus(selectedPaymentStatus);
        if (selectedCustomer != null) {
            filter.setCustomerId(selectedCustomer.getId());
            filter.setCustomerName(selectedCustomer.getName());
        }
        filter.setPaymentAmountMax(toBigDecimalOrNull(tfPaymentAmountMax.getText()));
        filter.setPaymentAmountMin(toBigDecimalOrNull(tfPaymentAmountMin.getText()));
        filter.setRemarks(tfRemarks.getText());
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfInvoiceNumber, tfCustomer, tfPaymentAmountMax, tfPaymentAmountMin, tfRemarks);
        tfInvoiceDateMax.setPlainText("");
        tfInvoiceDateMin.setPlainText("");
        tfDueDateMax.setPlainText("");
        tfDueDateMin.setPlainText("");
        tfPaymentDateMax.setPlainText("");
        tfPaymentDateMin.setPlainText("");
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        selectedCustomer = null;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        TextFieldUtils.setDigitTextFields(tfPaymentAmountMax, tfPaymentAmountMin);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
        setCustomerChooser(tfCustomer, this::handleSelectedCustomer, tfPaymentDateMin);
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

}

package stoready.desktop.controller.payable;

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
import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.CommonLabel;
import stoready.desktop.constant.PaymentStatus;
import stoready.desktop.constant.StringConstants;
import stoready.desktop.controller.CommonDataFilterController;
import stoready.desktop.viewmodel.ChooseResultVM;
import stoready.desktop.viewmodel.PayableFilterVM;
import stoready.desktop.viewmodel.SupplierVM;

public class PayableFilterController extends CommonDataFilterController<PayableFilterVM> {

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private MaskedTextField tfInvoiceDateMin;

    @FXML
    private MaskedTextField tfInvoiceDateMax;

    @FXML
    private TextField tfSupplier;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private MaskedTextField tfDueDateMin;

    @FXML
    private MaskedTextField tfDueDateMax;

    @FXML
    private TextField tfAmountMin;

    @FXML
    private TextField tfAmountMax;

    @FXML
    private MaskedTextField tfCompletionDateMin;

    @FXML
    private MaskedTextField tfCompletionDateMax;

    @FXML
    private TextField tfRemarks;

    private SupplierVM selectedSupplier;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        if (currentFilter != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN);
            tfInvoiceNumber.setText(currentFilter.getInvoiceNumber());
            tfSupplier.setText(currentFilter.getSupplierName());
            tfRemarks.setText(currentFilter.getRemarks());
            tfAmountMax.setText(toStringOrNull(currentFilter.getAmountMax()));
            tfAmountMin.setText(toStringOrNull(currentFilter.getAmountMin()));
            if (currentFilter.getInvoiceDateMin() != null) {
                tfInvoiceDateMin.setText(currentFilter.getInvoiceDateMin().format(dateFormatter));
            }
            if (currentFilter.getInvoiceDateMax() != null) {
                tfInvoiceDateMax.setText(currentFilter.getInvoiceDateMax().format(dateFormatter));
            }
            if (currentFilter.getDueDateMax() != null) {
                tfDueDateMax.setText(currentFilter.getDueDateMax().format(dateFormatter));
            }
            if (currentFilter.getDueDateMin() != null) {
                tfDueDateMin.setText(currentFilter.getDueDateMin().format(dateFormatter));
            }
            if (currentFilter.getCompletionDateMax() != null) {
                tfCompletionDateMax.setText(currentFilter.getCompletionDateMax().format(dateFormatter));
            }
            if (currentFilter.getCompletionDateMin() != null) {
                tfCompletionDateMin.setText(currentFilter.getCompletionDateMin().format(dateFormatter));
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
    protected PayableFilterVM getFreshFilterValues() {
        PayableFilterVM filter = new PayableFilterVM();
        filter.setInvoiceNumber(tfInvoiceNumber.getText());
        filter.setInvoiceDateMax(
                parseLocalDateQuietly(tfInvoiceDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setInvoiceDateMin(
                parseLocalDateQuietly(tfInvoiceDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setDueDateMax(parseLocalDateQuietly(tfDueDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setDueDateMin(parseLocalDateQuietly(tfDueDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setCompletionDateMax(
                parseLocalDateQuietly(tfCompletionDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setCompletionDateMin(
                parseLocalDateQuietly(tfCompletionDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        PaymentStatus selectedPaymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        filter.setPaymentStatus(selectedPaymentStatus);
        if (selectedSupplier != null) {
            filter.setSupplierId(selectedSupplier.getId());
            filter.setSupplierName(selectedSupplier.getName());
        }
        filter.setAmountMax(toBigDecimalOrNull(tfAmountMax.getText()));
        filter.setAmountMin(toBigDecimalOrNull(tfAmountMin.getText()));
        filter.setRemarks(tfRemarks.getText());
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfInvoiceNumber, tfSupplier, tfAmountMax, tfAmountMin, tfRemarks);
        tfInvoiceDateMax.setPlainText("");
        tfInvoiceDateMin.setPlainText("");
        tfDueDateMax.setPlainText("");
        tfDueDateMin.setPlainText("");
        tfCompletionDateMax.setPlainText("");
        tfCompletionDateMin.setPlainText("");
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        selectedSupplier = null;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        TextFieldUtils.setDigitTextFields(tfAmountMax, tfAmountMin);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
        setSupplierChooser(tfSupplier, this::handleSelectedSupplier, tfCompletionDateMin);
    }

    public void handleSelectedSupplier(ChooseResultVM<SupplierVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(supplier -> {
            selectedSupplier = supplier;
            tfSupplier.setText(supplier.getName());
        }, () -> {
            selectedSupplier = null;
            tfSupplier.setText("");
        });
    }

}

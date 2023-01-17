package pospino.desktop.controller.purchase;

import static com.gitlab.muhammadkholidb.toolbox.data.DateTimeUtils.parseLocalDateQuietly;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toStringOrNull;

import java.time.format.DateTimeFormatter;

import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.StringConstants;
import pospino.desktop.controller.CommonDataFilterController;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.PurchaseFilterVM;
import pospino.desktop.viewmodel.SupplierVM;

public class PurchaseFilterController extends CommonDataFilterController<PurchaseFilterVM> {

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
    private TextField tfTotalPaymentMin;

    @FXML
    private TextField tfTotalPaymentMax;

    @FXML
    private TextField tfTotalProductMin;

    @FXML
    private TextField tfTotalProductMax;

    private SupplierVM selectedSupplier;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        if (currentFilter != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN);
            tfInvoiceNumber.setText(currentFilter.getInvoiceNumber());
            tfSupplier.setText(currentFilter.getSupplierName());
            tfTotalPaymentMax.setText(toStringOrNull(currentFilter.getTotalPaymentMax()));
            tfTotalPaymentMin.setText(toStringOrNull(currentFilter.getTotalPaymentMin()));
            tfTotalProductMax.setText(toStringOrNull(currentFilter.getTotalProductMax()));
            tfTotalProductMin.setText(toStringOrNull(currentFilter.getTotalProductMin()));
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
    protected PurchaseFilterVM getFreshFilterValues() {
        PurchaseFilterVM filter = new PurchaseFilterVM();
        filter.setInvoiceNumber(tfInvoiceNumber.getText());
        filter.setInvoiceDateMax(
                parseLocalDateQuietly(tfInvoiceDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setInvoiceDateMin(
                parseLocalDateQuietly(tfInvoiceDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setDueDateMax(parseLocalDateQuietly(tfDueDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setDueDateMin(parseLocalDateQuietly(tfDueDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        PaymentStatus selectedPaymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        filter.setPaymentStatus(selectedPaymentStatus);
        if (selectedSupplier != null) {
            filter.setSupplierId(selectedSupplier.getId());
            filter.setSupplierName(selectedSupplier.getName());
        }
        filter.setTotalPaymentMax(toBigDecimalOrNull(tfTotalPaymentMax.getText()));
        filter.setTotalPaymentMin(toBigDecimalOrNull(tfTotalPaymentMin.getText()));
        filter.setTotalProductMax(toIntegerOrNull(tfTotalProductMax.getText()));
        filter.setTotalProductMin(toIntegerOrNull(tfTotalProductMin.getText()));
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(
                tfInvoiceNumber,
                tfSupplier,
                tfTotalPaymentMax,
                tfTotalPaymentMin,
                tfTotalProductMax,
                tfTotalProductMin);
        tfInvoiceDateMax.setPlainText("");
        tfInvoiceDateMin.setPlainText("");
        tfDueDateMax.setPlainText("");
        tfDueDateMin.setPlainText("");
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        selectedSupplier = null;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        TextFieldUtils.setDigitTextFields(tfTotalPaymentMax, tfTotalPaymentMin, tfTotalProductMax, tfTotalProductMin);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(StringConstants.EMPTY, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentStatus.PAID.toString(), t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID.toString(), t.translate(CommonLabel.LBL_UNPAID)));
        setSupplierChooser(tfSupplier, this::handleSelectedSupplier, tfTotalProductMin);
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

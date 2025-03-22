package pinodesk.controller.transaction.purchase;

import static com.mudiatech.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.mudiatech.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.mudiatech.toolbox.data.StringNumberUtils.toStringOrNull;

import com.mudiatech.pandora.model.SimpleComboBoxModel;
import com.mudiatech.pandora.utility.ComboBoxUtils;
import com.mudiatech.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.PaymentStatus;
import pinodesk.constant.StringConstants;
import pinodesk.controller.CommonDataFilterController;
import pinodesk.viewmodel.ChooseResultVM;
import pinodesk.viewmodel.PurchaseFilterVM;
import pinodesk.viewmodel.SupplierVM;

public class PurchaseFilterController extends CommonDataFilterController<PurchaseFilterVM> {

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private DatePicker dpInvoiceDateMin;

    @FXML
    private DatePicker dpInvoiceDateMax;

    @FXML
    private TextField tfSupplier;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private DatePicker dpDueDateMin;

    @FXML
    private DatePicker dpDueDateMax;

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
            tfInvoiceNumber.setText(currentFilter.getInvoiceNumber());
            tfSupplier.setText(currentFilter.getSupplierName());
            tfTotalProductMax.setText(toStringOrNull(currentFilter.getTotalProductMax()));
            tfTotalProductMin.setText(toStringOrNull(currentFilter.getTotalProductMin()));
            if (currentFilter.getTotalPaymentMax() != null) {
                tfTotalPaymentMax.setText(currentFilter.getTotalPaymentMax().doubleValue() + "");
            }
            if (currentFilter.getTotalPaymentMin() != null) {
                tfTotalPaymentMin.setText(currentFilter.getTotalPaymentMin().doubleValue() + "");
            }
            if (currentFilter.getInvoiceDateMin() != null) {
                dpInvoiceDateMin.setValue(currentFilter.getInvoiceDateMin());
            }
            if (currentFilter.getInvoiceDateMax() != null) {
                dpInvoiceDateMax.setValue(currentFilter.getInvoiceDateMax());
            }
            if (currentFilter.getDueDateMax() != null) {
                dpDueDateMax.setValue(currentFilter.getDueDateMax());
            }
            if (currentFilter.getDueDateMin() != null) {
                dpDueDateMin.setValue(currentFilter.getDueDateMin());
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
        filter.setInvoiceDateMax(dpInvoiceDateMax.getValue());
        filter.setInvoiceDateMin(dpInvoiceDateMin.getValue());
        filter.setDueDateMax(dpDueDateMax.getValue());
        filter.setDueDateMin(dpDueDateMin.getValue());
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
        dpInvoiceDateMax.setValue(null);
        dpInvoiceDateMin.setValue(null);
        dpDueDateMax.setValue(null);
        dpDueDateMin.setValue(null);
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        selectedSupplier = null;
    }

    @Override
    protected void initServices() {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        TextFieldUtils.setDecimalTextFields(tfTotalPaymentMax, tfTotalPaymentMin);
        TextFieldUtils.setDigitTextFields(tfTotalProductMax, tfTotalProductMin);
        initCustomDatePicker(dpDueDateMax, dpDueDateMin, dpInvoiceDateMax, dpInvoiceDateMin);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
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

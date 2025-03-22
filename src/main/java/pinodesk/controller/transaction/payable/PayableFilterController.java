package pinodesk.controller.transaction.payable;

import static com.mudiatech.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static pinodesk.constant.CommonConstants.DECIMAL_SCALE;

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
import pinodesk.viewmodel.PayableFilterVM;
import pinodesk.viewmodel.SupplierVM;

public class PayableFilterController extends CommonDataFilterController<PayableFilterVM> {

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
    private TextField tfAmountMin;

    @FXML
    private TextField tfAmountMax;

    @FXML
    private DatePicker dpCompletionDateMin;

    @FXML
    private DatePicker dpCompletionDateMax;

    @FXML
    private TextField tfRemarks;

    private SupplierVM selectedSupplier;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        if (currentFilter != null) {
            tfInvoiceNumber.setText(currentFilter.getInvoiceNumber());
            tfSupplier.setText(currentFilter.getSupplierName());
            tfRemarks.setText(currentFilter.getRemarks());
            if (currentFilter.getAmountMax() != null) {
                tfAmountMax.setText(currentFilter.getAmountMax().doubleValue() + "");
            }
            if (currentFilter.getAmountMin() != null) {
                tfAmountMin.setText(currentFilter.getAmountMin().doubleValue() + "");
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
            if (currentFilter.getCompletionDateMax() != null) {
                dpCompletionDateMax.setValue(currentFilter.getCompletionDateMax());
            }
            if (currentFilter.getCompletionDateMin() != null) {
                dpCompletionDateMin.setValue(currentFilter.getCompletionDateMin());
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
        filter.setInvoiceDateMax(dpInvoiceDateMax.getValue());
        filter.setInvoiceDateMin(dpInvoiceDateMin.getValue());
        filter.setDueDateMax(dpDueDateMax.getValue());
        filter.setDueDateMin(dpDueDateMin.getValue());
        filter.setCompletionDateMax(dpCompletionDateMax.getValue());
        filter.setCompletionDateMin(dpCompletionDateMin.getValue());
        PaymentStatus selectedPaymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        filter.setPaymentStatus(selectedPaymentStatus);
        if (selectedSupplier != null) {
            filter.setSupplierId(selectedSupplier.getId());
            filter.setSupplierName(selectedSupplier.getName());
        }
        filter.setAmountMax(toBigDecimalOrNull(tfAmountMax.getText(), DECIMAL_SCALE));
        filter.setAmountMin(toBigDecimalOrNull(tfAmountMin.getText(), DECIMAL_SCALE));
        filter.setRemarks(tfRemarks.getText());
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfInvoiceNumber, tfSupplier, tfAmountMax, tfAmountMin, tfRemarks);
        dpInvoiceDateMax.setValue(null);
        dpInvoiceDateMin.setValue(null);
        dpDueDateMax.setValue(null);
        dpDueDateMin.setValue(null);
        dpCompletionDateMax.setValue(null);
        dpCompletionDateMin.setValue(null);
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        selectedSupplier = null;
    }

    @Override
    protected void initServices() {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        initCustomDatePicker(
                dpInvoiceDateMax,
                dpInvoiceDateMin,
                dpDueDateMax,
                dpDueDateMin,
                dpCompletionDateMax,
                dpCompletionDateMin);
        TextFieldUtils.setDecimalTextFields(tfAmountMax, tfAmountMin);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
        setSupplierChooser(tfSupplier, this::handleSelectedSupplier, dpCompletionDateMin);
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

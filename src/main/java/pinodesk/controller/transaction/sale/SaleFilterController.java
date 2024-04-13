package pinodesk.controller.transaction.sale;

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
import javafx.scene.layout.VBox;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.PaymentStatus;
import pinodesk.constant.StringConstants;
import pinodesk.controller.CommonDataFilterController;
import pinodesk.viewmodel.ChooseResultVM;
import pinodesk.viewmodel.CustomerVM;
import pinodesk.viewmodel.DoctorVM;
import pinodesk.viewmodel.SaleFilterVM;

public class SaleFilterController extends CommonDataFilterController<SaleFilterVM> {

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private DatePicker dpDueDateMax;

    @FXML
    private DatePicker dpDueDateMin;

    @FXML
    private DatePicker dpCreatedDateMax;

    @FXML
    private DatePicker dpCreatedDateMin;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private TextField tfCustomer;

    @FXML
    private VBox vboxDoctor;

    @FXML
    private TextField tfDoctor;

    @FXML
    private TextField tfTotalPaymentMax;

    @FXML
    private TextField tfTotalPaymentMin;

    @FXML
    private TextField tfTotalProductMax;

    @FXML
    private TextField tfTotalProductMin;

    private CustomerVM selectedCustomer;
    private DoctorVM selectedDoctor;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        if (currentFilter != null) {
            tfInvoiceNumber.setText(currentFilter.getInvoiceNumber());
            tfCustomer.setText(currentFilter.getCustomerName());
            tfDoctor.setText(currentFilter.getDoctorName());
            tfTotalPaymentMax.setText(toStringOrNull(currentFilter.getTotalPaymentMax()));
            tfTotalPaymentMin.setText(toStringOrNull(currentFilter.getTotalPaymentMin()));
            tfTotalProductMax.setText(toStringOrNull(currentFilter.getTotalProductMax()));
            tfTotalProductMin.setText(toStringOrNull(currentFilter.getTotalProductMin()));
            if (currentFilter.getCreatedDateMin() != null) {
                dpCreatedDateMin.setValue(currentFilter.getCreatedDateMin());
            }
            if (currentFilter.getCreatedDateMax() != null) {
                dpCreatedDateMax.setValue(currentFilter.getCreatedDateMax());
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
        if (!isPharmacyFeatureEnabled()) {
            vboxDoctor.setVisible(false);
        }
    }

    @Override
    protected void initDataFilterControlActions() {
        initCustomDatePicker(dpCreatedDateMax, dpCreatedDateMin, dpDueDateMax, dpDueDateMin);
        TextFieldUtils.setDigitTextFields(tfTotalPaymentMax, tfTotalPaymentMin, tfTotalProductMax, tfTotalProductMin);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
        setCustomerChooser(
                tfCustomer,
                this::handleSelectedCustomer,
                isPharmacyFeatureEnabled() ? tfDoctor.getParent() : cbPaymentStatus.getParent());
        setDoctorChooser(tfDoctor, this::handleSelectedDoctor, cbPaymentStatus.getParent());
    }

    @Override
    protected SaleFilterVM getFreshFilterValues() {
        SaleFilterVM filter = new SaleFilterVM();
        filter.setInvoiceNumber(tfInvoiceNumber.getText());
        filter.setCreatedDateMax(dpCreatedDateMax.getValue());
        filter.setCreatedDateMin(dpCreatedDateMin.getValue());
        filter.setDueDateMax(dpDueDateMax.getValue());
        filter.setDueDateMin(dpDueDateMin.getValue());
        PaymentStatus selectedPaymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        filter.setPaymentStatus(selectedPaymentStatus);
        if (selectedCustomer != null) {
            filter.setCustomerId(selectedCustomer.getId());
            filter.setCustomerName(selectedCustomer.getName());
        }
        if (selectedDoctor != null) {
            filter.setDoctorId(selectedDoctor.getId());
            filter.setDoctorName(selectedDoctor.getName());
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
                tfCustomer,
                tfDoctor,
                tfTotalPaymentMax,
                tfTotalPaymentMin,
                tfTotalProductMax,
                tfTotalProductMin);
        dpCreatedDateMax.setValue(null);
        dpCreatedDateMin.setValue(null);
        dpDueDateMax.setValue(null);
        dpDueDateMin.setValue(null);
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        selectedCustomer = null;
        selectedDoctor = null;
    }

    @Override
    protected void initServices() {
        // No services to initialize
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

}

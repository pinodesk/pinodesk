package pospino.desktop.controller.sale;

import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toStringOrNull;

import com.gitlab.mudiasoft.pandora.model.SimpleComboBoxModel;
import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.StringConstants;
import pospino.desktop.controller.CommonDataFilterController;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.CustomerVM;
import pospino.desktop.viewmodel.DoctorVM;
import pospino.desktop.viewmodel.SaleFilterVM;

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
        setCustomerChooser(tfCustomer, this::handleSelectedCustomer, tfDoctor.getParent());
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

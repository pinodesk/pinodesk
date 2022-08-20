package stoready.desktop.controller.sale;

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
import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.CommonLabel;
import stoready.desktop.constant.PaymentStatus;
import stoready.desktop.constant.StringConstants;
import stoready.desktop.controller.CommonDataFilterController;
import stoready.desktop.viewmodel.ChooseResultVM;
import stoready.desktop.viewmodel.CustomerVM;
import stoready.desktop.viewmodel.DoctorVM;
import stoready.desktop.viewmodel.SaleFilterVM;

public class SaleFilterController extends CommonDataFilterController<SaleFilterVM> {

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private MaskedTextField tfDueDateMax;

    @FXML
    private MaskedTextField tfDueDateMin;

    @FXML
    private MaskedTextField tfCreatedDateMax;

    @FXML
    private MaskedTextField tfCreatedDateMin;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private TextField tfCustomer;

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
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN);
            tfInvoiceNumber.setText(currentFilter.getInvoiceNumber());
            tfCustomer.setText(currentFilter.getCustomerName());
            tfDoctor.setText(currentFilter.getDoctorName());
            tfTotalPaymentMax.setText(toStringOrNull(currentFilter.getTotalPaymentMax()));
            tfTotalPaymentMin.setText(toStringOrNull(currentFilter.getTotalPaymentMin()));
            tfTotalProductMax.setText(toStringOrNull(currentFilter.getTotalProductMax()));
            tfTotalProductMin.setText(toStringOrNull(currentFilter.getTotalProductMin()));
            if (currentFilter.getCreatedDateMin() != null) {
                tfCreatedDateMin.setText(currentFilter.getCreatedDateMin().format(dateFormatter));
            }
            if (currentFilter.getCreatedDateMax() != null) {
                tfCreatedDateMax.setText(currentFilter.getCreatedDateMax().format(dateFormatter));
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
    protected void initDataFilterControlActions() {
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
        filter.setCreatedDateMax(
                parseLocalDateQuietly(tfCreatedDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setCreatedDateMin(
                parseLocalDateQuietly(tfCreatedDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setDueDateMax(parseLocalDateQuietly(tfDueDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setDueDateMin(parseLocalDateQuietly(tfDueDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
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
        tfCreatedDateMax.setPlainText("");
        tfCreatedDateMin.setPlainText("");
        tfDueDateMax.setPlainText("");
        tfDueDateMin.setPlainText("");
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        selectedCustomer = null;
        selectedDoctor = null;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
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

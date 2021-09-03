package toscabox.desktop.controller.purchase;

import java.time.format.DateTimeFormatter;

import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import toscabox.desktop.constant.CommonConstants;
import toscabox.desktop.constant.Page;
import toscabox.desktop.constant.PaymentMethod;
import toscabox.desktop.constant.PaymentPeriodUnit;
import toscabox.desktop.constant.PaymentStatus;
import toscabox.desktop.constant.StringConstants;
import toscabox.desktop.controller.CommonDataFilterController;
import toscabox.desktop.viewmodel.PurchaseFilterVM;
import toscabox.desktop.viewmodel.SupplierVM;

public class PurchaseFilterController extends CommonDataFilterController<PurchaseFilterVM> {

    @FXML
    private TextField tfOrderNumber;

    @FXML
    private MaskedTextField tfOrderDateMin;

    @FXML
    private MaskedTextField tfOrderDateMax;

    @FXML
    private TextField tfSupplier;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentMethod;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentPeriod;

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
        if (currentFilter != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN);
            tfOrderNumber.setText(currentFilter.getOrderNumber());
            tfSupplier.setText(toStringOrNull(currentFilter.getSupplierId()));
            tfTotalPaymentMax.setText(toStringOrNull(currentFilter.getTotalPaymentMax()));
            tfTotalPaymentMin.setText(toStringOrNull(currentFilter.getTotalPaymentMin()));
            tfTotalProductMax.setText(toStringOrNull(currentFilter.getTotalProductMax()));
            tfTotalProductMin.setText(toStringOrNull(currentFilter.getTotalProductMin()));
            if (currentFilter.getOrderDateMin() != null) {
                tfOrderDateMin.setText(currentFilter.getOrderDateMin().format(dateFormatter));
            }
            if (currentFilter.getOrderDateMax() != null) {
                tfOrderDateMax.setText(currentFilter.getOrderDateMax().format(dateFormatter));
            }
            if (currentFilter.getDueDateMax() != null) {
                tfDueDateMax.setText(currentFilter.getDueDateMax().format(dateFormatter));
            }
            if (currentFilter.getDueDateMin() != null) {
                tfDueDateMin.setText(currentFilter.getDueDateMin().format(dateFormatter));
            }
            if (currentFilter.getPaymentMethod() != null) {
                ComboBoxUtils.select(cbPaymentMethod,
                        () -> cbPaymentMethod.getItems().stream()
                                .filter(vm -> currentFilter.getPaymentMethod().name().equals(vm.getValue())).findAny()
                                .orElseThrow());
            }
            if (currentFilter.getPaymentPeriodUnit() != null) {
                ComboBoxUtils.select(cbPaymentPeriod,
                        () -> cbPaymentPeriod.getItems().stream()
                                .filter(vm -> currentFilter.getPaymentPeriodUnit().name().equals(vm.getValue()))
                                .findAny().orElseThrow());
            }
            if (currentFilter.getPaymentStatus() != null) {
                ComboBoxUtils.select(cbPaymentStatus,
                        () -> cbPaymentStatus.getItems().stream()
                                .filter(vm -> currentFilter.getPaymentStatus().name().equals(vm.getValue())).findAny()
                                .orElseThrow());
            }
        }
    }

    @Override
    protected PurchaseFilterVM getFreshFilterValues() {
        PurchaseFilterVM filter = new PurchaseFilterVM();
        filter.setOrderNumber(tfOrderNumber.getText());
        filter.setOrderDateMax(parseDateQuietly(tfOrderDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setOrderDateMin(parseDateQuietly(tfOrderDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setDueDateMax(parseDateQuietly(tfDueDateMax.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        filter.setDueDateMin(parseDateQuietly(tfDueDateMin.getText(), CommonConstants.DATE_DISPLAY_PATTERN));
        SimpleComboBoxModel selectedPaymentMethod = ComboBoxUtils.getSelectedItem(cbPaymentMethod);
        SimpleComboBoxModel selectedPaymentPeriod = ComboBoxUtils.getSelectedItem(cbPaymentPeriod);
        SimpleComboBoxModel selectedPaymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus);
        filter.setPaymentMethod(selectedPaymentMethod == null || selectedPaymentMethod.getValue().isEmpty() ? null
                : PaymentMethod.valueOf(ComboBoxUtils.getSelectedItem(cbPaymentMethod).getValue()));
        filter.setPaymentPeriodUnit(selectedPaymentPeriod == null || selectedPaymentPeriod.getValue().isEmpty() ? null
                : PaymentPeriodUnit.valueOf(ComboBoxUtils.getSelectedItem(cbPaymentPeriod).getValue()));
        filter.setPaymentStatus(selectedPaymentStatus == null || selectedPaymentStatus.getValue().isEmpty() ? null
                : PaymentStatus.valueOf(ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue()));
        filter.setSupplierId(selectedSupplier == null ? null : selectedSupplier.getId());
        filter.setTotalPaymentMax(toBigDecimalOrNull(tfTotalPaymentMax.getText()));
        filter.setTotalPaymentMin(toBigDecimalOrNull(tfTotalPaymentMin.getText()));
        filter.setTotalProductMax(toIntegerOrNull(tfTotalProductMax.getText()));
        filter.setTotalProductMin(toIntegerOrNull(tfTotalProductMin.getText()));
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfOrderNumber, tfSupplier, tfTotalPaymentMax, tfTotalPaymentMin, tfTotalProductMax,
                tfTotalProductMin);
        tfOrderDateMax.setPlainText("");
        tfOrderDateMin.setPlainText("");
        tfDueDateMax.setPlainText("");
        tfDueDateMin.setPlainText("");
        ComboBoxUtils.selectIndex(cbPaymentMethod, 0);
        ComboBoxUtils.selectIndex(cbPaymentPeriod, 0);
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        TextFieldUtils.setDigitTextFields(tfTotalPaymentMax, tfTotalPaymentMin, tfTotalProductMax, tfTotalProductMin);
        ComboBoxUtils.initSimple(cbPaymentMethod, new SimpleComboBoxModel(StringConstants.EMPTY, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentMethod.CASH.name(), translate("lbl.cash")),
                new SimpleComboBoxModel(PaymentMethod.CREDIT.name(), translate("lbl.credit")));
        ComboBoxUtils.initSimple(cbPaymentPeriod, new SimpleComboBoxModel(StringConstants.EMPTY, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentPeriodUnit.DAY.name(), translate("lbl.day")),
                new SimpleComboBoxModel(PaymentPeriodUnit.WEEK.name(), translate("lbl.week")),
                new SimpleComboBoxModel(PaymentPeriodUnit.MONTH.name(), translate("lbl.month")));
        ComboBoxUtils.initSimple(cbPaymentStatus, new SimpleComboBoxModel(StringConstants.EMPTY, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentStatus.PAID.name(), translate("lbl.paid")),
                new SimpleComboBoxModel(PaymentStatus.UNPAID.name(), translate("lbl.unpaid")));
        tfSupplier.focusedProperty().addListener((o, ov, nv) -> {
            if (Boolean.TRUE.equals(nv)) {
                StageUtils.modal(Page.MASTER_SUPPLIER_CHOOSE, false, we -> {
                    selectedSupplier = getPageData();
                    if (selectedSupplier != null) {
                        tfSupplier.setText(selectedSupplier.getName());
                    }
                });
                setFocused(cbPaymentMethod);
            }
        });
    }

}

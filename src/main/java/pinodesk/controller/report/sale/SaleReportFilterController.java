package pinodesk.controller.report.sale;

import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.PaymentStatus;
import pinodesk.constant.SellingMode;
import pinodesk.constant.StringConstants;
import pinodesk.controller.CommonDataFilterController;
import pinodesk.viewmodel.SaleReportFilterVM;

public class SaleReportFilterController extends CommonDataFilterController<SaleReportFilterVM> {

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbSellingMode;

    @FXML
    private DatePicker dpInvoiceDateMax;

    @FXML
    private DatePicker dpInvoiceDateMin;

    @FXML
    private TextField tfCustomerName;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private TextField tfProductName;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        ComboBoxUtils.selectIndex(cbSellingMode, 0);
        if (currentFilter != null) {
            tfInvoiceNumber.setText(currentFilter.getInvoiceNumber());
            tfCustomerName.setText(currentFilter.getCustomerName());
            tfProductName.setText(currentFilter.getProductName());
            dpInvoiceDateMax.setValue(currentFilter.getInvoiceDateMax());
            dpInvoiceDateMin.setValue(currentFilter.getInvoiceDateMin());
            if (currentFilter.getPaymentStatus() != null) {
                ComboBoxUtils.select(
                        cbPaymentStatus,
                        () -> cbPaymentStatus.getItems().stream()
                                .filter(vm -> currentFilter.getPaymentStatus().equals(vm.getValue())).findAny()
                                .orElseThrow());
            }
            if (currentFilter.getSellingMode() != null) {
                ComboBoxUtils.select(
                        cbSellingMode,
                        () -> cbSellingMode.getItems().stream()
                                .filter(vm -> currentFilter.getSellingMode().equals(vm.getValue())).findAny()
                                .orElseThrow());
            }
        }
    }

    @Override
    protected void initDataFilterControlActions() {
        initCustomDatePicker(dpInvoiceDateMin, dpInvoiceDateMax);
        ComboBoxUtils.initSimple(
                cbPaymentStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(PaymentStatus.PAID, t.translate(CommonLabel.LBL_PAID)),
                new SimpleComboBoxModel(PaymentStatus.UNPAID, t.translate(CommonLabel.LBL_UNPAID)));
        ComboBoxUtils.initSimple(
                cbSellingMode,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(SellingMode.GENERAL, t.translate(CommonLabel.LBL_GENERAL)),
                new SimpleComboBoxModel(SellingMode.PRESCRIPTION, t.translate(CommonLabel.LBL_PRESCRIPTION)));
    }

    @Override
    protected SaleReportFilterVM getFreshFilterValues() {
        SaleReportFilterVM filter = new SaleReportFilterVM();
        filter.setInvoiceNumber(tfInvoiceNumber.getText());
        filter.setCustomerName(tfCustomerName.getText());
        filter.setProductName(tfProductName.getText());
        filter.setInvoiceDateMax(dpInvoiceDateMax.getValue());
        filter.setInvoiceDateMin(dpInvoiceDateMin.getValue());
        PaymentStatus selectedPaymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        filter.setPaymentStatus(selectedPaymentStatus);
        SellingMode selectSellingMode = ComboBoxUtils.getSelectedItem(cbSellingMode).getValue();
        filter.setSellingMode(selectSellingMode);
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfInvoiceNumber, tfCustomerName, tfProductName);
        dpInvoiceDateMax.setValue(null);
        dpInvoiceDateMin.setValue(null);
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        ComboBoxUtils.selectIndex(cbSellingMode, 0);
    }

    @Override
    protected void initServices() {
        // No services to initialize
    }

}

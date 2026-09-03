package com.pinodesk.controller.report.purchase;

import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.PaymentStatus;
import com.pinodesk.constant.StringConstants;
import com.pinodesk.controller.CommonDataFilterController;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.TextFieldUtils;
import com.pinodesk.viewmodel.PurchaseReportFilterVM;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class PurchaseReportFilterController extends CommonDataFilterController<PurchaseReportFilterVM> {

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPaymentStatus;

    @FXML
    private DatePicker dpInvoiceDateMax;

    @FXML
    private DatePicker dpInvoiceDateMin;

    @FXML
    private TextField tfSupplierName;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private TextField tfProductName;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
        if (currentFilter != null) {
            tfInvoiceNumber.setText(currentFilter.getInvoiceNumber());
            tfSupplierName.setText(currentFilter.getSupplierName());
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
    }

    @Override
    protected PurchaseReportFilterVM getFreshFilterValues() {
        PurchaseReportFilterVM filter = new PurchaseReportFilterVM();
        filter.setInvoiceNumber(tfInvoiceNumber.getText());
        filter.setSupplierName(tfSupplierName.getText());
        filter.setProductName(tfProductName.getText());
        filter.setInvoiceDateMax(dpInvoiceDateMax.getValue());
        filter.setInvoiceDateMin(dpInvoiceDateMin.getValue());
        PaymentStatus selectedPaymentStatus = ComboBoxUtils.getSelectedItem(cbPaymentStatus).getValue();
        filter.setPaymentStatus(selectedPaymentStatus);
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfInvoiceNumber, tfSupplierName, tfProductName);
        dpInvoiceDateMax.setValue(null);
        dpInvoiceDateMin.setValue(null);
        ComboBoxUtils.selectIndex(cbPaymentStatus, 0);
    }

    @Override
    protected void initServices() {
        // No services to initialize
    }

}

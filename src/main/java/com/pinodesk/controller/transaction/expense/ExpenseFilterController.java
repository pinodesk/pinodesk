package com.pinodesk.controller.transaction.expense;

import static com.pinodesk.constant.CommonConstants.DECIMAL_SCALE;
import static com.pinodesk.toolbox.data.StringNumberUtils.toBigDecimalOrNull;

import com.pinodesk.controller.CommonDataFilterController;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.entity.PaymentMethod;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.TextFieldUtils;
import com.pinodesk.util.ExpenseCategoryControls;
import com.pinodesk.util.PaymentMethodControls;
import com.pinodesk.viewmodel.ExpenseFilterVM;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class ExpenseFilterController extends CommonDataFilterController<ExpenseFilterVM> {

    @FXML
    private TextField tfTitle;

    @FXML
    private ComboBox<ExpenseCategory> cbExpenseCategory;

    @FXML
    private TextField tfNominalMin;

    @FXML
    private TextField tfNominalMax;

    @FXML
    private DatePicker dpExpenseDateMin;

    @FXML
    private DatePicker dpExpenseDateMax;

    @FXML
    private ComboBox<PaymentMethod> cbPaymentMethod;

    @Override
    protected void initDataFilterControlValues() {
        if (currentFilter != null) {
            tfTitle.setText(currentFilter.getTitle());
            if (currentFilter.getNominalMax() != null) {
                tfNominalMax.setText(currentFilter.getNominalMax().doubleValue() + "");
            }
            if (currentFilter.getNominalMin() != null) {
                tfNominalMin.setText(currentFilter.getNominalMin().doubleValue() + "");
            }
            if (currentFilter.getExpenseDateMin() != null) {
                dpExpenseDateMin.setValue(currentFilter.getExpenseDateMin());
            }
            if (currentFilter.getExpenseDateMax() != null) {
                dpExpenseDateMax.setValue(currentFilter.getExpenseDateMax());
            }
            ExpenseCategoryControls
                    .initialize(cbExpenseCategory, resources, currentFilter.getExpenseCategoryId(), true);
            PaymentMethodControls.initialize(cbPaymentMethod, resources, currentFilter.getPaymentMethodId(), true);
        } else {
            ExpenseCategoryControls.initialize(cbExpenseCategory, resources, null, true);
            PaymentMethodControls.initialize(cbPaymentMethod, resources, null, true);
        }
    }

    @Override
    protected ExpenseFilterVM getFreshFilterValues() {
        ExpenseFilterVM filter = new ExpenseFilterVM();
        filter.setTitle(tfTitle.getText());
        filter.setNominalMax(toBigDecimalOrNull(tfNominalMax.getText(), DECIMAL_SCALE));
        filter.setNominalMin(toBigDecimalOrNull(tfNominalMin.getText(), DECIMAL_SCALE));
        filter.setExpenseDateMax(dpExpenseDateMax.getValue());
        filter.setExpenseDateMin(dpExpenseDateMin.getValue());
        ExpenseCategory selectedCategory = ComboBoxUtils.getSelectedItem(cbExpenseCategory);
        if (selectedCategory != null) {
            filter.setExpenseCategoryId(selectedCategory.getId());
            filter.setExpenseCategoryName(selectedCategory.getName());
        }
        PaymentMethod selectedPaymentMethod = ComboBoxUtils.getSelectedItem(cbPaymentMethod);
        if (selectedPaymentMethod != null) {
            filter.setPaymentMethodId(selectedPaymentMethod.getId());
            filter.setPaymentMethodName(selectedPaymentMethod.getName());
        }
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfTitle, tfNominalMax, tfNominalMin);
        dpExpenseDateMax.setValue(null);
        dpExpenseDateMin.setValue(null);
        ExpenseCategoryControls.initialize(cbExpenseCategory, resources, null, true);
        PaymentMethodControls.initialize(cbPaymentMethod, resources, null, true);
    }

    @Override
    protected void initServices() {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        initCustomDatePicker(dpExpenseDateMax, dpExpenseDateMin);
        TextFieldUtils.setDecimalTextFields(tfNominalMax, tfNominalMin);
        ExpenseCategoryControls.initialize(cbExpenseCategory, resources, null, true);
        PaymentMethodControls.initialize(cbPaymentMethod, resources, null, true);
    }

}

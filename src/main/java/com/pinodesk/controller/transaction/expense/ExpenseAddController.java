package com.pinodesk.controller.transaction.expense;

import static com.pinodesk.constant.CommonConstants.DECIMAL_SCALE;
import static com.pinodesk.toolbox.data.StringNumberUtils.toBigDecimalOrNull;

import java.time.LocalDate;

import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.Page;
import com.pinodesk.controller.CommonDataSaveController;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.entity.PaymentMethod;
import com.pinodesk.pandora.constant.KeyConstants;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.pandora.utility.TextFieldUtils;
import com.pinodesk.service.ExpenseService;
import com.pinodesk.util.ExpenseCategoryControls;
import com.pinodesk.util.PaymentMethodControls;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ExpenseAddVM;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ExpenseAddController extends CommonDataSaveController {

    @FXML
    private TextField tfTitle;

    @FXML
    private ComboBox<ExpenseCategory> cbExpenseCategory;

    @FXML
    private TextField tfNominal;

    @FXML
    private DatePicker dpExpenseDate;

    @FXML
    private ComboBox<PaymentMethod> cbPaymentMethod;

    @FXML
    private TextArea taNotes;

    @FXML
    private Button btnSaveAndAdd;

    @FXML
    private Button btnNewExpenseCategory;

    @FXML
    private Button btnNewPaymentMethod;

    private ExpenseService expenseService;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_EXPENSE);
            resetControls();
            initDataSaveControlValues();
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        disableWriteAction(MenuCodeConstants.TRANSACTION_EXPENSES, btnSave, btnSaveAndAdd);
        initCustomDatePicker(dpExpenseDate);
        TextFieldUtils.setDecimalTextFields(tfNominal);
        ExpenseCategoryControls.initialize(cbExpenseCategory, resources, null);
        PaymentMethodControls.initialize(cbPaymentMethod, resources, null);
        setFocused(tfTitle);
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyConstants.CTRL_SHIFT_S.match(event)) {
                btnSaveAndAdd.fire();
                return;
            }
        });
    }

    @FXML
    void onActionBtnNewExpenseCategory(ActionEvent event) {
        StageUtils.modal(Page.SETTINGS_EXPENSE_CATEGORY_ADD, false, we -> {
            if (getPageData() != null) {
                Long selectedId = getPageData() instanceof ExpenseCategory cat ?
                        cat.getId() : (ComboBoxUtils.getSelectedItem(cbExpenseCategory) != null ?
                                ComboBoxUtils.getSelectedItem(cbExpenseCategory).getId() : null);
                ExpenseCategoryControls.initialize(cbExpenseCategory, resources, selectedId);
            }
        });
    }

    @FXML
    void onActionBtnNewPaymentMethod(ActionEvent event) {
        StageUtils.modal(Page.SETTINGS_PAYMENT_METHOD_ADD, false, we -> {
            if (getPageData() != null) {
                Long selectedId = getPageData() instanceof PaymentMethod pm ?
                        pm.getId() : (ComboBoxUtils.getSelectedItem(cbPaymentMethod) != null ?
                                ComboBoxUtils.getSelectedItem(cbPaymentMethod).getId() : null);
                PaymentMethodControls.initialize(cbPaymentMethod, resources, selectedId);
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        dpExpenseDate.setValue(LocalDate.now());
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfTitle, MessageCode.ERROR_EMPTY_TITLE);
        validator.validateCustom(
                () -> ComboBoxUtils.getSelectedItem(cbExpenseCategory) == null,
                MessageCode.ERROR_EMPTY_EXPENSE_CATEGORY);
        validator.validatePositive(tfNominal, MessageCode.ERROR_EMPTY_NOMINAL);
        validator.validateCustom(() -> dpExpenseDate.getValue() == null, MessageCode.ERROR_EMPTY_EXPENSE_DATE);
        validator.validateCustom(
                () -> ComboBoxUtils.getSelectedItem(cbPaymentMethod) == null,
                MessageCode.ERROR_EMPTY_PAYMENT_METHOD);
    }

    @Override
    protected Object save() {
        ExpenseAddVM expenseAdd = new ExpenseAddVM();
        expenseAdd.setTitle(tfTitle.getText().trim());
        expenseAdd.setExpenseCategoryId(ComboBoxUtils.getSelectedItem(cbExpenseCategory).getId());
        expenseAdd.setNominal(toBigDecimalOrNull(tfNominal.getText(), DECIMAL_SCALE));
        expenseAdd.setExpenseDate(dpExpenseDate.getValue());
        expenseAdd.setPaymentMethodId(ComboBoxUtils.getSelectedItem(cbPaymentMethod).getId());
        expenseAdd.setNotes(taNotes.getText().trim());
        expenseService.createExpense(expenseAdd);
        return true;
    }

    @Override
    protected void initServices() {
        expenseService = SpringUtils.getBean(ExpenseService.class);
    }

    private void resetControls() {
        TextFieldUtils.setTextEmpty(tfTitle, tfNominal);
        taNotes.setText("");
        ExpenseCategoryControls.initialize(cbExpenseCategory, resources, null);
        PaymentMethodControls.initialize(cbPaymentMethod, resources, null);
        setFocused(tfTitle);
    }

}

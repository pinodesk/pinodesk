package com.pinodesk.controller.transaction.receivable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.StyleConstants;
import com.pinodesk.controller.CommonDataSaveController;
import com.pinodesk.pandora.factory.LocalDateCellFactory;
import com.pinodesk.pandora.factory.NumberCellFactory;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.TableViewUtils;
import com.pinodesk.pandora.utility.TextFieldUtils;
import com.pinodesk.pandora.utility.ValidationResult;
import com.pinodesk.service.ReceivableService;
import com.pinodesk.toolbox.data.StringNumberUtils;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ReceivableEditVM;
import com.pinodesk.viewmodel.ReceivablePaymentVM;
import com.pinodesk.viewmodel.ReceivableVM;

import static com.pinodesk.constant.CommonConstants.DECIMAL_SCALE;
import static com.pinodesk.toolbox.data.StringNumberUtils.toBigDecimalOrNull;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ReceivableEditController extends CommonDataSaveController {

    @FXML
    private TextField tfCustomer;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private DatePicker dpInvoiceDate;

    @FXML
    private TextField tfReceivableAmount;

    @FXML
    private DatePicker dpDueDate;

    @FXML
    private DatePicker dpPaymentDate;

    @FXML
    private TextField tfPaymentAmount;

    @FXML
    private Button btnAddPayment;

    @FXML
    private Button btnRemovePayment;

    @FXML
    private TableView<ReceivablePaymentVM> tblPayments;

    @FXML
    private TableColumn<ReceivablePaymentVM, BigDecimal> colAmount;

    @FXML
    private TableColumn<ReceivablePaymentVM, LocalDate> colPaymentDate;

    private ReceivableService receivableService;
    private ReceivableVM currentReceivable;

    @FXML
    void onActionBtnAddPayment(ActionEvent event) {
        LocalDate paymentDate = dpPaymentDate.getValue();
        ValidationResult validationResult = validateAddPayment(paymentDate);
        if (!validationResult.isValid()) {
            displayError(validationResult.getMessages());
            return;
        }
        Predicate<ReceivablePaymentVM> predicate = vm -> vm.getPaymentDate().equals(paymentDate);
        int idx = TableViewUtils.getItemIndex(predicate, tblPayments);
        if (idx != -1) {
            tblPayments.getItems().remove(idx);
        }
        ReceivablePaymentVM vm = new ReceivablePaymentVM();
        vm.setAmount(toBigDecimalOrNull(tfPaymentAmount.getText(), DECIMAL_SCALE));
        vm.setPaymentDate(paymentDate);
        tblPayments.getItems().add(vm);
        tfPaymentAmount.setText("");
        dpPaymentDate.setValue(null);
    }

    @FXML
    void onActionBtnRemovePayment(ActionEvent event) {
        if (TableViewUtils.hasItemSelected(tblPayments)) {
            tblPayments.getItems().removeAll(TableViewUtils.getSelectedItems(tblPayments));
        }
        if (tblPayments.getItems().isEmpty()) {
            tblPayments.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        disableWriteAction(MenuCodeConstants.TRANSACTION_PAYABLES, btnSave, btnAddPayment, btnRemovePayment);
        initCustomDatePicker(dpInvoiceDate, dpDueDate, dpPaymentDate);
        tblPayments.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblPayments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableViewUtils.initTableColumn(
                colAmount,
                new NumberCellFactory<>(DECIMAL_SCALE, resources.getLocale()),
                ReceivablePaymentVM::getAmount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPaymentDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivablePaymentVM::getPaymentDate);
        TextFieldUtils.setDecimalTextFields(tfReceivableAmount, tfPaymentAmount);
        setFocused(tfPaymentAmount);
    }

    @Override
    protected void initDataSaveControlValues() {
        currentReceivable = getPageData();
        tfCustomer.setText(currentReceivable.getCustomerName());
        tfInvoiceNumber.setText(currentReceivable.getInvoiceNumber());
        dpInvoiceDate.setValue(currentReceivable.getInvoiceDate());
        tfReceivableAmount.setText(currentReceivable.getAmount().doubleValue() + "");
        dpDueDate.setValue(currentReceivable.getDueDate());
        List<ReceivablePaymentVM> payments = receivableService.getReceivablePayments(currentReceivable.getId());
        tblPayments.getItems().addAll(payments);
    }

    @Override
    protected void validate(ControlValidator validator) {
        // Nothing to validate
    }

    @Override
    protected Object save() {
        ReceivableEditVM receivableEdit = new ReceivableEditVM();
        receivableEdit.setPayments(tblPayments.getItems());
        receivableService.updateReceivable(receivableEdit, currentReceivable.getId());
        return true;
    }

    @Override
    protected void initServices() {
        receivableService = SpringUtils.getBean(ReceivableService.class);
    }

    private ValidationResult validateAddPayment(LocalDate paymentDate) {
        ControlValidator cv = new ControlValidator(resources);
        cv.validateCustom(() -> paymentDate == null, MessageCode.ERROR_INVALID_PAYMENT_DATE);
        cv.validatePositive(tfPaymentAmount, MessageCode.ERROR_INVALID_AMOUNT);
        cv.validateCustom(() -> {
            BigDecimal total = StringNumberUtils.toBigDecimalOrZero(tfPaymentAmount.getText());
            for (ReceivablePaymentVM vm : tblPayments.getItems()) {
                total = total.add(vm.getAmount());
            }
            return total.compareTo(currentReceivable.getAmount()) > 0;
        }, MessageCode.ERROR_PAYMENT_AMOUNT_GREATER_THAN_RECEIVABLE_AMOUNT);
        return cv.getResult();
    }

}

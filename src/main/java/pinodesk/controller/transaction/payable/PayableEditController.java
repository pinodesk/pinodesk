package pinodesk.controller.transaction.payable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import com.mudiatech.pandora.factory.LocalDateCellFactory;
import com.mudiatech.pandora.factory.NumberCellFactory;
import com.mudiatech.pandora.utility.ControlValidator;
import com.mudiatech.pandora.utility.TableViewUtils;
import com.mudiatech.pandora.utility.TextFieldUtils;
import com.mudiatech.pandora.utility.ValidationResult;
import com.mudiatech.toolbox.data.StringNumberUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.MenuCodeConstants;
import pinodesk.constant.MessageCode;
import pinodesk.constant.StyleConstants;
import pinodesk.controller.CommonDataSaveController;
import pinodesk.service.PayableService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.PayableEditVM;
import pinodesk.viewmodel.PayablePaymentVM;
import pinodesk.viewmodel.PayableVM;

public class PayableEditController extends CommonDataSaveController {

    @FXML
    private TextField tfSupplier;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private DatePicker dpInvoiceDate;

    @FXML
    private TextField tfPayableAmount;

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
    private TableView<PayablePaymentVM> tblPayments;

    @FXML
    private TableColumn<PayablePaymentVM, BigDecimal> colAmount;

    @FXML
    private TableColumn<PayablePaymentVM, LocalDate> colPaymentDate;

    private PayableService payableService;
    private PayableVM currentPayable;

    @FXML
    void onActionBtnAddPayment(ActionEvent event) {
        LocalDate paymentDate = dpPaymentDate.getValue();
        ValidationResult validationResult = validateAddPayment(paymentDate);
        if (!validationResult.isValid()) {
            displayError(validationResult.getMessages());
            return;
        }
        Predicate<PayablePaymentVM> predicate = vm -> vm.getPaymentDate().equals(paymentDate);
        int idx = TableViewUtils.getItemIndex(predicate, tblPayments);
        if (idx != -1) {
            tblPayments.getItems().remove(idx);
        }
        PayablePaymentVM vm = new PayablePaymentVM();
        vm.setAmount(StringNumberUtils.toBigDecimalOrNull(tfPaymentAmount.getText()));
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
                new NumberCellFactory<>(resources.getLocale()),
                PayablePaymentVM::getAmount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPaymentDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                PayablePaymentVM::getPaymentDate);
        TextFieldUtils.setDigitTextFields(tfPayableAmount);
        setFocused(tfPaymentAmount);
    }

    @Override
    protected void initDataSaveControlValues() {
        currentPayable = getPageData();
        tfSupplier.setText(currentPayable.getSupplierName());
        tfInvoiceNumber.setText(currentPayable.getInvoiceNumber());
        dpInvoiceDate.setValue(currentPayable.getInvoiceDate());
        tfPayableAmount.setText(StringNumberUtils.toStringOrEmpty(currentPayable.getAmount()));
        dpDueDate.setValue(currentPayable.getDueDate());
        List<PayablePaymentVM> payments = payableService.getPayablePayments(currentPayable.getId());
        tblPayments.getItems().addAll(payments);
    }

    @Override
    protected void validate(ControlValidator validator) {
        // Nothing to validate
    }

    @Override
    protected Object save() {
        PayableEditVM payableEdit = new PayableEditVM();
        payableEdit.setPayments(tblPayments.getItems());
        payableService.updatePayable(payableEdit, currentPayable.getId());
        return true;
    }

    @Override
    protected void initServices() {
        payableService = SpringUtils.getBean(PayableService.class);
    }

    private ValidationResult validateAddPayment(LocalDate paymentDate) {
        ControlValidator cv = new ControlValidator(resources);
        cv.validateCustom(() -> paymentDate == null, MessageCode.ERROR_INVALID_PAYMENT_DATE);
        cv.validatePositive(tfPaymentAmount, MessageCode.ERROR_INVALID_AMOUNT);
        cv.validateCustom(() -> {
            BigDecimal total = StringNumberUtils.toBigDecimalOrZero(tfPaymentAmount.getText());
            for (PayablePaymentVM vm : tblPayments.getItems()) {
                total = total.add(vm.getAmount());
            }
            return total.compareTo(currentPayable.getAmount()) > 0;
        }, MessageCode.ERROR_PAYMENT_AMOUNT_GREATER_THAN_PAYABLE_AMOUNT);
        return cv.getResult();
    }

}

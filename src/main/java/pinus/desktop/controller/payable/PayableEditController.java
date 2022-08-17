package pinus.desktop.controller.payable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.factory.LocalDateCellFactory;
import com.gitlab.muhammadkholidb.pandora.factory.NumberCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;
import com.gitlab.muhammadkholidb.pandora.utility.ValidationResult;
import com.gitlab.muhammadkholidb.toolbox.data.DateTimeUtils;
import com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.MenuCodeConstants;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.constant.StyleConstants;
import pinus.desktop.controller.CommonDataSaveController;
import pinus.desktop.service.PayableService;
import pinus.desktop.viewmodel.PayableEditVM;
import pinus.desktop.viewmodel.PayablePaymentVM;
import pinus.desktop.viewmodel.PayableVM;

public class PayableEditController extends CommonDataSaveController {

    @FXML
    private TextField tfSupplier;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private MaskedTextField tfInvoiceDate;

    @FXML
    private TextField tfPayableAmount;

    @FXML
    private MaskedTextField tfDueDate;

    @FXML
    private MaskedTextField tfPaymentDate;

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
        LocalDate paymentDate = DateTimeUtils
                .parseLocalDateQuietly(tfPaymentDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN);
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
        tfPaymentDate.setPlainText("");
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
    }

    @Override
    protected void initDataSaveControlValues() {
        currentPayable = getPageData();
        tfSupplier.setText(currentPayable.getSupplierName());
        tfInvoiceNumber.setText(currentPayable.getInvoiceNumber());
        tfInvoiceDate.setText(
                currentPayable.getInvoiceDate()
                        .format(DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN)));
        tfPayableAmount.setText(StringNumberUtils.toStringOrEmpty(currentPayable.getAmount()));
        tfDueDate.setText(
                currentPayable.getDueDate().format(DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN)));
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
    protected void initServices(ApplicationContext ctx) {
        payableService = ctx.getBean(PayableService.class);
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

package pospino.desktop.controller.receivable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

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
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.service.ReceivableService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.ReceivableEditVM;
import pospino.desktop.viewmodel.ReceivablePaymentVM;
import pospino.desktop.viewmodel.ReceivableVM;

public class ReceivableEditController extends CommonDataSaveController {

    @FXML
    private TextField tfCustomer;

    @FXML
    private TextField tfInvoiceNumber;

    @FXML
    private MaskedTextField tfInvoiceDate;

    @FXML
    private TextField tfReceivableAmount;

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
    private TableView<ReceivablePaymentVM> tblPayments;

    @FXML
    private TableColumn<ReceivablePaymentVM, BigDecimal> colAmount;

    @FXML
    private TableColumn<ReceivablePaymentVM, LocalDate> colPaymentDate;

    private ReceivableService receivableService;
    private ReceivableVM currentReceivable;

    @FXML
    void onActionBtnAddPayment(ActionEvent event) {
        LocalDate paymentDate = DateTimeUtils
                .parseLocalDateQuietly(tfPaymentDate.getText(), CommonConstants.DATE_DISPLAY_PATTERN);
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
                ReceivablePaymentVM::getAmount,
                StyleConstants.ALIGN_RIGHT);
        TableViewUtils.initTableColumn(
                colPaymentDate,
                new LocalDateCellFactory<>(CommonConstants.DATE_DISPLAY_PATTERN),
                ReceivablePaymentVM::getPaymentDate);
        TextFieldUtils.setDigitTextFields(tfReceivableAmount);
        setFocused(tfPaymentDate);
    }

    @Override
    protected void initDataSaveControlValues() {
        currentReceivable = getPageData();
        tfCustomer.setText(currentReceivable.getCustomerName());
        tfInvoiceNumber.setText(currentReceivable.getInvoiceNumber());
        tfInvoiceDate.setText(
                currentReceivable.getInvoiceDate()
                        .format(DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN)));
        tfReceivableAmount.setText(StringNumberUtils.toStringOrEmpty(currentReceivable.getAmount()));
        tfDueDate.setText(
                currentReceivable.getDueDate()
                        .format(DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN)));
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

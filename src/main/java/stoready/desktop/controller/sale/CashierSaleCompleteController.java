package stoready.desktop.controller.sale;

import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.formatOrDefault;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.CommonLabel;
import stoready.desktop.constant.PaymentStatus;
import stoready.desktop.constant.SellingMode;
import stoready.desktop.constant.StringConstants;
import stoready.desktop.controller.CommonDataSaveController;
import stoready.desktop.controller.sale.CashierController.SaleData;
import stoready.desktop.controller.sale.CashierPayController.PaymentData;
import stoready.desktop.viewmodel.CustomerVM;

public class CashierSaleCompleteController extends CommonDataSaveController {

    @FXML
    private Label lblInvoiceNumber;

    @FXML
    private Label lblSellingMode;

    @FXML
    private Label lblCustomer;

    @FXML
    private Label lblTotalProduct;

    @FXML
    private Label lblTotalSale;

    @FXML
    private Label lblPaymentStatus;

    @FXML
    private Label lblPaymentDueDate;

    @FXML
    private Label lblPaymentAmount;

    @FXML
    private Label lblChange;

    @FXML
    private Button btnPrintCopy;

    private Runnable printFn;

    @FXML
    void onActionBtnPrintCopy(ActionEvent event) {
        printFn.run();
    }

    @Override
    protected void initDataSaveControlActions() {
        setFocused(btnSave);
    }

    @Override
    protected void initDataSaveControlValues() {
        Locale locale = resources.getLocale();
        List<Object> list = getPageData();
        SaleData saleData = (SaleData) list.get(0);
        PaymentData paymentData = (PaymentData) list.get(1);
        printFn = (Runnable) list.get(2);
        lblInvoiceNumber.setText(paymentData.getInvoiceNumber());
        lblSellingMode.setText(
                SellingMode.GENERAL.equals(saleData.getSellingMode()) ?
                        t.translate(CommonLabel.LBL_GENERAL) : t.translate(CommonLabel.LBL_PRESCRIPTION));
        lblCustomer.setText(saleData.getCustomer().map(CustomerVM::getName).orElse(StringConstants.MINUS));
        lblTotalProduct.setText(formatOrDefault(saleData.getTotalProduct(), locale, "0"));
        lblTotalSale.setText(formatOrDefault(saleData.getTotalSale(), locale, "0"));
        PaymentStatus paymentStatus = paymentData.getPaymentStatus();
        lblPaymentStatus.setText(
                PaymentStatus.PAID.equals(paymentStatus) ?
                        t.translate(CommonLabel.LBL_PAID) : t.translate(CommonLabel.LBL_UNPAID));
        if (PaymentStatus.UNPAID.equals(paymentStatus)) {
            String format = paymentData.getPaymentDueDate()
                    .format(DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN));
            lblPaymentDueDate.setText(format);
        }
        lblPaymentAmount.setText(formatOrDefault(paymentData.getPaymentAmount(), locale, "0"));
        lblChange.setText(formatOrDefault(paymentData.getChangeAmount(), locale, "0"));
    }

    @Override
    protected Object save() {
        return null;
    }

    @Override
    protected void validate(ControlValidator validator) {
        // Nothing to do
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // Nothing to do
    }

}

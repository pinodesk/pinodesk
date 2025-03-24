package pinodesk.controller.transaction.sale;

import static com.mudiatech.toolbox.data.StringNumberUtils.formatOrDefault;
import static pinodesk.constant.CommonConstants.DECIMAL_SCALE;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.ConfigurationConstants;
import pinodesk.constant.PaymentStatus;
import pinodesk.constant.SellingMode;
import pinodesk.constant.StringConstants;
import pinodesk.controller.CommonContentPaneController;
import pinodesk.service.ConfigurationService;
import pinodesk.util.PrintUtils;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.CustomerVM;
import pinodesk.viewmodel.PaymentDataVM;
import pinodesk.viewmodel.SaleDataVM;

@Slf4j
public class CashierSaleCompleteController extends CommonContentPaneController {

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
    private Button btnClose;

    @FXML
    private SplitMenuButton btnPrint;

    private MenuItem btnPrintCopy;

    private ConfigurationService configurationService;

    private PrintUtils printer;

    SaleDataVM saleData;

    PaymentDataVM paymentData;

    @FXML
    void onActionBtnClose(ActionEvent event) {
        close();
    }

    @FXML
    void onActionBtnPrint(ActionEvent event) {
        String printerName = configurationService.getConfiguration(ConfigurationConstants.PRINTER_NAME);
        if (StringUtils.isBlank(printerName)) {
            log.debug("Printer name is empty");
            return;
        }
        printer.printReceipt(printerName, saleData, paymentData, false);
    }

    @Override
    protected void initContentPaneControlActions() {
        btnPrintCopy = new MenuItem(t.translate(CommonLabel.BTN_PRINT_COPY));
        btnPrintCopy.setOnAction(event -> {
            String printerName = configurationService.getConfiguration(ConfigurationConstants.PRINTER_NAME);
            if (StringUtils.isBlank(printerName)) {
                log.debug("Printer name is empty");
                return;
            }
            printer.printReceipt(printerName, saleData, paymentData, true);
        });
        btnPrint.getItems().addAll(btnPrintCopy);
        setFocused(contentPane);
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                if (btnClose.isFocused()) {
                    btnClose.fire();
                    return;
                }
                if (btnPrint.isFocused()) {
                    btnPrint.fire();
                    return;
                }
            }
        });
    }

    @Override
    protected void initControlValues() {
        Locale locale = resources.getLocale();
        List<Object> list = getPageData();
        saleData = (SaleDataVM) list.get(0);
        paymentData = (PaymentDataVM) list.get(1);
        lblInvoiceNumber.setText(paymentData.getInvoiceNumber());
        lblSellingMode.setText(
                SellingMode.GENERAL.equals(saleData.getSellingMode()) ?
                        t.translate(CommonLabel.LBL_GENERAL) : t.translate(CommonLabel.LBL_PRESCRIPTION));
        lblCustomer.setText(saleData.getCustomer().map(CustomerVM::getName).orElse(StringConstants.MINUS));
        lblTotalProduct.setText(formatOrDefault(saleData.getTotalProduct(), locale, "0"));
        lblTotalSale.setText(formatOrDefault(saleData.getTotalSale(), locale, DECIMAL_SCALE, "0"));
        PaymentStatus paymentStatus = paymentData.getPaymentStatus();
        lblPaymentStatus.setText(
                PaymentStatus.PAID.equals(paymentStatus) ?
                        t.translate(CommonLabel.LBL_PAID) : t.translate(CommonLabel.LBL_UNPAID));
        if (PaymentStatus.UNPAID.equals(paymentStatus)) {
            String format = paymentData.getPaymentDueDate()
                    .format(DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN));
            lblPaymentDueDate.setText(format);
        }
        lblPaymentAmount.setText(formatOrDefault(paymentData.getPaymentAmount(), locale, DECIMAL_SCALE, "0"));
        lblChange.setText(formatOrDefault(paymentData.getChangeAmount(), locale, DECIMAL_SCALE, "0"));
    }

    @Override
    protected void initServices() {
        configurationService = SpringUtils.getBean(ConfigurationService.class);
        printer = new PrintUtils(configurationService, t, resources);
    }

}

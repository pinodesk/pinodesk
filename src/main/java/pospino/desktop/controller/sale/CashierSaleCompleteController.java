package pospino.desktop.controller.sale;

import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.formatOrDefault;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.KeyCode;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.constant.SellingMode;
import pospino.desktop.constant.StringConstants;
import pospino.desktop.controller.CommonContentPaneController;
import pospino.desktop.service.ConfigurationService;
import pospino.desktop.util.PrintUtils;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.CustomerVM;
import pospino.desktop.viewmodel.PaymentDataVM;
import pospino.desktop.viewmodel.SaleDataVM;

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
        printer.printReceipt(saleData, paymentData, false);
    }

    @Override
    protected void initContentPaneControlActions() {
        btnPrintCopy = new MenuItem(t.translate(CommonLabel.BTN_PRINT_COPY));
        btnPrintCopy.setOnAction(event -> {
            printer.printReceipt(saleData, paymentData, true);
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
    protected void initServices() {
        configurationService = SpringUtils.getBean(ConfigurationService.class);
        printer = new PrintUtils(configurationService, t, resources);
    }

}

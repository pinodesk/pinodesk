package stoready.desktop.util;

import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.formatOrDefault;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.muhammadkholidb.pandora.utility.Translator;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import stoready.desktop.constant.CommonLabel;
import stoready.desktop.constant.ConfigurationConstants;
import stoready.desktop.constant.MessageCode;
import stoready.desktop.exception.PrinterException;
import stoready.desktop.service.ConfigurationService;
import stoready.desktop.viewmodel.PaymentDataVM;
import stoready.desktop.viewmodel.SaleDataVM;
import stoready.desktop.viewmodel.SaleProductVM;

@Slf4j
@AllArgsConstructor
public class PrintUtils {

    private ConfigurationService configurationService;
    private Translator t;
    private ResourceBundle resources;

    // https://examples.javacodegeeks.com/desktop-java/javafx/javafx-print-api/
    // https://stackoverflow.com/questions/38470568/javafx-doesnt-detect-changes-of-available-printers
    public void printReceipt(SaleDataVM saleData, PaymentDataVM paymentData, boolean isCopy) {
        String printerName = configurationService.getConfiguration(ConfigurationConstants.PRINTER_NAME);
        if (StringUtils.isBlank(printerName)) {
            log.debug("Printer name is empty");
            return;
        }
        Optional<Printer> printer = Printer.getAllPrinters().stream().filter(p -> p.getName().equals(printerName))
                .findAny();
        if (printer.isEmpty()) {
            throw new PrinterException(MessageCode.ERROR_PRINTER_NOT_FOUND);
        }
        PrinterJob job = PrinterJob.createPrinterJob(printer.get());
        if (job == null) {
            throw new PrinterException(MessageCode.ERROR_PRINTER_UNAVAILABLE);
        }
        job.getJobSettings().setJobName("Stoready Print Job");
        PageLayout pl = job.getJobSettings().getPageLayout();
        PageLayout pageLayout = job.getPrinter().createPageLayout(pl.getPaper(), pl.getPageOrientation(), 0, 0, 0, 0);
        double scale = 0.6;
        Node node = prepareReceipt(saleData, paymentData, isCopy);
        node.getTransforms().add(new Scale(scale, scale));
        boolean printed = job.printPage(pageLayout, node);
        if (printed) {
            job.endJob();
        }
    }

    public Node prepareReceipt(SaleDataVM saleData, PaymentDataVM paymentData, boolean isCopy) {
        Locale locale = resources.getLocale();
        Map<String, String> config = configurationService.getConfigurationMap();

        String sep = "--------------------------------------------------";

        Label lblReceiptStoreName = new Label(config.get(ConfigurationConstants.STORE_NAME));
        lblReceiptStoreName.setWrapText(true);
        lblReceiptStoreName.setTextAlignment(TextAlignment.CENTER);

        Label lblStoreAddress = new Label(config.get(ConfigurationConstants.STORE_ADDRESS));
        lblStoreAddress.setTextAlignment(TextAlignment.CENTER);
        lblStoreAddress.setWrapText(true);

        Label lblFooter = new Label(config.get(ConfigurationConstants.PRINTER_FOOTER));
        lblFooter.setTextAlignment(TextAlignment.CENTER);

        Label lblPowered = new Label(t.translate(CommonLabel.LBL_POWERED_BY_STOREADY));
        lblPowered.setTextAlignment(TextAlignment.CENTER);

        Label lblWww = new Label(t.translate(CommonLabel.LBL_WWW_STOREADY));
        lblWww.setTextAlignment(TextAlignment.CENTER);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(5d);
        vbox.setMaxWidth(240);
        if (isCopy) {
            Label lblCopyReceipt = new Label("--- " + t.translate(CommonLabel.LBL_COPY_RECEIPT) + " ---");
            lblCopyReceipt.setTextAlignment(TextAlignment.CENTER);
            vbox.getChildren().add(lblCopyReceipt);
            vbox.getChildren().add(new Label());
        }
        vbox.getChildren().addAll(
                lblReceiptStoreName,
                lblStoreAddress,
                new Label(sep),
                topGridPane(paymentData, locale),
                new Label(sep),
                mainGridPane(saleData, locale),
                new Label(sep),
                bottomGridPane(saleData, paymentData, locale),
                new Label(sep),
                lblFooter,
                new Label(),
                lblPowered,
                lblWww,
                new Label(),
                new Label(),
                new Label());
        return vbox;
    }

    private GridPane bottomGridPane(SaleDataVM saleData, PaymentDataVM paymentData, Locale locale) {
        GridPane gp = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(10);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(50);
        gp.getColumnConstraints().addAll(col1, col2, col3);
        gp.setHgap(5);
        Label lblReceiptTotalProduct = new Label(formatOrDefault(saleData.getTotalProduct(), locale, "0"));
        GridPane.setHalignment(lblReceiptTotalProduct, HPos.RIGHT);
        Label lblReceiptTotalSale = new Label(formatOrDefault(saleData.getTotalSale(), locale, "0"));
        GridPane.setHalignment(lblReceiptTotalSale, HPos.RIGHT);
        Label lblPaymentAmount = new Label(formatOrDefault(paymentData.getPaymentAmount(), locale, "0"));
        GridPane.setHalignment(lblPaymentAmount, HPos.RIGHT);
        Label lblChangeAmount = new Label(formatOrDefault(paymentData.getChangeAmount(), locale, "0"));
        GridPane.setHalignment(lblChangeAmount, HPos.RIGHT);
        VBox.setMargin(gp, new Insets(0, 15, 0, 0));
        gp.add(new Label(t.translate(CommonLabel.LBL_TOTAL)), 0, 0);
        gp.add(new Label(":"), 1, 0);
        gp.add(lblReceiptTotalSale, 2, 0);
        gp.add(new Label(t.translate(CommonLabel.LBL_PAY)), 0, 1);
        gp.add(new Label(":"), 1, 1);
        gp.add(lblPaymentAmount, 2, 1);
        gp.add(new Label(t.translate(CommonLabel.LBL_CHANGE)), 0, 2);
        gp.add(new Label(":"), 1, 2);
        gp.add(lblChangeAmount, 2, 2);
        return gp;
    }

    private GridPane mainGridPane(SaleDataVM saleData, Locale locale) {
        GridPane gp = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(50);
        gp.getColumnConstraints().addAll(col1, col2, col3);
        gp.setHgap(5);
        VBox.setMargin(gp, new Insets(0, 15, 0, 0));
        int row = 0;
        List<SaleProductVM> saleProducts = new ArrayList<>();
        saleData.getSaleProducts().forEach(sp -> {
            boolean found = false;
            for (SaleProductVM sp1 : saleProducts) {
                if (sp1.getProductId().equals(sp.getProductId())) {
                    sp1.setSaleQuantity(sp1.getSaleQuantity() + sp.getSaleQuantity());
                    sp1.setSubtotal(sp1.getSubtotal().add(sp.getSubtotal()));
                    found = true;
                    break;
                }
            }
            if (!found) {
                saleProducts.add(sp);
            }
        });
        for (SaleProductVM sp : saleProducts) {
            Label prod = new Label(sp.getProductName());
            Label qty = new Label(sp.getSaleQuantity() + " x");
            Label price = new Label(formatOrDefault(sp.getSellingPrice(), locale, "0"));
            Label total = new Label(formatOrDefault(sp.getSubtotal(), locale, "0"));
            GridPane.setColumnSpan(prod, 3);
            GridPane.setHalignment(total, HPos.RIGHT);
            gp.add(prod, 0, row);
            row++;
            gp.add(qty, 0, row);
            gp.add(price, 1, row);
            gp.add(total, 2, row);
            row++;
        }
        return gp;
    }

    private GridPane topGridPane(PaymentDataVM paymentData, Locale locale) {
        GridPane gp = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(10);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(50);
        gp.getColumnConstraints().addAll(col1, col2, col3);
        gp.setHgap(5);
        String date = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", locale).format(LocalDateTime.now());
        Label lblDate = new Label(date);
        GridPane.setHalignment(lblDate, HPos.RIGHT);
        Label lblInvoiceNumber = new Label(paymentData.getInvoiceNumber());
        GridPane.setHalignment(lblInvoiceNumber, HPos.RIGHT);
        VBox.setMargin(gp, new Insets(0, 15, 0, 0));
        gp.add(new Label(t.translate(CommonLabel.LBL_DATE)), 0, 0);
        gp.add(new Label(":"), 1, 0);
        gp.add(lblDate, 2, 0);
        gp.add(new Label(t.translate(CommonLabel.LBL_INVOICE_NUMBER)), 0, 1);
        gp.add(new Label(":"), 1, 1);
        gp.add(lblInvoiceNumber, 2, 1);
        return gp;
    }

}

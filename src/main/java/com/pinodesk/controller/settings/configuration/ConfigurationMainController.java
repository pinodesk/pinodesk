package com.pinodesk.controller.settings.configuration;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.StringUtils;

import com.pinodesk.apimodel.RequestInstallationCodeResponse;
import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.Page;
import com.pinodesk.constant.PaymentStatus;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.constant.StringConstants;
import com.pinodesk.constant.SystemConstants;
import com.pinodesk.controller.CommonContentPaneController;
import com.pinodesk.javafx.converter.LanguageComboBoxConverter;
import com.pinodesk.model.InstallationData;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.AlertResult;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.ScrollPaneUtils;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.service.ConfigurationService;
import com.pinodesk.service.InstallationService;
import com.pinodesk.toolbox.data.ListBuilder;
import com.pinodesk.util.PrintUtils;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.util.TaskUtils;
import com.pinodesk.viewmodel.PaymentDataVM;
import com.pinodesk.viewmodel.SaleDataVM;
import com.pinodesk.viewmodel.SaleProductVM;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigurationMainController extends CommonContentPaneController {

    @FXML
    private Button btnSaveGeneral;

    @FXML
    private Button btnSavePrinter;

    @FXML
    private Button btnBackup;

    @FXML
    private Button btnRestore;

    @FXML
    private TextField tfBackup;

    @FXML
    private TextField tfRestore;

    @FXML
    private TextField tfStoreName;

    @FXML
    private TextField tfStoreAddress;

    @FXML
    private ComboBox<Locale> cbLanguage;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPrinterName;

    @FXML
    private TextField tfPrinterFooter;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbFooterPoweredBy;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbPharmacyFeatures;

    @FXML
    private ScrollPane configurationScrollPane;

    @FXML
    private TextField tfActivationEmail;

    @FXML
    private TextField tfActivationCode;

    @FXML
    private VBox vboxActivateNow;

    @FXML
    private Label lblActivationIntro;

    @FXML
    private Button btnRequestCode;

    private FileChooser fileChooser = new FileChooser();

    private ConfigurationService configurationService;
    private InstallationService installationService;
    private String installationRequestId;
    private List<Locale> locales;
    private Map<String, String> configurationMap;
    private PrintUtils printer;

    @FXML
    void onActionBtnRequestCode(ActionEvent event) {
        String email = tfActivationEmail.getText();
        if (email == null || email.trim().isEmpty()) {
            displayError(MessageCode.ERROR_EMPTY_EMAIL);
            return;
        }
        Stage loading = displayLoading();
        TaskUtils.runTask("Request installation code", () -> {
            RequestInstallationCodeResponse response = installationService.requestInstallationCode(email);
            if (response != null && response.getInstallationRequestId() != null) {
                installationRequestId = response.getInstallationRequestId();
                log.info("Installation code requested successfully, request ID: {}", installationRequestId);
            }
            Platform.runLater(() -> {
                loading.hide();
                if (installationRequestId != null) {
                    displayInfo(MessageCode.SUCCESS_REQUEST_INSTALLATION_CODE);
                    tfActivationCode.requestFocus();
                }
            });
        }, throwable -> Platform.runLater(() -> {
            loading.hide();
            handleException(throwable);
        }));
    }

    @FXML
    void onActionBtnActivateNow(ActionEvent event) {
        String email = tfActivationEmail.getText();
        if (email == null || email.trim().isEmpty()) {
            displayError(MessageCode.ERROR_EMPTY_EMAIL);
            return;
        }
        if (installationRequestId == null) {
            displayError(MessageCode.ERROR_EMPTY_INSTALLATION_REQUEST_ID);
            return;
        }
        String code = tfActivationCode.getText();
        if (code == null || code.trim().isEmpty()) {
            displayError(MessageCode.ERROR_EMPTY_INSTALLATION_CODE);
            return;
        }
        Stage loading = displayLoading();
        TaskUtils.runTask("Register installation", () -> {
            installationService.registerInstallation(email, installationRequestId, code);
            Platform.runLater(() -> {
                loading.hide();
                displayInfo(MessageCode.SUCCESS_INSTALLATION_REGISTRATION);
                updateInstallationRegistrationSection();
            });
        }, throwable -> Platform.runLater(() -> {
            loading.hide();
            handleException(throwable);
        }));
    }

    @FXML
    void onActionBtnSaveGeneral(ActionEvent event) {
        Locale selectedLocale = ComboBoxUtils.getSelectedItem(cbLanguage);
        Map<String, String> map = new HashMap<>();
        map.put(ConfigurationConstants.LANGUAGE, selectedLocale.getLanguage());
        map.put(ConfigurationConstants.STORE_NAME, tfStoreName.getText());
        map.put(ConfigurationConstants.STORE_ADDRESS, tfStoreAddress.getText());
        map.put(
                ConfigurationConstants.PHARMACY_FEATURES_ENABLED,
                ComboBoxUtils.getSelectedItem(cbPharmacyFeatures).getValue().toString());
        configurationService.updateConfiguration(map);
        displayInfo(MessageCode.SUCCESS_EDIT_CONFIGURATION_WITH_LOGOUT);
        closeRootPane();
        sessionService.logout();
        StageUtils.open(Page.LOGIN, false);
    }

    @FXML
    void onActionBtnTestPrinter(ActionEvent event) {
        Printer selectedPrinter = ComboBoxUtils.getSelectedItem(cbPrinterName).getValue();
        if (selectedPrinter == null) {
            log.debug("The selected printer is empty");
            return;
        }
        String lblProduct = t.translate(CommonLabel.LBL_PRODUCT);
        List<SaleProductVM> saleProducts = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            SaleProductVM sp = new SaleProductVM();
            sp.setProductId(i);
            sp.setProductName(String.format("%s %d", lblProduct, i));
            sp.setSellingPrice(BigDecimal.valueOf(1000));
            sp.setSaleQuantity(1);
            sp.setSubtotal(BigDecimal.valueOf(1000));
            saleProducts.add(sp);
        }
        SaleDataVM saleData = new SaleDataVM();
        saleData.setSaleProducts(saleProducts);
        saleData.setTotalProduct(10);
        saleData.setTotalSale(BigDecimal.valueOf(3000));
        PaymentDataVM paymentData = new PaymentDataVM();
        paymentData.setChangeAmount(BigDecimal.valueOf(2000));
        paymentData.setPaymentAmount(BigDecimal.valueOf(5000));
        paymentData.setPaymentStatus(PaymentStatus.PAID);
        paymentData.setInvoiceNumber("1234567890");
        paymentData.setPaymentDateTime(LocalDateTime.now());
        printer.printReceipt(selectedPrinter.getName(), saleData, paymentData, false);
    }

    @FXML
    void onActionBtnSavePrinter(ActionEvent event) {
        Printer selectedPrinter = ComboBoxUtils.getSelectedItem(cbPrinterName).getValue();
        Map<String, String> map = new HashMap<>();
        map.put(
                ConfigurationConstants.PRINTER_NAME,
                selectedPrinter == null ? StringConstants.EMPTY : selectedPrinter.getName());
        map.put(ConfigurationConstants.PRINTER_FOOTER, tfPrinterFooter.getText());
        map.put(
                ConfigurationConstants.PRINTER_FOOTER_POWERED_BY,
                ComboBoxUtils.getSelectedItem(cbFooterPoweredBy).getValue().toString());
        configurationService.updateConfiguration(map);
        displayInfo(MessageCode.SUCCESS_EDIT_CONFIGURATION);
    }

    @FXML
    void onActionBtnBackup(ActionEvent event) {
        String location = tfBackup.getText();
        if (StringUtils.isBlank(location)) {
            tfBackup.requestFocus();
            return;
        }
        Stage stage = displayLoading();
        CompletableFuture.runAsync(() -> {
            try {
                configurationService.createBackup(location);
            } catch (Exception e) {
                log.error("Error on backup process", e);
                throw new CompletionException(e);
            }
        }).whenComplete((result, ex) -> Platform.runLater(() -> {
            stage.hide();
            if (ex != null) {
                handleException(ex);
                return;
            }
            tfBackup.setText("");
            displayInfo(String.format(t.translate(MessageCode.SUCCESS_BACKUP), location));
        }));
        setFocusedToContentPane();
    }

    @FXML
    void onActionBtnRestore(ActionEvent event) {
        String location = tfRestore.getText();
        if (StringUtils.isBlank(location)) {
            tfRestore.requestFocus();
            return;
        }
        AlertResult confirmation = displayConfirmation(MessageCode.CONFIRMATION_RESTORE_DATA);
        if (!confirmation.isConfirmed()) {
            return;
        }
        Stage stage = displayLoading();
        CompletableFuture.runAsync(() -> {
            try {
                configurationService.restoreDatabase(location);
            } catch (Exception e) {
                log.error("Error on restore backup", e);
                throw new CompletionException(e);
            }
        }).whenComplete((result, ex) -> Platform.runLater(() -> {
            stage.hide();
            if (ex != null) {
                ex.printStackTrace();
                handleException(ex);
                return;
            }
            tfRestore.setText("");
            displayInfo(MessageCode.SUCCESS_RESTORE);
            closeRootPane();
        }));
        setFocusedToContentPane();
    }

    @Override
    protected void initServices() {
        configurationService = SpringUtils.getBean(ConfigurationService.class);
        installationService = SpringUtils.getBean(InstallationService.class);
        locales = FXCollections.observableArrayList(
                Locale.forLanguageTag(CommonConstants.LANGUAGE_CODE_ENGLISH),
                Locale.forLanguageTag(CommonConstants.LANGUAGE_CODE_INDONESIA));
        configurationMap = configurationService.getConfigurationMap();
        printer = new PrintUtils(configurationService, t, resources);
    }

    @Override
    protected void initContentPaneControlActions() {
        disableWriteAction(MenuCodeConstants.SETTINGS_CONFIGURATION, btnSaveGeneral);
        ComboBoxUtils.init(
                cbLanguage,
                new LanguageComboBoxConverter(cbLanguage, configurationMap.get(ConfigurationConstants.LANGUAGE)),
                locales);
        List<SimpleComboBoxModel> printerModels = new ListBuilder<SimpleComboBoxModel>()
                .add(new SimpleComboBoxModel(null, StringConstants.EMPTY)).build();
        Printer.getAllPrinters().forEach(p -> {
            printerModels.add(new SimpleComboBoxModel(p, p.getName()));
        });
        ComboBoxUtils.initSimple(cbPrinterName, printerModels);
        ComboBoxUtils.initSimple(
                cbFooterPoweredBy,
                new SimpleComboBoxModel(SimpleStatus.YES, t.translate(CommonLabel.LBL_SHOW.toString())),
                new SimpleComboBoxModel(SimpleStatus.NO, t.translate(CommonLabel.LBL_HIDE.toString())));
        ComboBoxUtils.initSimple(
                cbPharmacyFeatures,
                new SimpleComboBoxModel(SimpleStatus.YES, t.translate(CommonLabel.LBL_ENABLE.toString())),
                new SimpleComboBoxModel(SimpleStatus.NO, t.translate(CommonLabel.LBL_DISABLE.toString())));
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        fileChooser.setInitialDirectory(new File(SystemConstants.USER_HOME_DIR));
        fileChooser.setInitialFileName(String.format("pinodesk-backup-%s.zip", timestamp));
        tfBackup.focusedProperty().addListener((o, ov, nv) -> {
            boolean isFocused = Boolean.TRUE.equals(nv);
            if (isFocused) {
                setFocused(btnBackup);
                File file = fileChooser.showSaveDialog(getCurrentStage());
                if (file != null) {
                    tfBackup.setText(file.getAbsolutePath());
                } else {
                    tfBackup.setText("");
                }
            }
        });
        tfRestore.focusedProperty().addListener((o, ov, nv) -> {
            boolean isFocused = Boolean.TRUE.equals(nv);
            if (isFocused) {
                setFocused(btnRestore);
                File file = fileChooser.showOpenDialog(getCurrentStage());
                if (file != null) {
                    tfRestore.setText(file.getAbsolutePath());
                } else {
                    tfRestore.setText("");
                }
            }
        });
    }

    @Override
    protected void initControlValues() {
        tfStoreName.setText(configurationMap.get(ConfigurationConstants.STORE_NAME));
        tfStoreAddress.setText(configurationMap.get(ConfigurationConstants.STORE_ADDRESS));
        ComboBoxUtils.select(cbLanguage, () -> locales.stream().filter(locale -> {
            String configLanguage = configurationMap.get(ConfigurationConstants.LANGUAGE);
            return Locale.forLanguageTag(configLanguage).getLanguage().equals(locale.getLanguage());
        }).findAny().get());
        ComboBoxUtils.select(cbPrinterName, () -> {
            String printerName = configurationMap.get(ConfigurationConstants.PRINTER_NAME);
            ObservableList<SimpleComboBoxModel> items = cbPrinterName.getItems();
            return items.stream().filter(model -> {
                Printer printer = model.getValue();
                return printer != null && printer.getName().equals(printerName);
            }).findAny().orElseGet(() -> items.stream().filter(model -> model.getValue() == null).findAny().get());
        });
        tfPrinterFooter.setText(configurationMap.get(ConfigurationConstants.PRINTER_FOOTER));
        ComboBoxUtils.select(cbFooterPoweredBy, () -> cbFooterPoweredBy.getItems().stream().filter(model -> {
            String val = configurationMap.get(ConfigurationConstants.PRINTER_FOOTER_POWERED_BY);
            return val.equals(model.getValue().toString());
        }).findAny().get());
        ComboBoxUtils.select(cbPharmacyFeatures, () -> cbPharmacyFeatures.getItems().stream().filter(model -> {
            String val = configurationMap.get(ConfigurationConstants.PHARMACY_FEATURES_ENABLED);
            return val.equals(model.getValue().toString());
        }).findAny().get());
        Platform.runLater(() -> {
            ScrollPaneUtils.fixBlur(configurationScrollPane);
        });
        updateInstallationRegistrationSection();
    }

    private void updateInstallationRegistrationSection() {
        boolean registered = installationService.isRegistered();
        if (registered) {
            InstallationData data = installationService.getInstallationData().get();
            lblActivationIntro.setText(t.translate("lbl_installation_registration_registered"));
            String email = data != null && data.getEmail() != null ? data.getEmail() : "";
            String code = data != null && data.getInstallationId() != null ? data.getInstallationId() : "";
            tfActivationEmail.setText(email);
            tfActivationCode.setText(code);
            tfActivationEmail.setEditable(false);
            tfActivationCode.setEditable(false);
            if (btnRequestCode != null) {
                setVisibleInLayout(false, btnRequestCode);
            }
            setVisibleInLayout(false, vboxActivateNow);
        } else {
            lblActivationIntro.setText(t.translate("lbl_installation_registration_info"));
            tfActivationEmail.setEditable(true);
            tfActivationCode.setEditable(true);
            if (btnRequestCode != null) {
                setVisibleInLayout(true, btnRequestCode);
            }
            setVisibleInLayout(true, vboxActivateNow);
        }
    }

    @Override
    protected Stage getCurrentStage() {
        return (Stage) contentPane.getScene().getWindow();
    }

    private void closeRootPane() {
        contentPane.getParent().getParent().getScene().getWindow().hide();
    }

}

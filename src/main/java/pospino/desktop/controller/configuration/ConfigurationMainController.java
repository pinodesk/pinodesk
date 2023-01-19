package pospino.desktop.controller.configuration;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.toolbox.data.ListBuilder;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.StringConstants;
import pospino.desktop.constant.SystemConstants;
import pospino.desktop.controller.CommonContentPaneController;
import pospino.desktop.javafx.converter.LanguageComboBoxConverter;
import pospino.desktop.service.ConfigurationService;

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

    private FileChooser fileChooser = new FileChooser();

    private ConfigurationService configurationService;
    private List<Locale> locales;
    private Map<String, String> configurationMap;

    @FXML
    void onActionBtnSaveGeneral(ActionEvent event) {
        Locale selectedLocale = ComboBoxUtils.getSelectedItem(cbLanguage);
        Map<String, String> map = new HashMap<>();
        map.put(ConfigurationConstants.LANGUAGE, selectedLocale.getLanguage());
        map.put(ConfigurationConstants.STORE_NAME, tfStoreName.getText());
        map.put(ConfigurationConstants.STORE_ADDRESS, tfStoreAddress.getText());
        configurationService.updateConfiguration(map);
        displayInfo(MessageCode.SUCCESS_EDIT_CONFIGURATION_WITH_LOGOUT);
        closeRootPane();
        sessionService.get().logout();
        StageUtils.open(Page.LOGIN, false);
    }

    @FXML
    void onActionBtnSavePrinter(ActionEvent event) {
        Printer selectedPrinter = ComboBoxUtils.getSelectedItem(cbPrinterName).getValue();
        Map<String, String> map = new HashMap<>();
        map.put(
                ConfigurationConstants.PRINTER_NAME,
                selectedPrinter == null ? StringConstants.EMPTY : selectedPrinter.getName());
        map.put(ConfigurationConstants.PRINTER_FOOTER, tfPrinterFooter.getText());
        configurationService.updateConfiguration(map);
        displayInfo(MessageCode.SUCCESS_EDIT_CONFIGURATION);
    }

    @FXML
    void onActionBtnBackup(ActionEvent event) {
        String location = tfBackup.getText();
        if (StringUtils.isBlank(location)) {
            return;
        }
        Stage stage = displayLoading();
        CompletableFuture.runAsync(() -> {
            try {
                configurationService.createBackup(location);
            } catch (Exception e) {
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
    protected void initServices(ApplicationContext ctx) {
        configurationService = ctx.getBean(ConfigurationService.class);
        locales = FXCollections.observableArrayList(
                new Locale(CommonConstants.LANGUAGE_CODE_ENGLISH),
                new Locale(CommonConstants.LANGUAGE_CODE_INDONESIA));
        configurationMap = configurationService.getConfigurationMap();
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
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        fileChooser.setInitialDirectory(new File(SystemConstants.USER_HOME_DIR));
        fileChooser.setInitialFileName(String.format("pospino-backup-%s.zip", timestamp));
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
            return new Locale(configLanguage).getLanguage().equals(locale.getLanguage());
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
    }

    @Override
    protected Stage getCurrentStage() {
        return (Stage) contentPane.getScene().getWindow();
    }

    private void closeRootPane() {
        contentPane.getParent().getParent().getScene().getWindow().hide();
    }

}

package stoready.desktop.controller.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.toolbox.data.ListBuilder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.ConfigurationConstants;
import stoready.desktop.constant.MenuCodeConstants;
import stoready.desktop.constant.MessageCode;
import stoready.desktop.constant.Page;
import stoready.desktop.constant.StringConstants;
import stoready.desktop.controller.BaseController;
import stoready.desktop.javafx.converter.LanguageComboBoxConverter;
import stoready.desktop.service.ConfigurationService;

public class ConfigurationMainController extends BaseController {

    @FXML
    private VBox contentPane;

    @FXML
    private Button btnSaveGeneral;

    @FXML
    private Button btnSavePrinter;

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

    @Override
    protected void initServices(ApplicationContext ctx) {
        configurationService = ctx.getBean(ConfigurationService.class);
        locales = FXCollections.observableArrayList(
                new Locale(CommonConstants.LANGUAGE_CODE_ENGLISH),
                new Locale(CommonConstants.LANGUAGE_CODE_INDONESIA));
        configurationMap = configurationService.getConfigurationMap();
    }

    @Override
    protected void initControlActions() {
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

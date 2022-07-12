package pinus.desktop.controller.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pinus.desktop.Pinus;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.MessageCode;
import pinus.desktop.controller.BaseController;
import pinus.desktop.javafx.converter.LanguageComboBoxConverter;
import pinus.desktop.service.ConfigurationService;

public class ConfigurationMainController extends BaseController {

    @FXML
    private Button btnSave;

    @FXML
    private TextField tfStoreName;

    @FXML
    private TextField tfStoreAddress;

    @FXML
    private ComboBox<Locale> cbLanguage;

    private ConfigurationService configurationService;

    @FXML
    void onActionBtnSave(ActionEvent event) throws IOException {
        Locale selectedLocale = ComboBoxUtils.getSelectedItem(cbLanguage);
        Map<String, String> map = new HashMap<>();
        map.put(ConfigurationConstants.LANGUAGE, selectedLocale.getLanguage());
        map.put(ConfigurationConstants.STORE_NAME, tfStoreName.getText());
        map.put(ConfigurationConstants.STORE_ADDRESS, tfStoreAddress.getText());
        configurationService.updateConfiguration(map);
        displayInfo(MessageCode.SUCCESS_EDIT_CONFIGURATION);
        Pinus.reload();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        configurationService = ctx.getBean(ConfigurationService.class);
    }

    @Override
    protected void initControlActions() {
        // Nothing to init
    }

    @Override
    protected void initControlValues() {
        ObservableList<Locale> locales = FXCollections.observableArrayList(
                new Locale(CommonConstants.LANGUAGE_CODE_ENGLISH),
                new Locale(CommonConstants.LANGUAGE_CODE_INDONESIA));
        Map<String, String> configurationMap = configurationService.getConfigurationMap();
        tfStoreName.setText(configurationMap.get(ConfigurationConstants.STORE_NAME));
        tfStoreAddress.setText(configurationMap.get(ConfigurationConstants.STORE_ADDRESS));
        ComboBoxUtils.init(
                cbLanguage,
                new LanguageComboBoxConverter(cbLanguage, configurationMap.get(ConfigurationConstants.LANGUAGE)),
                locales);
        ComboBoxUtils.select(cbLanguage, () -> locales.stream().filter(locale -> {
            String configLanguage = configurationMap.get(ConfigurationConstants.LANGUAGE);
            return new Locale(configLanguage).getLanguage().equals(locale.getLanguage());
        }).findAny().get());
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

}

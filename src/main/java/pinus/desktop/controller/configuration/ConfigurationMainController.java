package pinus.desktop.controller.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.springframework.context.ApplicationContext;

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
import pinus.desktop.javafx.converter.DrugCategoryBaseComboBoxConverter;
import pinus.desktop.javafx.converter.LanguageComboBoxConverter;
import pinus.desktop.service.ConfigurationService;
import pinus.desktop.service.DrugCategoryService;
import pinus.desktop.viewmodel.DrugCategoryBaseVM;

public class ConfigurationMainController extends BaseController {

    @FXML
    private Button btnSave;

    @FXML
    private TextField tfStoreName;

    @FXML
    private TextField tfStoreAddress;

    @FXML
    private TextField tfVatPercentage;

    @FXML
    private ComboBox<DrugCategoryBaseVM> cbDrugCategoryBase;

    @FXML
    private ComboBox<Locale> cbLanguage;

    private ConfigurationService configurationService;

    private DrugCategoryService drugCategoryService;

    @FXML
    void onActionBtnSave(ActionEvent event) throws IOException {
        Locale localeIndonesia = new Locale(CommonConstants.LANGUAGE_CODE_INDONESIA);
        Locale selectedLocale = ComboBoxUtils.getSelectedItem(cbLanguage);
        DrugCategoryBaseVM selectedDrugCategoryBase = ComboBoxUtils.getSelectedItem(cbDrugCategoryBase);
        Map<String, String> map = new HashMap<>();
        map.put(ConfigurationConstants.DRUG_CATEGORY_BASE_ID, selectedDrugCategoryBase.getId().toString());
        map.put(
                ConfigurationConstants.LANGUAGE_CODE,
                selectedLocale.equals(localeIndonesia) ?
                        CommonConstants.LANGUAGE_CODE_INDONESIA : CommonConstants.LANGUAGE_CODE_ENGLISH);
        map.put(ConfigurationConstants.STORE_NAME, tfStoreName.getText());
        map.put(ConfigurationConstants.STORE_ADDRESS, tfStoreAddress.getText());
        map.put(ConfigurationConstants.VAT_PERCENTAGE, tfVatPercentage.getText());
        configurationService.updateConfiguration(map);
        displayInfo(MessageCode.SUCCESS_EDIT_CONFIGURATION);
        Pinus.reload();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        configurationService = ctx.getBean(ConfigurationService.class);
        drugCategoryService = ctx.getBean(DrugCategoryService.class);
    }

    @Override
    protected void initControlActions() {
        ComboBoxUtils.init(cbDrugCategoryBase, new DrugCategoryBaseComboBoxConverter(cbDrugCategoryBase));
        ComboBoxUtils.init(
                cbLanguage,
                new LanguageComboBoxConverter(
                        cbLanguage,
                        configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE)));
        TextFieldUtils.setDigitTextFields(tfVatPercentage);
    }

    @Override
    protected void initControlValues() {
        ObservableList<Locale> locales = FXCollections.observableArrayList(
                new Locale(CommonConstants.LANGUAGE_CODE_ENGLISH),
                new Locale(CommonConstants.LANGUAGE_CODE_INDONESIA));
        List<DrugCategoryBaseVM> drugCategoryBases = drugCategoryService.getAllDrugCategoryBases();
        Map<String, String> configurationMap = configurationService.getConfigurationMap();
        tfStoreName.setText(configurationMap.get(ConfigurationConstants.STORE_NAME));
        tfStoreAddress.setText(configurationMap.get(ConfigurationConstants.STORE_ADDRESS));
        tfVatPercentage.setText(configurationMap.get(ConfigurationConstants.VAT_PERCENTAGE));
        cbDrugCategoryBase.setItems(FXCollections.observableList(drugCategoryBases));
        cbLanguage.setItems(locales);
        ComboBoxUtils.select(cbDrugCategoryBase, () -> getSelectedDrugCategoryBase(configurationMap));
        ComboBoxUtils.select(cbLanguage, () -> locales.stream().filter(locale -> {
            String configLanguageCode = configurationMap.get(ConfigurationConstants.LANGUAGE_CODE);
            return new Locale(configLanguageCode).getLanguage().equals(locale.getLanguage());
        }).findAny().get());
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private DrugCategoryBaseVM getSelectedDrugCategoryBase(Map<String, String> configurationMap) {
        String categoryBaseId = configurationMap.get(ConfigurationConstants.DRUG_CATEGORY_BASE_ID);
        List<DrugCategoryBaseVM> drugCategoryBases = drugCategoryService.getAllDrugCategoryBases();
        return drugCategoryBases.stream().filter(base -> base.getId().equals(Long.valueOf(categoryBaseId))).findAny()
                .orElseThrow();
    }

}

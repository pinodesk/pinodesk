package com.getkembang.kembangdesktop.controller.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.getkembang.kembangdesktop.Kembang;
import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.controller.BaseController;
import com.getkembang.kembangdesktop.javafx.converter.DrugCategoryBaseComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.converter.LanguageComboBoxConverter;
import com.getkembang.kembangdesktop.service.ConfigurationService;
import com.getkembang.kembangdesktop.service.DrugCategoryService;
import com.getkembang.kembangdesktop.service.LanguageService;
import com.getkembang.kembangdesktop.viewmodel.DrugCategoryBaseVM;
import com.getkembang.kembangdesktop.viewmodel.LanguageVM;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.springframework.context.ApplicationContext;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private ComboBox<LanguageVM> cbLanguage;

    private ConfigurationService configurationService;

    private DrugCategoryService drugCategoryService;

    private LanguageService languageService;

    @FXML
    void onActionBtnSave(ActionEvent event) throws IOException {
        LanguageVM selectedLanguage = ComboBoxUtils.getSelectedItem(cbLanguage);
        DrugCategoryBaseVM selectedDrugCategoryBase = ComboBoxUtils.getSelectedItem(cbDrugCategoryBase);
        Map<String, String> map = new HashMap<>();
        map.put(ConfigurationConstants.DRUG_CATEGORY_BASE_ID, selectedDrugCategoryBase.getId().toString());
        map.put(ConfigurationConstants.LANGUAGE_CODE, selectedLanguage.getCode());
        map.put(ConfigurationConstants.LANGUAGE_ID, selectedLanguage.getId().toString());
        map.put(ConfigurationConstants.STORE_NAME, tfStoreName.getText());
        map.put(ConfigurationConstants.STORE_ADDRESS, tfStoreAddress.getText());
        map.put(ConfigurationConstants.VAT_PERCENTAGE, tfVatPercentage.getText());
        configurationService.updateConfiguration(map);
        displayInfo(MessageCode.SUCCESS_EDIT_CONFIGURATION);
        Kembang.reload();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        configurationService = ctx.getBean(ConfigurationService.class);
        drugCategoryService = ctx.getBean(DrugCategoryService.class);
        languageService = ctx.getBean(LanguageService.class);
    }

    @Override
    protected void initControlActions() {
        ComboBoxUtils.init(cbDrugCategoryBase, new DrugCategoryBaseComboBoxConverter(cbDrugCategoryBase));
        ComboBoxUtils.init(cbLanguage, new LanguageComboBoxConverter(cbLanguage));
        TextFieldUtils.setDigitTextFields(tfVatPercentage);
    }

    @Override
    protected void initControlValues() {
        List<DrugCategoryBaseVM> drugCategoryBases = drugCategoryService.getAllDrugCategoryBases();
        List<LanguageVM> languages = languageService.getAllLanguages();
        Map<String, String> configurationMap = configurationService.getConfigurationMap();
        tfStoreName.setText(configurationMap.get(ConfigurationConstants.STORE_NAME));
        tfStoreAddress.setText(configurationMap.get(ConfigurationConstants.STORE_ADDRESS));
        tfVatPercentage.setText(configurationMap.get(ConfigurationConstants.VAT_PERCENTAGE));
        cbDrugCategoryBase.setItems(FXCollections.observableList(drugCategoryBases));
        cbLanguage.setItems(FXCollections.observableList(languages));
        ComboBoxUtils.select(cbDrugCategoryBase,
                () -> drugCategoryBases.stream().filter(base -> configurationMap
                        .get(ConfigurationConstants.DRUG_CATEGORY_BASE_ID).equals(base.getId().toString())).findAny()
                        .get());
        ComboBoxUtils.select(cbLanguage, () -> languages.stream().filter(
                lang -> configurationMap.get(ConfigurationConstants.LANGUAGE_ID).equals(lang.getId().toString()))
                .findAny().get());
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

}

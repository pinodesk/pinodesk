package com.getkembang.kembangdesktop.utility;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.service.ConfigurationService;

import org.apache.commons.lang3.StringUtils;

import javafx.fxml.FXMLLoader;

public class PageLoader {

    private static ConfigurationService configurationService;

    static {
        configurationService = ApplicationContextHolder.getApplicationContext().getBean(ConfigurationService.class);
    }

    private PageLoader() {
    }

    public static <T> T load(Page page) throws IOException {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return load(page, StringUtils.isBlank(languageCode) ? CommonConstants.ENGLISH : new Locale(languageCode));
    }

    public static <T> T load(Page page, Locale locale) throws IOException {
        String path = String.format("/assets/templates/%s.fxml", page.templateName());
        URL location = PageLoader.class.getResource(path);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        loader.setResources(ResourceBundle.getBundle("com.getkembang.kembangdesktop.lang", locale));
        return loader.load();
    }

}

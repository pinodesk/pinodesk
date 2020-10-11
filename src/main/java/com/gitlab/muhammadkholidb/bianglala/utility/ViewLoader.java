package com.gitlab.muhammadkholidb.bianglala.utility;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.ConfigurationConstants;

import org.apache.commons.lang3.StringUtils;

import javafx.fxml.FXMLLoader;

public class ViewLoader {
    
    private ViewLoader() {}

    public static <T> T load(String name) throws IOException {
        return load(name, null); 
    }

    public static <T> T load(String name, Locale locale) throws IOException {
        String path = String.format("/assets/views/%s.fxml", name);
        URL location = ViewLoader.class.getResource(path);
        if (locale == null) {
            return load(location);
        }
        return load(location, locale);
    }

    public static  <T> T load(URL location) throws IOException {
        String languageCode = ConfigurationHolder.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return load(location, StringUtils.isBlank(languageCode) ? CommonConstants.ENGLISH : new Locale(languageCode)); 
    }

    public static <T> T load(URL location, Locale locale) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        loader.setResources(ResourceBundle.getBundle("com.gitlab.muhammadkholidb.bianglala.lang", locale));
        return loader.load();
    }

}

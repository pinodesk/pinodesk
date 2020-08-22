package com.gitlab.muhammadkholidb.bianglala.utility;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;

import javafx.fxml.FXMLLoader;

public class ViewLoader {
    
    private ViewLoader() {}

    public static Object load(String name) throws IOException {
        return load(name, CommonConstants.BAHASA); // Default to bahasa language
    }

    public static Object load(String name, Locale locale) throws IOException {
        String location = String.format("/view/%s.fxml", name);
        return load(ViewLoader.class.getResource(location), locale);
    }

    public static Object load(URL location) throws IOException {
        return load(location, CommonConstants.BAHASA); // Default to bahasa language
    }

    public static Object load(URL location, Locale locale) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        loader.setResources(ResourceBundle.getBundle("com.gitlab.muhammadkholidb.bianglala.lang", locale));
        return loader.load();
    }

}
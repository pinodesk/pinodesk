package com.getkembang.kembangdesktop.javafx.converter;

import java.util.Locale;

import com.gitlab.muhammadkholidb.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;

public class LanguageComboBoxConverter extends DefaultStringConverterAdapter<Locale> {

    private String currentLanguageCode;

    public LanguageComboBoxConverter(ComboBox<Locale> cb, String currentLanguageCode) {
        super(cb);
        this.currentLanguageCode = currentLanguageCode;
    }

    @Override
    protected String getDisplayText(Locale locale) {
        Locale currentLocale = new Locale(currentLanguageCode);
        return locale.getDisplayLanguage(currentLocale);
    }
    
}

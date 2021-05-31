package com.getkembang.kembangdesktop.javafx.converter;

import com.getkembang.kembangdesktop.viewmodel.LanguageVM;
import com.gitlab.muhammadkholidb.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;

public class LanguageComboBoxConverter extends DefaultStringConverterAdapter<LanguageVM> {

    public LanguageComboBoxConverter(ComboBox<LanguageVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(LanguageVM vm) {
        return vm.getName();
    }
    
}

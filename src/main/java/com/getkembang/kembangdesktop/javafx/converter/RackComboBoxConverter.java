package com.getkembang.kembangdesktop.javafx.converter;

import com.getkembang.kembangdesktop.viewmodel.RackVM;

import javafx.scene.control.ComboBox;

public class RackComboBoxConverter extends DefaultStringConverterAdapter<RackVM> {

    public RackComboBoxConverter(ComboBox<RackVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(RackVM t) {
        return t.getName();
    }

}

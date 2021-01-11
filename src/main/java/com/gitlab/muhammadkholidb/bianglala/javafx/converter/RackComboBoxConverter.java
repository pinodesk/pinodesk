package com.gitlab.muhammadkholidb.bianglala.javafx.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.RackVM;

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

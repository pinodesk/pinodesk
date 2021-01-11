package com.gitlab.muhammadkholidb.bianglala.javafx.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.UnitVM;

import javafx.scene.control.ComboBox;

public class UnitComboBoxConverter extends DefaultStringConverterAdapter<UnitVM> {

    public UnitComboBoxConverter(ComboBox<UnitVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(UnitVM unit) {
        return unit.getLabel() + " (" + unit.getName() + ")";
    }

}

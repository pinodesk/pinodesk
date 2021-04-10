package com.getkembang.kembangdesktop.javafx.converter;

import com.getkembang.kembangdesktop.viewmodel.UnitVM;
import com.gitlab.muhammadkholidb.dior.converter.DefaultStringConverterAdapter;

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

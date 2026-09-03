package com.pinodesk.javafx.converter;

import com.pinodesk.pandora.converter.DefaultStringConverterAdapter;
import com.pinodesk.viewmodel.UnitVM;

import javafx.scene.control.ComboBox;

public class UnitComboBoxConverter extends DefaultStringConverterAdapter<UnitVM> {

    public UnitComboBoxConverter(ComboBox<UnitVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(UnitVM unit) {
        return unit.getLabel();
    }

}

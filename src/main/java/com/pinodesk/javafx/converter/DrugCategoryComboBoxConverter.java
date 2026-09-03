package com.pinodesk.javafx.converter;

import com.pinodesk.pandora.converter.DefaultStringConverterAdapter;
import com.pinodesk.viewmodel.DrugCategoryVM;

import javafx.scene.control.ComboBox;

public class DrugCategoryComboBoxConverter extends DefaultStringConverterAdapter<DrugCategoryVM> {

    public DrugCategoryComboBoxConverter(ComboBox<DrugCategoryVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(DrugCategoryVM t) {
        return t.getName();
    }

}

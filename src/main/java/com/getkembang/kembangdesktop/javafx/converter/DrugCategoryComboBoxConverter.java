package com.getkembang.kembangdesktop.javafx.converter;

import com.getkembang.kembangdesktop.viewmodel.DrugCategoryVM;
import com.gitlab.muhammadkholidb.dior.converter.DefaultStringConverterAdapter;

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

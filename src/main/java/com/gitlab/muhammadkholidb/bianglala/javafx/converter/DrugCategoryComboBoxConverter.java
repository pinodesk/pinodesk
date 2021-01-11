package com.gitlab.muhammadkholidb.bianglala.javafx.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.DrugCategoryVM;

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

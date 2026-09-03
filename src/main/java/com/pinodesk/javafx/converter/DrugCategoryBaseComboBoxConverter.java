package com.pinodesk.javafx.converter;

import com.pinodesk.pandora.converter.DefaultStringConverterAdapter;
import com.pinodesk.viewmodel.DrugCategoryBaseVM;

import javafx.scene.control.ComboBox;

public class DrugCategoryBaseComboBoxConverter extends DefaultStringConverterAdapter<DrugCategoryBaseVM> {

    public DrugCategoryBaseComboBoxConverter(ComboBox<DrugCategoryBaseVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(DrugCategoryBaseVM vm) {
        return vm.getName();
    }

}

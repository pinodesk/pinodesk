package com.getkembang.kembangdesktop.javafx.converter;

import com.getkembang.kembangdesktop.viewmodel.DrugCategoryBaseVM;
import com.gitlab.muhammadkholidb.pandora.converter.DefaultStringConverterAdapter;

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

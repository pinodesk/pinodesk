package com.gitlab.muhammadkholidb.bianglala.javafx.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.DrugCategoryVM;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DrugCategoryComboBoxConverter extends StringConverter<DrugCategoryVM> {

    private ComboBox<DrugCategoryVM> cb;

    @Override
    public String toString(DrugCategoryVM dc) {
        return dc == null ? null : dc.getName();
    }

    @Override
    public DrugCategoryVM fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return cb.getItems().stream().filter(dc -> dc.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

}

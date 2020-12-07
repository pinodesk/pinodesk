package com.gitlab.muhammadkholidb.bianglala.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.UnitVM;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnitComboBoxConverter extends StringConverter<UnitVM> {

    private ComboBox<UnitVM> cb;

    @Override
    public String toString(UnitVM unit) {
        return unit == null ? null : unit.getName();
    }

    @Override
    public UnitVM fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return cb.getItems().stream().filter(unit -> unit.getName().equals(string)).findFirst().orElse(null);
    }

}

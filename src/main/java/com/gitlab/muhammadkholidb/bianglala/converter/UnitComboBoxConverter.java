package com.gitlab.muhammadkholidb.bianglala.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.UnitSearchResult;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnitComboBoxConverter extends StringConverter<UnitSearchResult> {

    private ComboBox<UnitSearchResult> cb;

    @Override
    public String toString(UnitSearchResult unit) {
        return unit == null ? null : unit.getName();
    }

    @Override
    public UnitSearchResult fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return cb.getItems().stream().filter(unit -> unit.getName().equals(string)).findFirst().orElse(null);
    }

}

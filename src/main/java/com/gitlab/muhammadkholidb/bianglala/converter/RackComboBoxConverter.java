package com.gitlab.muhammadkholidb.bianglala.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.RackSearchResult;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RackComboBoxConverter extends StringConverter<RackSearchResult> {

    private ComboBox<RackSearchResult> cb;

    @Override
    public String toString(RackSearchResult rack) {
        return rack == null ? null : rack.getName();
    }

    @Override
    public RackSearchResult fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return cb.getItems().stream().filter(rack -> rack.getName().equals(string)).findFirst().orElse(null);
    }

}

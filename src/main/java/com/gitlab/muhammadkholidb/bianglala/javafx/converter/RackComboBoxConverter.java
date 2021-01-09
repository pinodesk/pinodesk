package com.gitlab.muhammadkholidb.bianglala.javafx.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.RackVM;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RackComboBoxConverter extends StringConverter<RackVM> {

    private ComboBox<RackVM> cb;

    @Override
    public String toString(RackVM rack) {
        return rack == null ? null : rack.getName();
    }

    @Override
    public RackVM fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return cb.getItems().stream().filter(rack -> rack.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

}

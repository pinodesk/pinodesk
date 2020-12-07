package com.gitlab.muhammadkholidb.bianglala.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategoryVM;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductCategoryComboBoxConverter extends StringConverter<ProductCategoryVM> {

    private ComboBox<ProductCategoryVM> cb;

    @Override
    public String toString(ProductCategoryVM pc) {
        return pc == null ? null : pc.getName();
    }

    @Override
    public ProductCategoryVM fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return cb.getItems().stream().filter(pc -> pc.getName().equals(string)).findFirst().orElse(null);
    }

}

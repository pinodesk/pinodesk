package com.gitlab.muhammadkholidb.bianglala.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategorySearchResult;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductCategoryComboBoxConverter extends StringConverter<ProductCategorySearchResult> {

    private ComboBox<ProductCategorySearchResult> cb;

    @Override
    public String toString(ProductCategorySearchResult pc) {
        return pc == null ? null : pc.getName();
    }

    @Override
    public ProductCategorySearchResult fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return cb.getItems().stream().filter(pc -> pc.getName().equals(string)).findFirst().orElse(null);
    }

}

package com.gitlab.muhammadkholidb.bianglala.converter;

import com.gitlab.muhammadkholidb.bianglala.entity.ProductEntity;

import org.apache.commons.lang3.StringUtils;

import javafx.util.StringConverter;

public class ProductStringConverter extends StringConverter<ProductEntity> {

    private static final String SEPARATOR = " - ";

    @Override
    public String toString(ProductEntity product) {
        return product == null ? null : (product.getCode() + SEPARATOR + product.getName());
    }

    @Override
    public ProductEntity fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        if (string.indexOf(SEPARATOR) == -1) {
            return null;
        }
        String[] s = string.split(SEPARATOR);
        ProductEntity product = new ProductEntity();
        product.setCode(s[0]);
        product.setName(s[1]);
        return product;
    }
    
}

package com.gitlab.muhammadkholidb.bianglala.converter;

import com.gitlab.muhammadkholidb.bianglala.data.model.Product;

import org.apache.commons.lang3.StringUtils;

import javafx.util.StringConverter;

public class ProductStringConverter extends StringConverter<Product> {

    private static final String SEPARATOR = " - ";

    @Override
    public String toString(Product product) {
        return product == null ? null : (product.getCode() + SEPARATOR + product.getName());
    }

    @Override
    public Product fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        if (string.indexOf(SEPARATOR) == -1) {
            return null;
        }
        String[] s = string.split(SEPARATOR);
        Product product = new Product();
        product.setCode(s[0]);
        product.setName(s[1]);
        return product;
    }
    
}

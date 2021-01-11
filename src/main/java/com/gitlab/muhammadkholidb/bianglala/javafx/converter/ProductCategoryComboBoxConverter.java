package com.gitlab.muhammadkholidb.bianglala.javafx.converter;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategoryVM;

import javafx.scene.control.ComboBox;

public class ProductCategoryComboBoxConverter extends DefaultStringConverterAdapter<ProductCategoryVM> {

    public ProductCategoryComboBoxConverter(ComboBox<ProductCategoryVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(ProductCategoryVM t) {
        return t.getName();
    }

}

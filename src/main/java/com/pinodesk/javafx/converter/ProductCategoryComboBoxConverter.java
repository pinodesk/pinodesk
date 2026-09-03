package com.pinodesk.javafx.converter;

import com.pinodesk.pandora.converter.DefaultStringConverterAdapter;
import com.pinodesk.viewmodel.ProductCategoryVM;

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

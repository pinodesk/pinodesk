package pinus.desktop.javafx.converter;

import com.gitlab.muhammadkholidb.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;
import pinus.desktop.viewmodel.ProductCategoryVM;

public class ProductCategoryComboBoxConverter extends DefaultStringConverterAdapter<ProductCategoryVM> {

    public ProductCategoryComboBoxConverter(ComboBox<ProductCategoryVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(ProductCategoryVM t) {
        return t.getName();
    }

}

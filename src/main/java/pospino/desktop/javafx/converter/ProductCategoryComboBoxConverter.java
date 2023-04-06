package pospino.desktop.javafx.converter;

import com.gitlab.mudiasoft.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;
import pospino.desktop.viewmodel.ProductCategoryVM;

public class ProductCategoryComboBoxConverter extends DefaultStringConverterAdapter<ProductCategoryVM> {

    public ProductCategoryComboBoxConverter(ComboBox<ProductCategoryVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(ProductCategoryVM t) {
        return t.getName();
    }

}

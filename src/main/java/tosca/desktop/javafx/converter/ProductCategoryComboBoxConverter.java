package tosca.desktop.javafx.converter;

import tosca.desktop.viewmodel.ProductCategoryVM;
import com.gitlab.muhammadkholidb.pandora.converter.DefaultStringConverterAdapter;

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

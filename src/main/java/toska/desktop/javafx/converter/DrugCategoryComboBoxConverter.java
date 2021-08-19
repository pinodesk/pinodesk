package toska.desktop.javafx.converter;

import com.gitlab.muhammadkholidb.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;
import toska.desktop.viewmodel.DrugCategoryVM;

public class DrugCategoryComboBoxConverter extends DefaultStringConverterAdapter<DrugCategoryVM> {

    public DrugCategoryComboBoxConverter(ComboBox<DrugCategoryVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(DrugCategoryVM t) {
        return t.getName();
    }

}

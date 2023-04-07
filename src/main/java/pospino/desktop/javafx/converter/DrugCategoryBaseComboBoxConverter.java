package pospino.desktop.javafx.converter;

import com.gitlab.mudiasoft.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;
import pospino.desktop.viewmodel.DrugCategoryBaseVM;

public class DrugCategoryBaseComboBoxConverter extends DefaultStringConverterAdapter<DrugCategoryBaseVM> {

    public DrugCategoryBaseComboBoxConverter(ComboBox<DrugCategoryBaseVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(DrugCategoryBaseVM vm) {
        return vm.getName();
    }

}

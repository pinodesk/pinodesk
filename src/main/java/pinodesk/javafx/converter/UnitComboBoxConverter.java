package pinodesk.javafx.converter;

import com.gitlab.mudiasoft.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;
import pinodesk.viewmodel.UnitVM;

public class UnitComboBoxConverter extends DefaultStringConverterAdapter<UnitVM> {

    public UnitComboBoxConverter(ComboBox<UnitVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(UnitVM unit) {
        return unit.getLabel();
    }

}

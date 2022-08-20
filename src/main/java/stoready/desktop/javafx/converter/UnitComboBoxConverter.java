package stoready.desktop.javafx.converter;

import com.gitlab.muhammadkholidb.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;
import stoready.desktop.viewmodel.UnitVM;

public class UnitComboBoxConverter extends DefaultStringConverterAdapter<UnitVM> {

    public UnitComboBoxConverter(ComboBox<UnitVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(UnitVM unit) {
        return unit.getLabel();
    }

}

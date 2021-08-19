package toska.desktop.javafx.converter;

import com.gitlab.muhammadkholidb.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;
import toska.desktop.viewmodel.RackVM;

public class RackComboBoxConverter extends DefaultStringConverterAdapter<RackVM> {

    public RackComboBoxConverter(ComboBox<RackVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(RackVM t) {
        return t.getName();
    }

}

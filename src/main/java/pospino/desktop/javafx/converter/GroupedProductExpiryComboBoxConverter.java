package pospino.desktop.javafx.converter;

import java.time.format.DateTimeFormatter;

import com.gitlab.mudiasoft.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.viewmodel.GroupedProductExpiryVM;

public class GroupedProductExpiryComboBoxConverter extends DefaultStringConverterAdapter<GroupedProductExpiryVM> {

    public GroupedProductExpiryComboBoxConverter(ComboBox<GroupedProductExpiryVM> cb) {
        super(cb);
    }

    @Override
    protected String getDisplayText(GroupedProductExpiryVM px) {
        return px.getExpiredDate().format(DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN)) + " : "
                + px.getQuantity();
    }

}

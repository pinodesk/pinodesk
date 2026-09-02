package pinodesk.javafx.converter;

import java.time.format.DateTimeFormatter;

import com.pinodesk.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;
import pinodesk.constant.CommonConstants;
import pinodesk.viewmodel.GroupedProductExpiryVM;

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

package toscabox.desktop.controller.supplier;

import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import toscabox.desktop.constant.StringConstants;
import toscabox.desktop.constant.SupplierType;
import toscabox.desktop.controller.CommonDataFilterController;
import toscabox.desktop.viewmodel.SupplierFilterVM;

public class SupplierFilterController extends CommonDataFilterController<SupplierFilterVM> {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfPhone;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfAddress;

    @FXML
    private TextField tfWebsite;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbType;

    @Override
    protected void initDataFilterControlValues() {
        if (currentFilter != null) {
            tfName.setText(currentFilter.getName());
            tfCode.setText(currentFilter.getCode());
            tfPhone.setText(currentFilter.getPhone());
            tfEmail.setText(currentFilter.getEmail());
            tfAddress.setText(currentFilter.getAddress());
            tfWebsite.setText(currentFilter.getWebsite());
            if (StringUtils.isNotBlank(currentFilter.getType())) {
                ComboBoxUtils.select(
                        cbType,
                        () -> cbType.getItems().stream().filter(vm -> currentFilter.getType().equals(vm.getValue()))
                                .findAny().orElseThrow());
            }
        }
    }

    @Override
    protected SupplierFilterVM getFreshFilterValues() {
        SupplierFilterVM filter = new SupplierFilterVM();
        filter.setName(tfName.getText());
        filter.setCode(tfCode.getText());
        filter.setPhone(tfPhone.getText());
        filter.setEmail(tfEmail.getText());
        filter.setAddress(tfAddress.getText());
        filter.setWebsite(tfWebsite.getText());
        filter.setType(ComboBoxUtils.getSelectedItem(cbType).getValue());
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfName, tfCode, tfPhone, tfEmail, tfAddress, tfWebsite);
        ComboBoxUtils.selectIndex(cbType, 0);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        TextFieldUtils.setDigitTextFields(tfPhone);
        ComboBoxUtils.initSimple(
                cbType,
                new SimpleComboBoxModel(StringConstants.EMPTY, StringConstants.EMPTY),
                new SimpleComboBoxModel(SupplierType.WHOLESALER.name(), translate("lbl.wholesaler")),
                new SimpleComboBoxModel(SupplierType.RETAILER.name(), translate("lbl.retailer")));
    }

}

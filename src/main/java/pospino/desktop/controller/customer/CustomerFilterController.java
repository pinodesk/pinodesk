package pospino.desktop.controller.customer;

import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pospino.desktop.controller.CommonDataFilterController;
import pospino.desktop.viewmodel.CustomerFilterVM;

public class CustomerFilterController extends CommonDataFilterController<CustomerFilterVM> {

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

    @Override
    protected void initDataFilterControlValues() {
        if (currentFilter != null) {
            tfName.setText(currentFilter.getName());
            tfCode.setText(currentFilter.getCode());
            tfPhone.setText(currentFilter.getPhone());
            tfEmail.setText(currentFilter.getEmail());
            tfAddress.setText(currentFilter.getAddress());
        }
    }

    @Override
    protected CustomerFilterVM getFreshFilterValues() {
        CustomerFilterVM filter = new CustomerFilterVM();
        filter.setName(tfName.getText());
        filter.setCode(tfCode.getText());
        filter.setPhone(tfPhone.getText());
        filter.setEmail(tfEmail.getText());
        filter.setAddress(tfAddress.getText());
        return filter;
    }

    @Override
    protected void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
        tfAddress.setText(null);
    }

    @Override
    protected void initServices() {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        TextFieldUtils.setDigitTextFields(tfPhone);
    }

}

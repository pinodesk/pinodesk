package com.pinodesk.controller.catalog.customer;

import com.pinodesk.controller.CommonDataFilterController;
import com.pinodesk.pandora.utility.TextFieldUtils;
import com.pinodesk.viewmodel.CustomerFilterVM;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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

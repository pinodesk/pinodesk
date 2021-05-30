package com.getkembang.kembangdesktop.controller.supplier;

import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.CommonDataFilterController;
import com.getkembang.kembangdesktop.viewmodel.SupplierFilterVM;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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

    @Override
    protected void initDataFilterControlValues() {
        if (currentFilter != null) {
            tfName.setText(currentFilter.getName());
            tfCode.setText(currentFilter.getCode());
            tfPhone.setText(currentFilter.getPhone());
            tfEmail.setText(currentFilter.getEmail());
            tfAddress.setText(currentFilter.getAddress());
            tfWebsite.setText(currentFilter.getWebsite());
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
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextNull(tfName, tfCode, tfPhone, tfEmail, tfAddress, tfWebsite);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        TextFieldUtils.setDigitTextFields(tfPhone);
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_SUPPLIER_FILTER;
    }

}

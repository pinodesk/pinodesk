package com.getkembang.kembangdesktop.controller.contact;

import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.BaseFilterController;
import com.getkembang.kembangdesktop.javafx.formatter.DigitFormatter;
import com.getkembang.kembangdesktop.viewmodel.ContactFilterVM;

import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ContactFilterController extends BaseFilterController<ContactFilterVM> {

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
    private TextField tfCompanyName;

    @Override
    protected void initFilterControlsValues(final ContactFilterVM vm) {
        if (vm != null) {
            tfName.setText(vm.getName());
            tfCode.setText(vm.getCode());
            tfPhone.setText(vm.getPhone());
            tfEmail.setText(vm.getEmail());
            tfAddress.setText(vm.getAddress());
            tfCompanyName.setText(vm.getCompanyName());
        }
    }

    @Override
    protected void setFilterValues(final ContactFilterVM vm) {
        vm.setName(tfName.getText());
        vm.setCode(tfCode.getText());
        vm.setPhone(tfPhone.getText());
        vm.setEmail(tfEmail.getText());
        vm.setAddress(tfAddress.getText());
        vm.setCompanyName(tfCompanyName.getText());
    }

    @Override
    protected void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
        tfAddress.setText(null);
        tfCompanyName.setText(null);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initControlsActions() {
        tfPhone.setTextFormatter(new DigitFormatter());
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_CONTACT_FILTER;
    }

}

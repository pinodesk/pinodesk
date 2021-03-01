package com.getkembang.kembangdesktop.controller.contact;

import java.util.Objects;

import com.getkembang.kembangdesktop.constant.ContactType;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.constant.StringConstants;
import com.getkembang.kembangdesktop.controller.CommonDataFilterController;
import com.getkembang.kembangdesktop.utility.ComboBoxUtils;
import com.getkembang.kembangdesktop.utility.FXUtils;
import com.getkembang.kembangdesktop.viewmodel.BasicComboBoxVM;
import com.getkembang.kembangdesktop.viewmodel.ContactFilterVM;

import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ContactFilterController extends CommonDataFilterController<ContactFilterVM> {

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

    @FXML
    private ComboBox<BasicComboBoxVM> cbContactType;

    @Override
    protected void initDataFilterControlValues() {
        if (currentFilter != null) {
            tfName.setText(currentFilter.getName());
            tfCode.setText(currentFilter.getCode());
            tfPhone.setText(currentFilter.getPhone());
            tfEmail.setText(currentFilter.getEmail());
            tfAddress.setText(currentFilter.getAddress());
            tfCompanyName.setText(currentFilter.getCompanyName());
            ComboBoxUtils.select(cbContactType, () -> cbContactType.getItems().stream()
                    .filter(vm -> Objects.equals(currentFilter.getContactType(), vm.getValue())).findAny().get());
        }
    }

    @Override
    protected ContactFilterVM getFreshFilterValues() {
        ContactFilterVM filter = new ContactFilterVM();
        filter.setName(tfName.getText());
        filter.setCode(tfCode.getText());
        filter.setPhone(tfPhone.getText());
        filter.setEmail(tfEmail.getText());
        filter.setAddress(tfAddress.getText());
        filter.setCompanyName(tfCompanyName.getText());
        filter.setContactType(ComboBoxUtils.getSelectedItem(cbContactType).getValue());
        return filter;
    }

    @Override
    protected void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
        tfAddress.setText(null);
        tfCompanyName.setText(null);
        ComboBoxUtils.selectIndex(cbContactType, 0);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        FXUtils.setDigitTextFields(tfPhone);
        ComboBoxUtils.initBasic(cbContactType, new BasicComboBoxVM(null, StringConstants.EMPTY),
                new BasicComboBoxVM(ContactType.CUSTOMER.toString(), translate("lbl.customer")),
                new BasicComboBoxVM(ContactType.SUPPLIER.toString(), translate("lbl.supplier")));
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_CONTACT_FILTER;
    }

}

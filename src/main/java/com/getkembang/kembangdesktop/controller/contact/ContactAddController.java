package com.getkembang.kembangdesktop.controller.contact;

import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.CommonDataSaveController;
import com.getkembang.kembangdesktop.service.ContactService;
import com.getkembang.kembangdesktop.utility.ComboBoxUtils;
import com.getkembang.kembangdesktop.viewmodel.BasicComboBoxVM;
import com.getkembang.kembangdesktop.viewmodel.ContactAddVM;
import com.getkembang.kembangdesktop.viewmodel.ValidationResult;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ContactAddController extends CommonDataSaveController {

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

    @FXML
    private Button btnSaveAndAdd;

    private ContactService contactService;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_CONTACT);
            resetControls();
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        contactService = ctx.getBean(ContactService.class);
    }

    @Override
    protected void initControlActions() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initControlValues() {
        // TODO Auto-generated method stub

    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_CONTACT_ADD;
    }

    @Override
    protected ValidationResult validateValues() {
        ValidationResult result = new ValidationResult();
        if (StringUtils.isBlank(tfName.getText())) {
            result.addError(MessageCode.ERROR_EMPTY_NAME);
        }
        if (StringUtils.isBlank(tfCode.getText())) {
            result.addError(MessageCode.ERROR_EMPTY_CODE);
        }
        if (ComboBoxUtils.getSelectedItem(cbContactType).getValue() == null) {
            result.addError(MessageCode.ERROR_EMPTY_CONTACT_TYPE);
        }
        return result;
    }

    @Override
    protected boolean save() {
        ContactAddVM contact = new ContactAddVM();
        contact.setName(tfName.getText());
        contact.setCode(tfCode.getText());
        contact.setPhone(tfPhone.getText());
        contact.setEmail(tfEmail.getText());
        contact.setAddress(tfAddress.getText());
        contact.setCompanyName(tfCompanyName.getText());
        return contactService.createContact(contact) > 0;
    }

    private void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
        tfAddress.setText(null);
        tfCompanyName.setText(null);
        ComboBoxUtils.selectIndex(cbContactType, 0);
    }

}

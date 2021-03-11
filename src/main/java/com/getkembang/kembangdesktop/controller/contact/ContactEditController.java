package com.getkembang.kembangdesktop.controller.contact;

import java.util.Arrays;
import java.util.Objects;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.ContactType;
import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.CommonDataSaveController;
import com.getkembang.kembangdesktop.javafx.control.MaskedTextField;
import com.getkembang.kembangdesktop.service.ContactService;
import com.getkembang.kembangdesktop.utility.ComboBoxUtils;
import com.getkembang.kembangdesktop.utility.FXUtils;
import com.getkembang.kembangdesktop.viewmodel.AlertResult;
import com.getkembang.kembangdesktop.viewmodel.BasicComboBoxVM;
import com.getkembang.kembangdesktop.viewmodel.ContactEditVM;
import com.getkembang.kembangdesktop.viewmodel.ContactVM;
import com.getkembang.kembangdesktop.viewmodel.ValidationResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ContactEditController extends CommonDataSaveController {

    @FXML
    private TextField tfName;

    @FXML
    private MaskedTextField tfCode;

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
    private Button btnRemove;

    private String generatedCode;

    private ContactService contactService;

    private ContactVM currentContact;

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_CONTACT);
        if (result.isConfirmed()) {
            contactService.removeContacts(Arrays.asList(currentContact.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_CONTACT);
            setPrevPageData(Boolean.TRUE);
            close();
        }
    }

    @FXML
    void onActionCbContactType(ActionEvent event) {
        if (ComboBoxUtils.hasItemSelected(cbContactType)) {
            ContactType ct = ContactType.of(ComboBoxUtils.getSelectedItem(cbContactType).getValue()).orElseThrow();
            if (ContactType.SUPPLIER.equals(ct)) {
                tfCode.setMask(CommonConstants.CONTACT_MASK_SUPPLIER);
            } else if (ContactType.CUSTOMER.equals(ct)) {
                tfCode.setMask(CommonConstants.CONTACT_MASK_CUSTOMER);
            }
        }
        tfCode.setPlainText(generatedCode);
    }

    @Override
    protected void initDataSaveControlActions() {
        FXUtils.setDigitTextFields(tfPhone);
        ComboBoxUtils.initBasic(cbContactType,
                new BasicComboBoxVM(ContactType.CUSTOMER.toString(), translate("lbl.customer")),
                new BasicComboBoxVM(ContactType.SUPPLIER.toString(), translate("lbl.supplier")));
    }

    @Override
    protected void initDataSaveControlValues() {
        currentContact = getPageData();
        tfName.setText(currentContact.getName());
        tfCode.setText(currentContact.getCode());
        tfPhone.setText(currentContact.getPhone());
        tfEmail.setText(currentContact.getEmail());
        tfAddress.setText(currentContact.getAddress());
        tfCompanyName.setText(currentContact.getCompanyName());
        ComboBoxUtils.select(cbContactType, () -> cbContactType.getItems().stream()
                .filter(vm -> Objects.equals(currentContact.getContactType(), vm.getValue())).findAny().orElseThrow());
        generatedCode = currentContact.getCode().substring(1).replaceAll("-", "");
    }

    @Override
    protected ValidationResult validateValues() {
        ValidationResult result = new ValidationResult();
        if (!ComboBoxUtils.hasItemSelected(cbContactType)) {
            result.addError(MessageCode.ERROR_EMPTY_CONTACT_TYPE);
        }
        if (StringUtils.isBlank(tfName.getText())) {
            result.addError(MessageCode.ERROR_EMPTY_NAME);
        }
        if (StringUtils.isBlank(tfCode.getText())) {
            result.addError(MessageCode.ERROR_EMPTY_CODE);
        } else if (tfCode.getPlainText().length() != CommonConstants.CONTACT_CODE_PLAIN_TEXT_LENGTH) {
            result.addError(MessageCode.ERROR_INVALID_CODE_FORMAT);
        }
        if (StringUtils.isNotBlank(tfEmail.getText()) && !GenericValidator.isEmail(tfEmail.getText())) {
            result.addError(MessageCode.ERROR_INVALID_EMAIL_FORMAT);
        }
        return result;
    }

    @Override
    protected boolean save() {
        ContactEditVM contact = new ContactEditVM();
        contact.setId(currentContact.getId());
        contact.setName(tfName.getText());
        contact.setCode(tfCode.getText());
        contact.setPhone(tfPhone.getText());
        contact.setEmail(tfEmail.getText());
        contact.setAddress(tfAddress.getText());
        contact.setCompanyName(tfCompanyName.getText());
        contact.setContactType(ComboBoxUtils.getSelectedItem(cbContactType).getValue());
        return contactService.updateContact(contact);
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        contactService = ctx.getBean(ContactService.class);
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_CONTACT_EDIT;
    }

}

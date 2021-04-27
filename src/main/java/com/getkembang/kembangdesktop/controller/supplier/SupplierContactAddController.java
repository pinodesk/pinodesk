package com.getkembang.kembangdesktop.controller.supplier;

import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.CommonDataSaveController;
import com.getkembang.kembangdesktop.viewmodel.SupplierContactAddVM;
import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;
import com.gitlab.muhammadkholidb.pandora.utility.ValidationResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SupplierContactAddController extends CommonDataSaveController {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfPhone;

    @FXML
    private TextField tfEmail;

    @FXML
    private Button btnSaveAndAdd;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_CUSTOMER);
            resetControls();
            initDataSaveControlValues();
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        TextFieldUtils.setDigitTextFields(tfPhone);
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyConstants.CTRL_SHIFT_S.match(event)) {
                btnSaveAndAdd.fire();
                return;
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        // Nothing to do here
    }

    @Override
    protected ValidationResult validateValues() {
        ValidationResult result = new ValidationResult();
        if (StringUtils.isBlank(tfName.getText())) {
            result.addError(MessageCode.ERROR_EMPTY_NAME);
        }
        if (StringUtils.isNotBlank(tfEmail.getText()) && !GenericValidator.isEmail(tfEmail.getText())) {
            result.addError(MessageCode.ERROR_INVALID_EMAIL_FORMAT);
        }
        return result;
    }

    @Override
    protected Object save() {
        SupplierContactAddVM contact = new SupplierContactAddVM();
        contact.setName(tfName.getText());
        contact.setPhone(tfPhone.getText());
        contact.setEmail(tfEmail.getText());
        return contact;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // Nothing to do here
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_SUPPLIER_CONTACT_ADD;
    }

    private void resetControls() {
        tfName.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
    }

}

package com.getkembang.kembangdesktop.controller.supplier;

import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.CommonDataSaveController;
import com.getkembang.kembangdesktop.service.CustomerService;
import com.getkembang.kembangdesktop.utility.FXUtils;
import com.getkembang.kembangdesktop.viewmodel.CustomerAddVM;
import com.gitlab.muhammadkholidb.dior.utility.ValidationResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SupplierAddController extends CommonDataSaveController {

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
    private Button btnSaveAndAdd;

    private CustomerService customerService;

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
    protected void initServices(ApplicationContext ctx) {
        customerService = ctx.getBean(CustomerService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        FXUtils.setDigitTextFields(tfPhone);
        addContentPaneOnKeyPressedHandler(event -> {
            if (FXUtils.CTRL_SHIFT_S.match(event)) {
                btnSaveAndAdd.fire();
                return;
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        String nextCustomerCode = customerService.getNextCustomerCode();
        tfCode.setText(nextCustomerCode);
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_CUSTOMER_ADD;
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
        if (StringUtils.isNotBlank(tfEmail.getText()) && !GenericValidator.isEmail(tfEmail.getText())) {
            result.addError(MessageCode.ERROR_INVALID_EMAIL_FORMAT);
        }
        return result;
    }

    @Override
    protected boolean save() {
        CustomerAddVM customer = new CustomerAddVM();
        customer.setName(tfName.getText());
        customer.setCode(tfCode.getText());
        customer.setPhone(tfPhone.getText());
        customer.setEmail(tfEmail.getText());
        customer.setAddress(tfAddress.getText());
        return customerService.createCustomer(customer) > 0;
    }

    private void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
        tfAddress.setText(null);
    }

}

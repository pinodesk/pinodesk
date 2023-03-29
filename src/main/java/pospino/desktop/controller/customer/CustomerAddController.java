package pospino.desktop.controller.customer;

import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.service.CustomerService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.CustomerAddVM;

public class CustomerAddController extends CommonDataSaveController {

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
    protected void initServices() {
        customerService = SpringUtils.getBean(CustomerService.class);
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
        String nextCustomerCode = customerService.getNextCustomerCode();
        tfCode.setText(nextCustomerCode);
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
    }

    @Override
    protected Object save() {
        CustomerAddVM customer = new CustomerAddVM();
        customer.setName(tfName.getText());
        customer.setCode(tfCode.getText());
        customer.setPhone(tfPhone.getText());
        customer.setEmail(tfEmail.getText());
        customer.setAddress(tfAddress.getText());
        return customerService.createCustomer(customer);
    }

    private void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
        tfAddress.setText(null);
    }

}

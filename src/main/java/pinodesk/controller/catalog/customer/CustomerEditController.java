package pinodesk.controller.catalog.customer;

import java.util.Arrays;

import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pinodesk.constant.MenuCodeConstants;
import pinodesk.constant.MessageCode;
import pinodesk.controller.CommonDataSaveController;
import pinodesk.service.CustomerService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.CustomerEditVM;
import pinodesk.viewmodel.CustomerVM;

public class CustomerEditController extends CommonDataSaveController {

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
    private Button btnRemove;

    private CustomerService customerService;

    private CustomerVM currentCustomer;

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_CUSTOMER);
        if (result.isConfirmed()) {
            customerService.removeCustomers(Arrays.asList(currentCustomer.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_CUSTOMER);
            setPageData(Boolean.TRUE);
            close();
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        disableWriteAction(MenuCodeConstants.CATALOG_CUSTOMERS, btnSave, btnRemove);
        TextFieldUtils.setDigitTextFields(tfPhone);
    }

    @Override
    protected void initDataSaveControlValues() {
        currentCustomer = getPageData();
        tfName.setText(currentCustomer.getName());
        tfCode.setText(currentCustomer.getCode());
        tfPhone.setText(currentCustomer.getPhone());
        tfEmail.setText(currentCustomer.getEmail());
        tfAddress.setText(currentCustomer.getAddress());
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
        validator.validateEmail(tfEmail, MessageCode.ERROR_INVALID_EMAIL_FORMAT);
    }

    @Override
    protected Object save() {
        CustomerEditVM customer = new CustomerEditVM();
        customer.setId(currentCustomer.getId());
        customer.setName(tfName.getText());
        customer.setCode(tfCode.getText());
        customer.setPhone(tfPhone.getText());
        customer.setEmail(tfEmail.getText());
        customer.setAddress(tfAddress.getText());
        return customerService.updateCustomer(customer);
    }

    @Override
    protected void initServices() {
        customerService = SpringUtils.getBean(CustomerService.class);
    }

}

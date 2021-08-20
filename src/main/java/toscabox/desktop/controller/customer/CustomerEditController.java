package toscabox.desktop.controller.customer;

import java.util.Arrays;

import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.controlsfx.validation.ValidationSupport;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import toscabox.desktop.constant.MessageCode;
import toscabox.desktop.controller.CommonDataSaveController;
import toscabox.desktop.service.CustomerService;
import toscabox.desktop.viewmodel.CustomerEditVM;
import toscabox.desktop.viewmodel.CustomerVM;

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
    protected void registerValidator(ValidationSupport vs) {
        registerRequiredFields(tfName);
        registerWhitespaceValidator(tfName);
        registerEmailValidator(tfEmail, false);
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
    protected void initServices(ApplicationContext ctx) {
        customerService = ctx.getBean(CustomerService.class);
    }

}

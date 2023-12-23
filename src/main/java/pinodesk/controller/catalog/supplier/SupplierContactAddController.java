package pinodesk.controller.catalog.supplier;

import java.util.ArrayList;
import java.util.List;

import com.gitlab.mudiasoft.pandora.constant.KeyConstants;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;

import org.apache.commons.lang3.StringUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pinodesk.constant.MessageCode;
import pinodesk.controller.CommonDataSaveController;
import pinodesk.viewmodel.SupplierContactAddVM;

public class SupplierContactAddController extends CommonDataSaveController {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfPhone;

    @FXML
    private TextField tfEmail;

    @FXML
    private Button btnSaveAndAdd;

    private List<SupplierContactAddVM> contacts = new ArrayList<>();

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_SUPPLIER_CONTACT);
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
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
        validator.validateCustom(
                () -> StringUtils.isAllBlank(tfPhone.getText(), tfEmail.getText()),
                MessageCode.ERROR_EMPTY_PHONE_OR_EMAIL);
    }

    @Override
    protected Object save() {
        SupplierContactAddVM contact = new SupplierContactAddVM();
        contact.setName(tfName.getText());
        contact.setPhone(tfPhone.getText());
        contact.setEmail(tfEmail.getText());
        contacts.add(contact);
        return contacts;
    }

    @Override
    protected void initServices() {
        // Nothing to do here
    }

    private void resetControls() {
        tfName.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
    }

}

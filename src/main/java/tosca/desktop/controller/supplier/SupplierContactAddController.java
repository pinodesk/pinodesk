package tosca.desktop.controller.supplier;

import java.util.ArrayList;
import java.util.List;

import tosca.desktop.constant.MessageCode;
import tosca.desktop.controller.CommonDataSaveController;
import tosca.desktop.viewmodel.SupplierContactAddVM;
import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.controlsfx.validation.ValidationSupport;
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
        disableOnValidationError(btnSaveAndAdd);
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
    protected void registerValidator(ValidationSupport vs) {
        registerRequiredFields(tfName);
        registerWhitespaceValidator(tfName);
        registerEmailValidator(tfEmail, false);
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
    protected void initServices(ApplicationContext ctx) {
        // Nothing to do here
    }

    private void resetControls() {
        tfName.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
    }

}

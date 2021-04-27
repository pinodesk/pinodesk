package com.getkembang.kembangdesktop.controller.supplier;

import com.getkembang.kembangdesktop.constant.MessageCode;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.controller.CommonDataSaveController;
import com.getkembang.kembangdesktop.service.SupplierService;
import com.getkembang.kembangdesktop.viewmodel.SupplierAddVM;
import com.getkembang.kembangdesktop.viewmodel.SupplierContactAddVM;
import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;
import com.gitlab.muhammadkholidb.pandora.utility.ValidationResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private TextField tfWebsite;

    @FXML
    private Button btnSaveAndAdd;

    @FXML
    private Button btnAddContact;

    @FXML
    private TableView<SupplierContactAddVM> tblSupplierContact;

    @FXML
    private TableColumn<SupplierContactAddVM, String> colName;

    @FXML
    private TableColumn<SupplierContactAddVM, String> colPhone;

    @FXML
    private TableColumn<SupplierContactAddVM, String> colEmail;

    private SupplierService supplierService;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_CUSTOMER);
            resetControls();
            initDataSaveControlValues();
        }
    }

    @FXML
    void onActionBtnAddContact(ActionEvent event) {
        StageUtils.modal(Page.MASTER_SUPPLIER_CONTACT_ADD, false, we -> {
            SupplierContactAddVM contact = getPageData();
            if (contact != null) {
                tblSupplierContact.getItems().add(contact);
            }
        });
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        supplierService = ctx.getBean(SupplierService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        TableViewUtils.setColumnValue(colName, SupplierContactAddVM::getName);
        TableViewUtils.setColumnValue(colPhone, SupplierContactAddVM::getPhone);
        TableViewUtils.setColumnValue(colEmail, SupplierContactAddVM::getEmail);
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
        String nextSupplierCode = supplierService.getNextSupplierCode();
        tfCode.setText(nextSupplierCode);
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_SUPPLIER_ADD;
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
    protected Object save() {
        SupplierAddVM supplier = new SupplierAddVM();
        supplier.setName(tfName.getText());
        supplier.setCode(tfCode.getText());
        supplier.setPhone(tfPhone.getText());
        supplier.setEmail(tfEmail.getText());
        supplier.setAddress(tfAddress.getText());
        supplier.setWebsite(tfWebsite.getText());
        return supplierService.createSupplier(supplier, tblSupplierContact.getItems());
    }

    private void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfPhone.setText(null);
        tfEmail.setText(null);
        tfAddress.setText(null);
    }

}

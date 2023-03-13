package pospino.desktop.controller.supplier;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.service.SupplierService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.SupplierAddVM;
import pospino.desktop.viewmodel.SupplierContactAddVM;

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
            displayInfo(MessageCode.SUCCESS_ADD_SUPPLIER);
            resetControls();
            initDataSaveControlValues();
        }
    }

    @FXML
    void onActionBtnAddContact(ActionEvent event) {
        StageUtils.modal(Page.MASTER_SUPPLIER_CONTACT_ADD, false, we -> {
            List<SupplierContactAddVM> contacts = getPageData();
            if (CollectionUtils.isNotEmpty(contacts)) {
                tblSupplierContact.getItems().addAll(contacts);
            }
        });
    }

    @FXML
    void onActionBtnRemoveContact(ActionEvent event) {
        ObservableList<SupplierContactAddVM> items = tblSupplierContact.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            tblSupplierContact.getItems().removeAll(items);
        }
        if (tblSupplierContact.getItems().isEmpty()) {
            tblSupplierContact.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        }
    }

    @Override
    protected void initServices() {
        supplierService = SpringUtils.getBean(SupplierService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        tblSupplierContact.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblSupplierContact.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
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
        TextFieldUtils.setTextEmpty(tfName, tfCode, tfPhone, tfEmail, tfAddress, tfWebsite);
    }

}

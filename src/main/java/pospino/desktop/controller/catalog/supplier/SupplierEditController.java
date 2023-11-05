package pospino.desktop.controller.catalog.supplier;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.TableViewUtils;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;

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
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.service.SupplierService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.SupplierContactAddVM;
import pospino.desktop.viewmodel.SupplierEditVM;
import pospino.desktop.viewmodel.SupplierVM;

public class SupplierEditController extends CommonDataSaveController {

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
    private Button btnAddContact;

    @FXML
    private Button btnRemove;

    @FXML
    private TableView<SupplierContactAddVM> tblSupplierContact;

    @FXML
    private TableColumn<SupplierContactAddVM, String> colName;

    @FXML
    private TableColumn<SupplierContactAddVM, String> colPhone;

    @FXML
    private TableColumn<SupplierContactAddVM, String> colEmail;

    private SupplierService supplierService;

    private SupplierVM currentSupplier;

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SUPPLIER);
        if (result.isConfirmed()) {
            supplierService.removeSuppliers(Arrays.asList(currentSupplier.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_SUPPLIER);
            setPageData(Boolean.TRUE);
            close();
        }
    }

    @FXML
    void onActionBtnAddContact(ActionEvent event) {
        StageUtils.modal(Page.CATALOG_SUPPLIER_CONTACT_ADD, false, we -> {
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
    protected void initDataSaveControlActions() {
        disableWriteAction(MenuCodeConstants.CATALOG_SUPPLIERS, btnSave, btnRemove);
        tblSupplierContact.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblSupplierContact.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableViewUtils.setColumnValue(colName, SupplierContactAddVM::getName);
        TableViewUtils.setColumnValue(colPhone, SupplierContactAddVM::getPhone);
        TableViewUtils.setColumnValue(colEmail, SupplierContactAddVM::getEmail);
        TextFieldUtils.setDigitTextFields(tfPhone);
    }

    @Override
    protected void initDataSaveControlValues() {
        currentSupplier = getPageData();
        tfName.setText(currentSupplier.getName());
        tfCode.setText(currentSupplier.getCode());
        tfPhone.setText(currentSupplier.getPhone());
        tfEmail.setText(currentSupplier.getEmail());
        tfAddress.setText(currentSupplier.getAddress());
        tfWebsite.setText(currentSupplier.getWebsite());
        List<SupplierContactAddVM> contacts = supplierService.getSupplierContacts(currentSupplier.getId());
        tblSupplierContact.getItems().addAll(contacts);
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
    }

    @Override
    protected Object save() {
        SupplierEditVM supplier = new SupplierEditVM();
        supplier.setId(currentSupplier.getId());
        supplier.setName(tfName.getText());
        supplier.setCode(tfCode.getText());
        supplier.setPhone(tfPhone.getText());
        supplier.setEmail(tfEmail.getText());
        supplier.setAddress(tfAddress.getText());
        supplier.setWebsite(tfWebsite.getText());
        return supplierService.updateSupplier(supplier, tblSupplierContact.getItems());
    }

    @Override
    protected void initServices() {
        supplierService = SpringUtils.getBean(SupplierService.class);
    }

}

package toscabox.desktop.controller.supplier;

import java.util.Arrays;
import java.util.List;

import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.apache.commons.collections.CollectionUtils;
import org.controlsfx.validation.ValidationSupport;
import org.springframework.context.ApplicationContext;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import toscabox.desktop.constant.MessageCode;
import toscabox.desktop.constant.Page;
import toscabox.desktop.constant.SupplierType;
import toscabox.desktop.controller.CommonDataSaveController;
import toscabox.desktop.service.SupplierService;
import toscabox.desktop.viewmodel.SupplierContactAddVM;
import toscabox.desktop.viewmodel.SupplierEditVM;
import toscabox.desktop.viewmodel.SupplierVM;

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
    private Button btnSaveAndAdd;

    @FXML
    private Button btnAddContact;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbType;

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
            tblSupplierContact.setPlaceholder(new Label(translate("lbl.nodata")));
        }
    }

    @Override
    protected void initDataSaveControlActions() {
        tblSupplierContact.setPlaceholder(new Label(translate("lbl.nodata")));
        tblSupplierContact.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableViewUtils.setColumnValue(colName, SupplierContactAddVM::getName);
        TableViewUtils.setColumnValue(colPhone, SupplierContactAddVM::getPhone);
        TableViewUtils.setColumnValue(colEmail, SupplierContactAddVM::getEmail);
        ComboBoxUtils.initSimple(
                cbType,
                new SimpleComboBoxModel(SupplierType.WHOLESALER.name(), translate("lbl.wholesaler")),
                new SimpleComboBoxModel(SupplierType.RETAILER.name(), translate("lbl.retailer")));
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
        ComboBoxUtils.select(
                cbType,
                () -> cbType.getItems().stream().filter(vm -> currentSupplier.getType().equals(vm.getValue())).findAny()
                        .orElseThrow());
        List<SupplierContactAddVM> contacts = supplierService.getSupplierContacts(currentSupplier.getId());
        tblSupplierContact.getItems().addAll(contacts);
    }

    @Override
    protected void registerValidator(ValidationSupport vs) {
        registerRequiredFields(tfName);
        registerWhitespaceValidator(tfName);
        registerEmailValidator(tfEmail, false);
        registerDomainValidator(tfWebsite, false);
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
        supplier.setType(ComboBoxUtils.getSelectedItem(cbType).getValue());
        return supplierService.updateSupplier(supplier, tblSupplierContact.getItems());
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        supplierService = ctx.getBean(SupplierService.class);
    }

}

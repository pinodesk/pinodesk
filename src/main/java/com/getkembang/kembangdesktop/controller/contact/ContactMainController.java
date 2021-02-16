package com.getkembang.kembangdesktop.controller.contact;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.ContactType;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.constant.StringConstants;
import com.getkembang.kembangdesktop.controller.BaseController;
import com.getkembang.kembangdesktop.javafx.factory.DateCellFactory;
import com.getkembang.kembangdesktop.service.ContactService;
import com.getkembang.kembangdesktop.utility.Async;
import com.getkembang.kembangdesktop.utility.ComboBoxUtils;
import com.getkembang.kembangdesktop.utility.FXUtils;
import com.getkembang.kembangdesktop.utility.TableViewUtils;
import com.getkembang.kembangdesktop.viewmodel.BasicComboBoxVM;
import com.getkembang.kembangdesktop.viewmodel.ContactFilterVM;
import com.getkembang.kembangdesktop.viewmodel.ContactVM;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContactMainController extends BaseController {

    @FXML
    private ComboBox<BasicComboBoxVM> cbContactType;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<ContactVM> tableContact;

    @FXML
    private TableColumn<ContactVM, String> colCode;

    @FXML
    private TableColumn<ContactVM, String> colName;

    @FXML
    private TableColumn<ContactVM, String> colPhone;

    @FXML
    private TableColumn<ContactVM, String> colEmail;

    @FXML
    private TableColumn<ContactVM, String> colAddress;

    @FXML
    private TableColumn<ContactVM, String> colCompanyName;

    @FXML
    private TableColumn<ContactVM, Date> colCreatedAt;

    @FXML
    private TableColumn<ContactVM, Date> colUpdatedAt;

    @FXML
    private Label lblRows;

    private ContactService contactService;

    private ContactFilterVM contactFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) throws IOException {
        Page nextPage = Page.MASTER_CONTACT_ADD;
        setNextPage(nextPage);
        FXUtils.show(nextPage, false, we -> {
            if (Boolean.TRUE.equals(getPageData())) {
                searchContacts();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) throws IOException {
        Page nextPage = Page.MASTER_CONTACT_FILTER;
        setNextPageData(nextPage, contactFilter);
        FXUtils.show(nextPage, false, we -> {
            contactFilter = getPageData();
            // It will trigger combobox's onActionCbContactType()
            ComboBoxUtils.clearSelection(cbContactType);
            ComboBoxUtils.select(cbContactType, () -> cbContactType.getItems().stream()
                    .filter(vm -> Objects.equals(contactFilter.getContactType(), vm.getValue())).findAny().get());
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        //
    }

    @FXML
    void onActionCbContactType(ActionEvent event) {
        if (ComboBoxUtils.hasItemSelected(cbContactType)) {
            BasicComboBoxVM selected = ComboBoxUtils.getSelectedItem(cbContactType);
            String value = selected.getValue();
            contactFilter.setContactType(value);
            searchContacts();
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        contactService = ctx.getBean(ContactService.class);
    }

    @Override
    protected void initControlActions() {
        tableContact.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableViewUtils.setColumnValue(colCode, ContactVM::getCode);
        TableViewUtils.setColumnValue(colName, ContactVM::getName);
        TableViewUtils.setColumnValue(colPhone, ContactVM::getPhone);
        TableViewUtils.setColumnValue(colEmail, ContactVM::getEmail);
        TableViewUtils.setColumnValue(colAddress, ContactVM::getAddress);
        TableViewUtils.setColumnValue(colCompanyName, ContactVM::getCompanyName);
        TableViewUtils.initTableColumn(colCreatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                ContactVM::getCreatedAt);
        TableViewUtils.initTableColumn(colUpdatedAt, new DateCellFactory<>(CommonConstants.DATETIME_PATTERN),
                ContactVM::getUpdatedAt);
        ComboBoxUtils.initBasic(cbContactType, new BasicComboBoxVM(null, StringConstants.EMPTY),
                new BasicComboBoxVM(ContactType.CUSTOMER.toString(), translate("lbl.customer")),
                new BasicComboBoxVM(ContactType.SUPPLIER.toString(), translate("lbl.supplier")));
        ComboBoxUtils.selectIndex(cbContactType, 0);
    }

    @Override
    protected void initControlValues() {
        contactFilter = new ContactFilterVM();
        searchContacts();
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_CONTACT_MAIN;
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    @SuppressWarnings("unchecked")
    private void searchContacts() {
        tableContact.setPlaceholder(new Label(translate("lbl.loadingdata")));
        tableContact.setItems(FXCollections.observableArrayList());
        Async.supply(() -> contactService.searchContacts(contactFilter))
                .thenAccept(contacts -> Platform.runLater(() -> {
                    if (contacts.isEmpty()) {
                        tableContact.setPlaceholder(new Label(translate("lbl.nodata")));
                        lblRows.setText("0");
                    }
                    tableContact.setItems(FXCollections.observableList(contacts));
                    tableContact.getSortOrder().setAll(colName); // Always sort by name after searching
                    lblRows.setText(contacts.size() + "");
                }));
    }

}

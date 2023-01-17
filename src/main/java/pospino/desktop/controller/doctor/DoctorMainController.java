package pospino.desktop.controller.doctor;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.controller.BaseController;
import pospino.desktop.service.DoctorService;
import pospino.desktop.viewmodel.DoctorFilterVM;
import pospino.desktop.viewmodel.DoctorVM;

public class DoctorMainController extends BaseController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<DoctorVM> tblDoctors;

    @FXML
    private TableColumn<DoctorVM, String> colCode;

    @FXML
    private TableColumn<DoctorVM, String> colName;

    @FXML
    private TableColumn<DoctorVM, String> colRegistrationNumber;

    @FXML
    private TableColumn<DoctorVM, String> colMedicalLicenseNumber;

    @FXML
    private TableColumn<DoctorVM, String> colCategory;

    @FXML
    private TableColumn<DoctorVM, String> colPhone;

    @FXML
    private TableColumn<DoctorVM, String> colEmail;

    @FXML
    private TableColumn<DoctorVM, String> colAddress;

    @FXML
    private TableColumn<DoctorVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<DoctorVM, LocalDateTime> colUpdatedAt;

    @FXML
    private Label lblRows;

    private DoctorService doctorService;

    private DoctorFilterVM doctorFilter;

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        StageUtils.modal(Page.MASTER_DOCTOR_ADD, false, we -> {
            if (getPageData() != null) {
                searchDoctors();
            }
        });
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(doctorFilter);
        StageUtils.modal(Page.MASTER_DOCTOR_FILTER, false, we -> {
            DoctorFilterVM result = getPageData();
            if (result == null) {
                return;
            }
            doctorFilter = result;
            searchDoctors();
        });
    }

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        ObservableList<DoctorVM> items = tblDoctors.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_SELECTED_DOCTORS);
            if (result.isConfirmed()) {
                doctorService.removeDoctors(items.stream().map(DoctorVM::getId).toList());
                displayInfo(MessageCode.SUCCESS_REMOVE_SELECTED_DOCTORS);
                searchDoctors();
            }
        }
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        doctorService = ctx.getBean(DoctorService.class);
    }

    @Override
    protected void initControlActions() {
        disableWriteAction(MenuCodeConstants.MASTER_DOCTORS, btnAdd, btnRemove);
        TableViewUtils.setColumnValue(colCode, DoctorVM::getCode);
        TableViewUtils.setColumnValue(colName, DoctorVM::getName);
        TableViewUtils.setColumnValue(colRegistrationNumber, DoctorVM::getRegistrationNumber);
        TableViewUtils.setColumnValue(colMedicalLicenseNumber, DoctorVM::getMedicalLicenseNumber);
        TableViewUtils.setColumnValue(colCategory, DoctorVM::getCategoryName);
        TableViewUtils.setColumnValue(colEmail, DoctorVM::getEmail);
        TableViewUtils.setColumnValue(colPhone, DoctorVM::getPhone);
        TableViewUtils.setColumnValue(colAddress, DoctorVM::getAddress);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                DoctorVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                DoctorVM::getUpdatedAt);
        tblDoctors.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblDoctors.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblDoctors.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                handleActionTblDoctors();
            }
        });
        tblDoctors.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                handleActionTblDoctors();
            }
        });
    }

    @Override
    protected void initControlValues() {
        doctorFilter = new DoctorFilterVM();
        searchDoctors();
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    private void searchDoctors() {
        tblDoctors.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblDoctors.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> doctorService.searchDoctorsByFilter(doctorFilter))
                .thenAccept(doctors -> Platform.runLater(() -> {
                    if (doctors.isEmpty()) {
                        tblDoctors.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                        lblRows.setText("0");
                    }
                    tblDoctors.setItems(FXCollections.observableList(doctors));
                    TableViewUtils.sortDescending(tblDoctors, colUpdatedAt);
                    lblRows.setText(doctors.size() + "");
                }));
    }

    private void handleActionTblDoctors() {
        if (TableViewUtils.hasItemSelected(tblDoctors)) {
            setPageData(TableViewUtils.getSelectedItem(tblDoctors));
            StageUtils.modal(Page.MASTER_DOCTOR_EDIT, false, event -> {
                if (getPageData() != null) {
                    searchDoctors();
                }
            });
        }
    }

}

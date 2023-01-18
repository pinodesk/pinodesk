package pospino.desktop.controller.doctor;

import java.time.LocalDateTime;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.controller.CommonDataChooseController;
import pospino.desktop.service.DoctorService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.DoctorVM;

public class DoctorChooseController extends CommonDataChooseController<DoctorVM> {

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<DoctorVM> tblDoctor;

    @FXML
    private TableColumn<DoctorVM, String> colCode;

    @FXML
    private TableColumn<DoctorVM, String> colName;

    @FXML
    private TableColumn<DoctorVM, String> colCategory;

    @FXML
    private TableColumn<DoctorVM, String> colRegistrationNumber;

    @FXML
    private TableColumn<DoctorVM, String> colMedicalLicenseNumber;

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

    private DoctorService doctorService;

    @Override
    protected void initDataChooseControlActions() {
        TableViewUtils.setColumnValue(colCode, DoctorVM::getCode);
        TableViewUtils.setColumnValue(colName, DoctorVM::getName);
        TableViewUtils.setColumnValue(colCategory, DoctorVM::getCategoryName);
        TableViewUtils.setColumnValue(colRegistrationNumber, DoctorVM::getRegistrationNumber);
        TableViewUtils.setColumnValue(colMedicalLicenseNumber, DoctorVM::getMedicalLicenseNumber);
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
        tblDoctor.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        tblDoctor.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblDoctor.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                btnChoose.fire();
            }
        });
        tfSearch.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                searchDoctors();
            }
        });
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        // Nothing to do
    }

    @Override
    protected DoctorVM getSelectedData() {
        return tblDoctor.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        doctorService = SpringUtils.getBean(DoctorService.class);
    }

    private void searchDoctors() {
        tblDoctor.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblDoctor.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> doctorService.searchDoctorsByKeyword(tfSearch.getText()))
                .thenAccept(doctors -> Platform.runLater(() -> {
                    if (doctors.isEmpty()) {
                        tblDoctor.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblDoctor.setItems(FXCollections.observableList(doctors));
                    TableViewUtils.sortAscending(tblDoctor, colName);
                }));
    }

}

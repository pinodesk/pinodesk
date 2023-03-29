package pospino.desktop.controller.doctor;

import java.util.Arrays;

import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.service.DoctorService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.DoctorCategoryVM;
import pospino.desktop.viewmodel.DoctorEditVM;
import pospino.desktop.viewmodel.DoctorVM;

public class DoctorEditController extends CommonDataSaveController {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfMedicalLicenseNumber;

    @FXML
    private TextField tfRegistrationNumber;

    @FXML
    private TextField tfCategory;

    @FXML
    private TextField tfPhone;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfAddress;

    @FXML
    private Button btnRemove;

    private DoctorService doctorService;

    private DoctorCategoryVM selectedDoctorCategory;

    private DoctorVM currentDoctor;

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_REMOVE_DOCTOR);
        if (result.isConfirmed()) {
            doctorService.removeDoctors(Arrays.asList(currentDoctor.getId()));
            displayInfo(MessageCode.SUCCESS_REMOVE_DOCTOR);
            setPageData(Boolean.TRUE);
            close();
        }
    }

    @Override
    protected void initServices() {
        doctorService = SpringUtils.getBean(DoctorService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        disableWriteAction(MenuCodeConstants.CATALOG_DOCTORS, btnSave, btnRemove);
        setDoctorCategoryChooser(tfCategory, this::handleSelectedDoctorCategory, tfRegistrationNumber);
        TextFieldUtils.setDigitTextFields(tfPhone);
    }

    @Override
    protected void initDataSaveControlValues() {
        currentDoctor = getPageData();
        tfName.setText(currentDoctor.getName());
        tfCode.setText(currentDoctor.getCode());
        tfMedicalLicenseNumber.setText(currentDoctor.getMedicalLicenseNumber());
        tfRegistrationNumber.setText(currentDoctor.getRegistrationNumber());
        tfCategory.setText(currentDoctor.getCategoryName());
        tfAddress.setText(currentDoctor.getAddress());
        tfEmail.setText(currentDoctor.getEmail());
        tfPhone.setText(currentDoctor.getPhone());
        selectedDoctorCategory = doctorService.getDoctorCategoryById(currentDoctor.getCategoryId());
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
        validator.validateBlank(tfCategory, MessageCode.ERROR_EMPTY_CATEGORY);
        validator.validateEmail(tfEmail, MessageCode.ERROR_INVALID_EMAIL_FORMAT);
    }

    @Override
    protected Object save() {
        DoctorEditVM doctor = new DoctorEditVM();
        doctor.setName(tfName.getText());
        doctor.setCode(tfCode.getText());
        doctor.setMedicalLicenseNumber(tfMedicalLicenseNumber.getText());
        doctor.setRegistrationNumber(tfRegistrationNumber.getText());
        doctor.setCategory(selectedDoctorCategory);
        doctor.setAddress(tfAddress.getText());
        doctor.setEmail(tfEmail.getText());
        doctor.setPhone(tfPhone.getText());
        return doctorService.updateDoctor(doctor, currentDoctor.getId());
    }

    public void handleSelectedDoctorCategory(ChooseResultVM<DoctorCategoryVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(category -> {
            selectedDoctorCategory = category;
            tfCategory.setText(category.getName());
        }, () -> {
            selectedDoctorCategory = null;
            tfCategory.setText("");
        });
    }

}

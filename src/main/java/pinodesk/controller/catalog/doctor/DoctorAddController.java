package pinodesk.controller.catalog.doctor;

import com.pinodesk.pandora.constant.KeyConstants;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.TextFieldUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pinodesk.constant.MessageCode;
import pinodesk.controller.CommonDataSaveController;
import pinodesk.service.DoctorService;
import pinodesk.util.SpringUtils;
import pinodesk.viewmodel.ChooseResultVM;
import pinodesk.viewmodel.DoctorAddVM;
import pinodesk.viewmodel.DoctorCategoryVM;

public class DoctorAddController extends CommonDataSaveController {

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
    private Button btnSaveAndAdd;

    private DoctorService doctorService;

    private DoctorCategoryVM selectedDoctorCategory;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        processDataSave();
        if (isLastDataSaved()) {
            displayInfo(MessageCode.SUCCESS_ADD_DOCTOR);
            resetControls();
            initDataSaveControlValues();
        }
    }

    @Override
    protected void initServices() {
        doctorService = SpringUtils.getBean(DoctorService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        setDoctorCategoryChooser(tfCategory, this::handleSelectedDoctorCategory, tfRegistrationNumber);
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyConstants.CTRL_SHIFT_S.match(event)) {
                btnSaveAndAdd.fire();
                return;
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        String nextCustomerCode = doctorService.getNextDoctorCode();
        tfCode.setText(nextCustomerCode);
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
        validator.validateBlank(tfCategory, MessageCode.ERROR_EMPTY_CATEGORY);
        validator.validateEmail(tfEmail, MessageCode.ERROR_INVALID_EMAIL_FORMAT);
    }

    @Override
    protected Object save() {
        DoctorAddVM doctor = new DoctorAddVM();
        doctor.setName(tfName.getText());
        doctor.setCode(tfCode.getText());
        doctor.setMedicalLicenseNumber(tfMedicalLicenseNumber.getText());
        doctor.setRegistrationNumber(tfRegistrationNumber.getText());
        doctor.setCategoryCode(selectedDoctorCategory.getCode());
        doctor.setAddress(tfAddress.getText());
        doctor.setEmail(tfEmail.getText());
        doctor.setPhone(tfPhone.getText());
        return doctorService.createDoctor(doctor);
    }

    private void resetControls() {
        TextFieldUtils.setTextEmpty(
                tfName,
                tfCode,
                tfMedicalLicenseNumber,
                tfRegistrationNumber,
                tfCategory,
                tfPhone,
                tfEmail,
                tfAddress);
        selectedDoctorCategory = null;
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

package stoready.desktop.controller.doctor;

import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.utility.ControlValidator;

import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import stoready.desktop.constant.MessageCode;
import stoready.desktop.controller.CommonDataSaveController;
import stoready.desktop.service.DoctorService;
import stoready.desktop.viewmodel.*;

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
    protected void initServices(ApplicationContext ctx) {
        doctorService = ctx.getBean(DoctorService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        setDoctorCategoryChooser(tfCategory, this::handleSelectedDoctorCategory, contentPane);
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
    }

    @Override
    protected Object save() {
        DoctorAddVM doctor = new DoctorAddVM();
        doctor.setName(tfName.getText());
        doctor.setCode(tfCode.getText());
        doctor.setMedicalLicenseNumber(tfMedicalLicenseNumber.getText());
        doctor.setRegistrationNumber(tfRegistrationNumber.getText());
        if (selectedDoctorCategory != null) {
            doctor.setCategoryCode(selectedDoctorCategory.getCode());
        }
        return doctorService.createDoctor(doctor);
    }

    private void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfMedicalLicenseNumber.setText(null);
        tfRegistrationNumber.setText(null);
        tfCategory.setText(null);
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

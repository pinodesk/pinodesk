package pospino.desktop.controller.doctor;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pospino.desktop.controller.CommonDataFilterController;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.DoctorCategoryVM;
import pospino.desktop.viewmodel.DoctorFilterVM;

public class DoctorFilterController extends CommonDataFilterController<DoctorFilterVM> {

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

    private DoctorCategoryVM selectedDoctorCategory;

    @Override
    protected void initDataFilterControlValues() {
        if (currentFilter != null) {
            selectedDoctorCategory = currentFilter.getCategory();
            tfName.setText(currentFilter.getName());
            tfCode.setText(currentFilter.getCode());
            tfMedicalLicenseNumber.setText(currentFilter.getMedicalLicenseNumber());
            tfRegistrationNumber.setText(currentFilter.getRegistrationNumber());
            if (selectedDoctorCategory != null) {
                tfCategory.setText(selectedDoctorCategory.getName());
            }
            tfAddress.setText(currentFilter.getAddress());
            tfEmail.setText(currentFilter.getEmail());
            tfPhone.setText(currentFilter.getPhone());
        }
    }

    @Override
    protected DoctorFilterVM getFreshFilterValues() {
        DoctorFilterVM filter = new DoctorFilterVM();
        filter.setName(tfName.getText());
        filter.setCode(tfCode.getText());
        filter.setMedicalLicenseNumber(tfMedicalLicenseNumber.getText());
        filter.setRegistrationNumber(tfRegistrationNumber.getText());
        filter.setCategory(selectedDoctorCategory);
        filter.setAddress(tfAddress.getText());
        filter.setEmail(tfEmail.getText());
        filter.setPhone(tfPhone.getText());
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(
                tfName,
                tfCode,
                tfMedicalLicenseNumber,
                tfRegistrationNumber,
                tfCategory,
                tfAddress,
                tfEmail,
                tfPhone);
        selectedDoctorCategory = null;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        setDoctorCategoryChooser(tfCategory, this::handleSelectedDoctorCategory, contentPane);
        TextFieldUtils.setDigitTextFields(tfPhone);
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

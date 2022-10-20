package stoready.desktop.controller.doctor;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import stoready.desktop.controller.CommonDataFilterController;
import stoready.desktop.viewmodel.ChooseResultVM;
import stoready.desktop.viewmodel.DoctorCategoryVM;
import stoready.desktop.viewmodel.DoctorFilterVM;

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
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfName, tfCode, tfMedicalLicenseNumber, tfRegistrationNumber, tfCategory);
        selectedDoctorCategory = null;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        setDoctorCategoryChooser(tfCategory, this::handleSelectedDoctorCategory, contentPane);
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

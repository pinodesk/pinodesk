package com.pinodesk.controller.settings.expensecategory;

import java.util.Objects;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.controller.CommonDataFilterController;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.viewmodel.ExpenseCategoryFilterVM;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ExpenseCategoryFilterController extends CommonDataFilterController<ExpenseCategoryFilterVM> {
    @FXML
    private TextField tfName;
    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;

    @Override
    protected void initServices() {
    }

    @Override
    protected void initDataFilterControlActions() {
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(null, ""),
                new SimpleComboBoxModel(UserStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(UserStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE)));
    }

    @Override
    protected void initDataFilterControlValues() {
        if (currentFilter != null) {
            tfName.setText(currentFilter.getName());
            cbStatus.getItems().stream().filter(item -> Objects.equals(item.getValue(), currentFilter.getStatus()))
                    .findFirst().ifPresent(cbStatus::setValue);
        }
    }

    @Override
    protected ExpenseCategoryFilterVM getFreshFilterValues() {
        ExpenseCategoryFilterVM value = new ExpenseCategoryFilterVM();
        value.setName(tfName.getText());
        value.setStatus(cbStatus.getValue().getValue());
        return value;
    }

    @Override
    protected void resetControls() {
        tfName.clear();
        cbStatus.getSelectionModel().selectFirst();
    }
}

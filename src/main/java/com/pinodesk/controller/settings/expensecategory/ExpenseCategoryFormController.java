package com.pinodesk.controller.settings.expensecategory;

import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.controller.CommonDataSaveController;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.service.ExpenseCategoryService;
import com.pinodesk.util.SpringUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ExpenseCategoryFormController extends CommonDataSaveController {
    @FXML
    private TextField tfName;
    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;
    private ExpenseCategoryService service;
    private ExpenseCategory current;

    @Override
    protected void initServices() {
        service = SpringUtils.getBean(ExpenseCategoryService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        btnSave.setDisable(!com.pinodesk.util.ExpenseCategoryControls.canWrite(sessionService));
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(UserStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(UserStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE)));
    }

    @Override
    protected void initDataSaveControlValues() {
        current = getPageData();
        ComboBoxUtils.selectIndex(cbStatus, 0);
        if (current != null) {
            tfName.setText(current.getName());
            UserStatus status = UserStatus.ACTIVE.toString().equals(current.getStatus()) ?
                    UserStatus.ACTIVE : UserStatus.INACTIVE;
            ComboBoxUtils.select(
                    cbStatus,
                    () -> cbStatus.getItems().stream().filter(item -> status.equals(item.getValue())).findFirst()
                            .orElseThrow());
        }
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
        validator
                .validateCustom(() -> tfName.getText().trim().length() > 100, MessageCode.ERROR_PAYMENT_METHOD_INVALID);
    }

    @Override
    protected Object save() {
        return service.save(
                current == null ? null : current.getId(),
                tfName.getText(),
                ComboBoxUtils.getSelectedItem(cbStatus).getValue());
    }
}

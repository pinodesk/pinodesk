package com.pinodesk.controller.settings.paymentmethod;

import com.pinodesk.util.PaymentMethodControls;

import java.util.Locale;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.PaymentMethodCategory;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.controller.CommonDataSaveController;
import com.pinodesk.entity.PaymentMethod;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.service.PaymentMethodService;
import com.pinodesk.util.SpringUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class PaymentMethodFormController extends CommonDataSaveController {
    @FXML
    private TextField tfName;
    @FXML
    private ComboBox<PaymentMethodCategory> cbCategory;
    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;
    private PaymentMethodService service;
    private PaymentMethod current;

    @Override
    protected void initServices() {
        service = SpringUtils.getBean(PaymentMethodService.class);
    }

    @Override
    protected void initDataSaveControlActions() {
        btnSave.setDisable(!PaymentMethodControls.canWrite(sessionService));
        cbCategory.getItems().setAll(PaymentMethodCategory.values());
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(UserStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(UserStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE)));
        cbCategory.setConverter(new StringConverter<>() {
            @Override
            public String toString(PaymentMethodCategory value) {
                return value == null ?
                        "" : resources.getString("payment_category_" + value.name().toLowerCase(Locale.ROOT));
            }

            @Override
            public PaymentMethodCategory fromString(String value) {
                return null;
            }
        });
    }

    @Override
    protected void initDataSaveControlValues() {
        current = getPageData();
        cbCategory.setValue(PaymentMethodCategory.CASH);
        ComboBoxUtils.selectIndex(cbStatus, 0);
        if (current != null) {
            tfName.setText(current.getName());
            cbCategory.setValue(PaymentMethodCategory.valueOf(current.getCategory()));
            cbCategory.setDisable(current.isDefaultMethod());
            UserStatus status = UserStatus.ACTIVE.toString().equals(current.getStatus()) ?
                    UserStatus.ACTIVE : UserStatus.INACTIVE;
            ComboBoxUtils.select(
                    cbStatus,
                    () -> cbStatus.getItems().stream().filter(vm -> status.equals(vm.getValue())).findFirst()
                            .orElseThrow());
        }
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validateBlank(tfName, MessageCode.ERROR_EMPTY_NAME);
        validator.validateCustom(
                () -> tfName.getText().trim().length() > 100 || cbCategory.getValue() == null,
                MessageCode.ERROR_PAYMENT_METHOD_INVALID);
    }

    @Override
    protected Object save() {
        return service.save(
                current == null ? null : current.getId(),
                tfName.getText(),
                cbCategory.getValue(),
                ComboBoxUtils.getSelectedItem(cbStatus).getValue());
    }
}

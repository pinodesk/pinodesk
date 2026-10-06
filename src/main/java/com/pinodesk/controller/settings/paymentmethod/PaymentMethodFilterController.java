package com.pinodesk.controller.settings.paymentmethod;

import java.util.Locale;
import java.util.Objects;

import com.pinodesk.constant.PaymentMethodCategory;
import com.pinodesk.constant.CommonLabel;
import com.pinodesk.constant.StringConstants;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.controller.CommonDataFilterController;
import com.pinodesk.pandora.model.SimpleComboBoxModel;
import com.pinodesk.pandora.utility.ComboBoxUtils;
import com.pinodesk.viewmodel.PaymentMethodFilterVM;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class PaymentMethodFilterController extends CommonDataFilterController<PaymentMethodFilterVM> {
    @FXML
    private TextField tfName;
    @FXML
    private ComboBox<SimpleComboBoxModel> cbCategory;
    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;

    @Override
    protected void initServices() {
    }

    @Override
    protected void initDataFilterControlActions() {
        ComboBoxUtils.initSimple(cbCategory, new SimpleComboBoxModel(null, StringConstants.EMPTY));
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(UserStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(UserStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE)));
        for (PaymentMethodCategory category : PaymentMethodCategory.values())
            cbCategory.getItems().add(
                    new SimpleComboBoxModel(
                            category.name(),
                            resources.getString("payment_category_" + category.name().toLowerCase(Locale.ROOT))));
    }

    @Override
    protected void initDataFilterControlValues() {
        if (currentFilter != null) {
            tfName.setText(currentFilter.getName());
            cbCategory.getItems().stream().filter(m -> Objects.equals(m.getValue(), currentFilter.getCategory()))
                    .findFirst().ifPresent(cbCategory::setValue);
            cbStatus.getItems().stream().filter(m -> Objects.equals(m.getValue(), currentFilter.getStatus()))
                    .findFirst().ifPresent(cbStatus::setValue);
        }
    }

    @Override
    protected PaymentMethodFilterVM getFreshFilterValues() {
        PaymentMethodFilterVM filter = new PaymentMethodFilterVM();
        filter.setName(tfName.getText());
        filter.setCategory(cbCategory.getValue().getValue());
        filter.setStatus(cbStatus.getValue().getValue());
        return filter;
    }

    @Override
    protected void resetControls() {
        tfName.clear();
        cbCategory.getSelectionModel().selectFirst();
        cbStatus.getSelectionModel().selectFirst();
    }
}

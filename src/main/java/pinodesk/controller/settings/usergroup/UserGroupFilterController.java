package pinodesk.controller.settings.usergroup;

import com.gitlab.mudiasoft.pandora.model.SimpleComboBoxModel;
import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.StringConstants;
import pinodesk.constant.UserGroupStatus;
import pinodesk.controller.CommonDataFilterController;
import pinodesk.viewmodel.UserGroupFilterVM;

public class UserGroupFilterController extends CommonDataFilterController<UserGroupFilterVM> {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfDescription;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbStatus, 0);
        if (currentFilter != null) {
            tfName.setText(currentFilter.getName());
            tfDescription.setText(currentFilter.getDescription());
            UserGroupStatus status = currentFilter.getStatus();
            if (status != null) {
                ComboBoxUtils.select(
                        cbStatus,
                        () -> cbStatus.getItems().stream().filter(vm -> status.equals(vm.getValue())).findAny()
                                .orElseThrow());
            }
        }
    }

    @Override
    protected UserGroupFilterVM getFreshFilterValues() {
        UserGroupFilterVM filter = new UserGroupFilterVM();
        filter.setName(tfName.getText());
        filter.setDescription(tfDescription.getText());
        filter.setStatus(ComboBoxUtils.getSelectedItem(cbStatus).getValue());
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfName, tfDescription);
        ComboBoxUtils.selectIndex(cbStatus, 0);
    }

    @Override
    protected void initServices() {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(UserGroupStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(UserGroupStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE)));
    }

}

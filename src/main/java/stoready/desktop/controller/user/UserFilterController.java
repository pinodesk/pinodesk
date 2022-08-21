package stoready.desktop.controller.user;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import stoready.desktop.constant.CommonLabel;
import stoready.desktop.constant.StringConstants;
import stoready.desktop.constant.UserStatus;
import stoready.desktop.controller.CommonDataFilterController;
import stoready.desktop.viewmodel.ChooseResultVM;
import stoready.desktop.viewmodel.UserFilterVM;
import stoready.desktop.viewmodel.UserGroupVM;

public class UserFilterController extends CommonDataFilterController<UserFilterVM> {

    @FXML
    private TextField tfFullName;

    @FXML
    private TextField tfUsername;

    @FXML
    private TextField tfUserGroup;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;

    private UserGroupVM selectedUserGroup;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbStatus, 0);
        if (currentFilter != null) {
            tfFullName.setText(currentFilter.getFullName());
            tfUsername.setText(currentFilter.getUsername());
            UserGroupVM userGroup = currentFilter.getUserGroup();
            if (userGroup != null) {
                selectedUserGroup = userGroup;
                tfUserGroup.setText(userGroup.getName());
            }
            UserStatus status = currentFilter.getStatus();
            if (status != null) {
                ComboBoxUtils.select(
                        cbStatus,
                        () -> cbStatus.getItems().stream().filter(vm -> status.equals(vm.getValue())).findAny()
                                .orElseThrow());
            }
        }
    }

    @Override
    protected UserFilterVM getFreshFilterValues() {
        UserFilterVM filter = new UserFilterVM();
        filter.setFullName(tfFullName.getText());
        filter.setUsername(tfUsername.getText());
        filter.setStatus(ComboBoxUtils.getSelectedItem(cbStatus).getValue());
        filter.setUserGroup(selectedUserGroup);
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(tfFullName, tfUsername, tfUserGroup);
        ComboBoxUtils.selectIndex(cbStatus, 0);
        selectedUserGroup = null;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initDataFilterControlActions() {
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(UserStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(UserStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE)));
        setUserGroupChooser(tfUserGroup, this::handleSelectedUserGroup, cbStatus.getParent());
    }

    public void handleSelectedUserGroup(ChooseResultVM<UserGroupVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(userGroup -> {
            selectedUserGroup = userGroup;
            tfUserGroup.setText(userGroup.getName());
        }, () -> {
            selectedUserGroup = null;
            tfUserGroup.setText("");
        });
    }

}

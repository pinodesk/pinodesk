package pinodesk.controller;

import com.pinodesk.pandora.constant.KeyConstants;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

public abstract class CommonDataFilterController<T> extends CommonContentPaneController {

    protected T currentFilter;

    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnReset;

    @FXML
    protected Button btnFilter;

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        setPageData(currentFilter);
        close();
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {
        setPageData(getFreshFilterValues());
        close();
    }

    @FXML
    void onActionBtnReset(ActionEvent event) {
        resetControls();
    }

    @Override
    protected void initControlValues() {
        currentFilter = getPageData();
        initDataFilterControlValues();
    }

    @Override
    protected void initContentPaneControlActions() {
        initDataFilterControlActions();
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyCode.ENTER.equals(event.getCode()) || KeyConstants.CTRL_S.match(event)) {
                btnFilter.fire();
                return;
            }
            if (KeyCode.ESCAPE.equals(event.getCode())) {
                btnCancel.fire();
                return;
            }
            if (KeyConstants.CTRL_R.match(event)) {
                btnReset.fire();
                return;
            }
        });
        resetControls();
    }

    protected abstract void initDataFilterControlValues();

    protected abstract void initDataFilterControlActions();

    protected abstract T getFreshFilterValues();

    protected abstract void resetControls();

}

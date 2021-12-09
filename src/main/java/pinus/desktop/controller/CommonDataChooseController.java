package pinus.desktop.controller;

import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

public abstract class CommonDataChooseController<T> extends CommonContentPaneController {

    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnChoose;

    @FXML
    void onActionBtnChoose(ActionEvent event) {
        setPageData(getSelectedData());
        close();
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        close();
    }

    @Override
    protected void initContentPaneControlActions() {
        initDataChooseControlActions();
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyConstants.CTRL_S.match(event)) {
                btnChoose.fire();
                return;
            }
            if (KeyCode.ESCAPE.equals(event.getCode())) {
                btnCancel.fire();
                return;
            }
        });
    }

    @Override
    protected void initControlValues() {
        initDataChooseControlValues();
    }

    protected abstract void initDataChooseControlActions();

    protected abstract void initDataChooseControlValues();

    protected abstract T getSelectedData();

}

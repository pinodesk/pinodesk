package pospino.desktop.controller;

import java.util.Optional;

import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import pospino.desktop.viewmodel.ChooseResultVM;

public abstract class CommonDataChooseController<T> extends CommonContentPaneController {

    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnChoose;

    @FXML
    void onActionBtnChoose(ActionEvent event) {
        setPageData(new ChooseResultVM<>(false, Optional.ofNullable(getSelectedData())));
        close();
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        setPageData(new ChooseResultVM<>(true, Optional.empty()));
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

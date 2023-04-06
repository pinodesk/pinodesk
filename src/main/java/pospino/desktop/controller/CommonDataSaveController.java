package pospino.desktop.controller;

import org.apache.commons.lang3.ObjectUtils;

import com.gitlab.mudiasoft.pandora.constant.KeyConstants;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;
import com.gitlab.mudiasoft.pandora.utility.ValidationResult;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

public abstract class CommonDataSaveController extends CommonContentPaneController {

    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnSave;

    /**
     * A flag indicating the controller has done at least once successful data
     * saving.
     */
    private boolean hasDataSaved;

    private Object lastDataSaved;

    protected boolean isLastDataSaved() {
        return Boolean.TRUE.equals(lastDataSaved) || ObjectUtils.isNotEmpty(lastDataSaved);
    }

    @FXML
    protected void onActionBtnSave(ActionEvent event) {
        processDataSave();
        if (hasDataSaved && lastDataSaved != null) {
            setPageData(lastDataSaved);
            close();
        }
    }

    @FXML
    protected void onActionBtnCancel(ActionEvent event) {
        if (hasDataSaved && lastDataSaved != null) {
            setPageData(lastDataSaved);
        }
        close();
    }

    protected void processDataSave() {
        lastDataSaved = null;
        ControlValidator validator = new ControlValidator(resources);
        validate(validator);
        ValidationResult result = validator.getResult();
        if (!result.isValid()) {
            displayError(result.getMessages());
            return;
        }
        lastDataSaved = save();
        if (!hasDataSaved) {
            hasDataSaved = true;
        }
    }

    @Override
    protected void initContentPaneControlActions() {
        initDataSaveControlActions();
        addContentPaneOnKeyPressedHandler(event -> {
            if (KeyCode.ENTER.equals(event.getCode()) || KeyConstants.CTRL_S.match(event)) {
                btnSave.fire();
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
        initDataSaveControlValues();
    }

    protected abstract void initDataSaveControlActions();

    protected abstract void initDataSaveControlValues();

    /**
     * Handles the process of storing data and returns true or non empty object for
     * successful operation.
     * 
     * @return the saved object.
     */
    protected abstract Object save();

    protected abstract void validate(ControlValidator validator);

}

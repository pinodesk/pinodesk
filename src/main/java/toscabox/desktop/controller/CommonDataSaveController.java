package toscabox.desktop.controller;

import java.util.Arrays;

import com.gitlab.muhammadkholidb.pandora.constant.KeyConstants;
import com.gitlab.muhammadkholidb.pandora.utility.CommonValidator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import toscabox.desktop.constant.MessageCode;

public abstract class CommonDataSaveController extends CommonContentPaneController {

    @FXML
    protected Button btnCancel;

    @FXML
    protected Button btnSave;

    private ValidationSupport validationSupport = new ValidationSupport();

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
    void onActionBtnSave(ActionEvent event) {
        processDataSave();
        if (hasDataSaved) {
            setPageData(lastDataSaved);
            close();
        }
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        if (hasDataSaved) {
            setPageData(lastDataSaved);
        }
        close();
    }

    protected void processDataSave() {
        lastDataSaved = null;
        if (Boolean.TRUE.equals(validationSupport.isInvalid())) {
            displayError(MessageCode.ERROR_INCOMPLETE_FORM);
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
        disableOnValidationError(btnSave);
        registerValidator(validationSupport);
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
     * Handles the process of storing and returns true or non empty object for
     * successful operation.
     * 
     * @return the saved object.
     */
    protected abstract Object save();

    protected void disableOnValidationError(ButtonBase btn) {
        btn.disableProperty().bind(validationSupport.invalidProperty());
    }

    protected abstract void registerValidator(ValidationSupport vs);

    protected void registerRequiredFields(Control... controls) {
        if (ArrayUtils.isNotEmpty(controls)) {
            Arrays.stream(controls).forEach(
                    control -> validationSupport.registerValidator(
                            control,
                            Validator.createEmptyValidator(translate(MessageCode.ERROR_REQUIRED))));
        }
    }

    protected void registerWhitespaceValidator(Control control, boolean required) {
        validationSupport.registerValidator(
                control,
                required,
                CommonValidator.createWhitespaceValidator(translate(MessageCode.ERROR_EMPTY_OR_BLANK)));
    }

    protected void registerWhitespaceValidator(Control control) {
        registerWhitespaceValidator(control, true);
    }

    protected void registerEmailValidator(Control control, boolean required) {
        validationSupport.registerValidator(
                control,
                required,
                CommonValidator.createEmailValidator(translate(MessageCode.ERROR_INVALID_EMAIL_FORMAT)));
    }

    protected void registerEmailValidator(Control control) {
        registerEmailValidator(control, true);
    }

    protected void registerDomainValidator(Control control, boolean required) {
        validationSupport.registerValidator(
                control,
                required,
                CommonValidator.createDomainValidator(translate(MessageCode.ERROR_INVALID_DOMAIN_FORMAT)));
    }

    protected void registerDomainValidator(Control control) {
        registerDomainValidator(control, true);
    }

}

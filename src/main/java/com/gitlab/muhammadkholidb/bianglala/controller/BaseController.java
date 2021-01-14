package com.gitlab.muhammadkholidb.bianglala.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;

import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseController {

    @FXML
    protected ResourceBundle resources;

    @FXML
    protected URL location;

    @FXML
    void initialize() {
        setDefaultUncaughtExceptionHandler();
        initServices(ApplicationContextHolder.getApplicationContext());
        initControls();
    }

    protected abstract void initServices(ApplicationContext ctx);

    protected abstract void initControls();

    // https://stackoverflow.com/questions/12409638/java-exception-handling-catching-superclass-exception
    private static void setDefaultUncaughtExceptionHandler() {
        try {
            if (Thread.getDefaultUncaughtExceptionHandler() == null) {
                Thread.setDefaultUncaughtExceptionHandler(
                        (t, e) -> log.error("Uncaught Exception detected in thread: " + t, e));
            }
        } catch (SecurityException e) {
            log.error("Could not set the Default Uncaught Exception Handler", e);
        }
    }

    private String getAlertHeaderLabelByType(AlertType type) {
        switch (type) {
            case INFORMATION:
                return "lbl.information";
            case ERROR:
                return "lbl.error";
            default:
                return "";
        }
    }

    protected String translate(String messageCode) {
        try {
            return resources.getString(messageCode);
        } catch (Exception e) {
            log.warn("Failed to translate message code '{}': {}", messageCode, e.toString());
            return messageCode;
        }
    }

    protected Optional<ButtonType> displayAlert(AlertType type, String messageCode) {
        Alert alert = new Alert(type);
        alert.setTitle(CommonConstants.APP_TITLE);
        alert.setHeaderText(translate(getAlertHeaderLabelByType(type)));
        alert.setContentText(translate(messageCode));
        return alert.showAndWait();
    }

    protected Optional<ButtonType> displayError(String message) {
        return displayAlert(AlertType.ERROR, message);
    }

    protected Optional<ButtonType> displayInfo(String message) {
        return displayAlert(AlertType.INFORMATION, message);
    }

}

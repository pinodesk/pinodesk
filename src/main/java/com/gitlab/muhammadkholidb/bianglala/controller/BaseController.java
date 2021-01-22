package com.gitlab.muhammadkholidb.bianglala.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.MessageCode;
import com.gitlab.muhammadkholidb.bianglala.constant.Page;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.FXUtils;
import com.gitlab.muhammadkholidb.bianglala.utility.PageData;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
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
        initControlsActions();
        initControlsValues();
    }

    protected abstract void initServices(ApplicationContext ctx);

    protected abstract void initControlsActions();

    protected abstract void initControlsValues();

    protected abstract Page getCurrentPage();

    protected abstract Stage getCurrentStage();

    protected void close() {
        getCurrentStage().close();
    }

    protected <T> T getPageData() {
        PageData pageData = PageData.INSTANCE;
        return pageData.get(pageData.getPageSet());
    }

    protected <T> void setPrevPageData(T data) {
        PageData pageData = PageData.INSTANCE;
        pageData.set(pageData.getPageSet().swap(), data);
    }

    protected <T> void setNextPageData(Page to, T data) {
        PageData.INSTANCE.set(getCurrentPage(), to, data);
    }

    // https://stackoverflow.com/questions/12409638/java-exception-handling-catching-superclass-exception
    private void setDefaultUncaughtExceptionHandler() {
        try {
            if (Thread.getDefaultUncaughtExceptionHandler() == null) {
                Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                    log.error("Uncaught exception detected in thread: " + t.getName(), e);
                    displayException(e);
                });
            }
        } catch (SecurityException e) {
            log.error("Unable to execute Thread.setDefaultUncaughtExceptionHandler()", e);
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

    private String getAlertHeaderMessageCode(AlertType type) {
        switch (type) {
            case INFORMATION:
                return "lbl.information";
            case ERROR:
                return "lbl.error";
            default:
                return "";
        }
    }

    protected Optional<ButtonType> displayAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        FXUtils.setDefaultIcons((Stage) alert.getDialogPane().getScene().getWindow());
        alert.setTitle(CommonConstants.APP_TITLE);
        alert.setHeaderText(translate(getAlertHeaderMessageCode(type)));
        alert.setContentText(message);
        return alert.showAndWait();
    }

    protected Optional<ButtonType> displayError(String message) {
        return displayAlert(AlertType.ERROR, message);
    }

    protected Optional<ButtonType> displayError(MessageCode messageCode) {
        return displayError(translate(messageCode.toString()));
    }

    protected Optional<ButtonType> displayInfo(String message) {
        return displayAlert(AlertType.INFORMATION, message);
    }

    protected Optional<ButtonType> displayInfo(MessageCode messageCode) {
        return displayInfo(translate(messageCode.toString()));
    }

    // From https://code.makery.ch/blog/javafx-dialogs-official/
    protected void displayException(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(CommonConstants.APP_TITLE);
        alert.setHeaderText(translate("lbl.systemerror"));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinWidth(800);
        dialogPane.setPrefWidth(800);

        FXUtils.setDefaultIcons((Stage) dialogPane.getScene().getWindow());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label(translate("lbl.details") + ":");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    protected Date parseDateQuietly(String str, String pattern) {
        try {
            return DateUtils.parseDate(str, pattern);
        } catch (ParseException e) {
            return null;
        }
    }

    protected String toStringOrDefault(BigDecimal num, String dflt) {
        return num == null ? dflt : num.setScale(0).toString();
    }

    protected String toStringOrDefault(Integer num, String dflt) {
        return num == null ? dflt : num.toString();
    }

}

package com.getkembang.kembangdesktop.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.getkembang.kembangdesktop.Kembang;
import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.constant.StringConstants;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.exception.FXException;
import com.getkembang.kembangdesktop.utility.SpringUtils;
import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.IMessage;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.toolbox.data.SingletonStack;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
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
        initServices(SpringUtils.getApplicationContext());
        initControlActions();
        initControlValues();
    }

    protected abstract void initServices(ApplicationContext ctx);

    protected abstract void initControlActions();

    protected abstract void initControlValues();

    protected abstract Stage getCurrentStage();

    protected void close() {
        getCurrentStage().close();
    }

    /**
     * Returns the topmost (last) data set for a page.
     */
    protected <T> T getPageData() {
        return SingletonStack.INSTANCE.pop();
    }

    /**
     * Sets data for a page on the topmost order of the data stack.
     * 
     * @param <T> type of the data.
     * @param data the data for a page.
     */
    protected <T> void setPageData(T data) {
        SingletonStack.INSTANCE.push(data);
    }

    // https://stackoverflow.com/questions/12409638/java-exception-handling-catching-superclass-exception
    private void setDefaultUncaughtExceptionHandler() {
        try {
            if (Thread.getDefaultUncaughtExceptionHandler() == null) {
                Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                    Throwable rootCause = ExceptionUtils.getRootCause(e);
                    if (rootCause instanceof DomainException) {
                        handleDomainException((DomainException) rootCause);
                        return;
                    }
                    if (rootCause instanceof FXException) {
                        handleFXException((FXException) rootCause);
                        return;
                    }
                    log.error("Uncaught exception detected in thread: " + t.getName(), rootCause);
                    displayException(rootCause);
                });
            }
        } catch (SecurityException e) {
            log.error("Unable to execute Thread.setDefaultUncaughtExceptionHandler()", e);
        }
    }

    private void handleDomainException(DomainException e) {
        DomainError err = e.getError();
        displayError(String.format("%s. (%s)", translate(err.messageCode()), err.code()));
    }

    private void handleFXException(FXException e) {
        log.error("FXException caught!", e);
        displayException(e.getCause());
    }

    protected String translate(String messageCode) {
        try {
            return resources.getString(messageCode);
        } catch (Exception e) {
            log.warn("Failed to translate message code '{}': {}", messageCode, e.toString());
            return messageCode;
        }
    }

    protected String translate(IMessage messageCode) {
        return translate(messageCode.toString());
    }

    private String getAlertHeaderMessageCode(AlertType type) {
        switch (type) {
        case INFORMATION:
            return "lbl.information";
        case ERROR:
            return "lbl.error";
        case CONFIRMATION:
            return "lbl.confirmation";
        default:
            return "";
        }
    }

    protected AlertResult displayAlert(AlertType type, String message) {
        ButtonType btnTypeOk = new ButtonType(translate("btn.ok"), ButtonData.OK_DONE);
        ButtonType btnTypeYes = new ButtonType(translate("btn.yes"), ButtonData.YES);
        ButtonType btnTypeNo = new ButtonType(translate("btn.no"), ButtonData.NO);
        Alert alert = new Alert(type);
        alert.setTitle(CommonConstants.APP_TITLE);
        alert.setHeaderText(translate(getAlertHeaderMessageCode(type)));
        DialogPane dialogPane = alert.getDialogPane();
        StageUtils.setIcons((Stage) dialogPane.getScene().getWindow(), Kembang.ICON_PATHS);
        Text text = new Text(message);
        text.setWrappingWidth(dialogPane.getWidth());
        text.setStyle("-fx-font-size: 13px");
        AnchorPane.setLeftAnchor(text, 2d);
        AnchorPane.setTopAnchor(text, 2d);
        AnchorPane contentPane = new AnchorPane(text);
        dialogPane.setContent(contentPane);
        dialogPane.getButtonTypes().clear();
        dialogPane.getStylesheets().add(getClass().getResource("/assets/css/kembang-desktop.css").toExternalForm());
        switch (type) {
        case INFORMATION:
        case ERROR:
            dialogPane.getButtonTypes().add(btnTypeOk);
            dialogPane.lookupButton(btnTypeOk).getStyleClass().add("btn-primary");
            break;
        case CONFIRMATION:
            dialogPane.getButtonTypes().addAll(btnTypeYes, btnTypeNo);
            dialogPane.lookupButton(btnTypeYes).getStyleClass().add("btn-primary");
            dialogPane.lookupButton(btnTypeNo).getStyleClass().add("btn-secondary");
            break;
        default:
            break;
        }
        return new AlertResult(alert.showAndWait());
    }

    protected AlertResult displayError(String message) {
        return displayAlert(AlertType.ERROR, message);
    }

    protected AlertResult displayError(IMessage messageCode) {
        return displayError(translate(messageCode.toString()));
    }

    protected AlertResult displayError(Collection<IMessage> messageCodes) {
        List<String> messages = messageCodes.stream().map(this::translate).collect(Collectors.toList());
        String message = StringUtils.join(messages, "\n");
        return displayError(message);
    }

    protected AlertResult displayInfo(String message) {
        return displayAlert(AlertType.INFORMATION, message);
    }

    protected AlertResult displayInfo(IMessage messageCode) {
        return displayInfo(translate(messageCode.toString()));
    }

    protected AlertResult displayConfirmation(String message) {
        return displayAlert(AlertType.CONFIRMATION, message);
    }

    protected AlertResult displayConfirmation(IMessage messageCode) {
        return displayConfirmation(translate(messageCode.toString()));
    }

    // From https://code.makery.ch/blog/javafx-dialogs-official/
    protected void displayException(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(CommonConstants.APP_TITLE);
        alert.setHeaderText(translate("lbl.systemerror"));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinWidth(600);
        dialogPane.setPrefWidth(600);

        StageUtils.setIcons((Stage) dialogPane.getScene().getWindow(), Kembang.ICON_PATHS);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label(translate("lbl.details") + StringConstants.COLON);

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        dialogPane.setExpandableContent(expContent);

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

    protected String toStringOrNull(BigDecimal num) {
        return toStringOrDefault(num, null);
    }

    protected String toStringOrEmpty(BigDecimal num) {
        return toStringOrDefault(num, "");
    }

    protected String toStringOrDefault(Integer num, String dflt) {
        return num == null ? dflt : num.toString();
    }

    protected String toStringOrNull(Integer num) {
        return toStringOrDefault(num, null);
    }

    protected String toStringOrEmpty(Integer num) {
        return toStringOrDefault(num, "");
    }

}

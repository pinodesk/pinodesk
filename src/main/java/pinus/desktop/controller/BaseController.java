package pinus.desktop.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.IMessage;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;
import com.gitlab.muhammadkholidb.pandora.utility.Translator;
import com.gitlab.muhammadkholidb.toolbox.data.SingletonStack;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.DomainError;
import pinus.desktop.constant.Page;
import pinus.desktop.constant.StringConstants;
import pinus.desktop.exception.DomainException;
import pinus.desktop.util.ResourceBundleUtils;
import pinus.desktop.util.SpringUtils;

@Slf4j
public abstract class BaseController {

    protected Translator t;

    @FXML
    protected ResourceBundle resources;

    @FXML
    protected URL location;

    @FXML
    void initialize() {
        t = new Translator(resources);
        setDefaultUncaughtExceptionHandler();
        initServices(SpringUtils.getApplicationContext());
        initControlActions();
        initControlValues();
    }

    protected abstract void initServices(ApplicationContext ctx);

    protected abstract void initControlActions();

    protected abstract void initControlValues();

    protected abstract Stage getCurrentStage();

    protected void setFocused(Node node) {
        Platform.runLater(node::requestFocus);
    }

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
     * @param <T>  type of the data.
     * @param data the data for a page.
     */
    protected <T> void setPageData(T data) {
        SingletonStack.INSTANCE.push(data);
    }

    // https://stackoverflow.com/questions/12409638/java-exception-handling-catching-superclass-exception
    private void setDefaultUncaughtExceptionHandler() {
        try {
            if (Thread.getDefaultUncaughtExceptionHandler() == null) {
                Thread.setDefaultUncaughtExceptionHandler((t, e) -> handleException(e, t));
            }
        } catch (SecurityException e) {
            log.error("Unable to execute Thread.setDefaultUncaughtExceptionHandler()", e);
        }
    }

    protected void handleException(Throwable e) {
        e.printStackTrace();
        handleException(e, Thread.currentThread());
    }

    protected void handleException(Throwable e, Thread t) {
        Throwable rootCause = ExceptionUtils.getRootCause(e);
        if (rootCause instanceof DomainException domainException) {
            handleDomainException(domainException);
            return;
        }
        if (log.isErrorEnabled()) {
            log.error("Uncaught exception detected in thread: " + t.getName(), rootCause);
        }
        displayException(rootCause);
    }

    protected void handleDomainException(DomainException e) {
        ResourceBundle rb = ResourceBundleUtils.getDefaultResourceBundle();
        Translator translator = new Translator(rb);
        DomainError err = e.getError();
        String message = String.format(translator.translate(err.messageCode()), e.getArguments());
        displayError(String.format("%s. (%s)", message, err.code()));
    }

    private IMessage getAlertHeaderMessageCode(AlertType type) {
        switch (type) {
            case INFORMATION:
                return CommonLabel.LBL_INFORMATION;
            case ERROR:
                return CommonLabel.LBL_ERROR;
            case CONFIRMATION:
                return CommonLabel.LBL_CONFIRMATION;
            default:
                return null;
        }
    }

    protected AlertResult displayAlert(AlertType type, String message) {
        ButtonType btnTypeOk = new ButtonType(t.translate(CommonLabel.BTN_OK), ButtonData.OK_DONE);
        ButtonType btnTypeYes = new ButtonType(t.translate(CommonLabel.BTN_YES), ButtonData.YES);
        ButtonType btnTypeNo = new ButtonType(t.translate(CommonLabel.BTN_NO), ButtonData.NO);
        Alert alert = new Alert(type);
        alert.setTitle(CommonConstants.APP_TITLE);
        alert.setHeaderText(t.translate(getAlertHeaderMessageCode(type)));
        DialogPane dialogPane = alert.getDialogPane();
        Text text = new Text(message);
        text.setWrappingWidth(dialogPane.getWidth());
        text.setStyle("-fx-font-size: 13px");
        AnchorPane.setLeftAnchor(text, 2d);
        AnchorPane.setTopAnchor(text, 2d);
        AnchorPane contentPane = new AnchorPane(text);
        dialogPane.setContent(contentPane);
        dialogPane.getButtonTypes().clear();
        dialogPane.getStylesheets().add(getClass().getResource("/assets/css/pinus-desktop.css").toExternalForm());
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
        dialogPane.applyCss();
        HBox buttonContainer = (HBox) dialogPane.lookup(".container");
        buttonContainer.setSpacing(1);
        buttonContainer.requestFocus();
        return new AlertResult(alert.showAndWait());
    }

    protected AlertResult displayError(String message) {
        return displayAlert(AlertType.ERROR, message);
    }

    protected AlertResult displayError(IMessage messageCode) {
        return displayError(t.translate(messageCode.toString()));
    }

    protected AlertResult displayError(Collection<?> messageCodes) {
        List<String> messages = messageCodes.stream().map(val -> {
            if (val instanceof String str) {
                return str;
            }
            if (val instanceof IMessage im) {
                return t.translate(im);
            }
            return null;
        }).filter(Objects::nonNull).toList();
        String message = StringUtils.join(messages, "\n");
        return displayError(message);
    }

    protected AlertResult displayInfo(String message) {
        return displayAlert(AlertType.INFORMATION, message);
    }

    protected AlertResult displayInfo(IMessage messageCode) {
        return displayInfo(t.translate(messageCode.toString()));
    }

    protected AlertResult displayConfirmation(String message) {
        return displayAlert(AlertType.CONFIRMATION, message);
    }

    protected AlertResult displayConfirmation(IMessage messageCode) {
        return displayConfirmation(t.translate(messageCode.toString()));
    }

    // From https://code.makery.ch/blog/javafx-dialogs-official/
    protected void displayException(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(CommonConstants.APP_TITLE);
        alert.setHeaderText(t.translate(CommonLabel.LBL_SYSTEM_ERROR));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinWidth(600);
        dialogPane.setPrefWidth(600);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label(t.translate(CommonLabel.LBL_DETAILS) + StringConstants.COLON);

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

    protected Stage displayLoading() {
        return StageUtils.modal(Page.LOADING, StageStyle.UNDECORATED);
    }

    protected String formatNumber(Number number) {
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(resources.getLocale()));
        df.setGroupingUsed(true);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(number);
    }

    protected <T> void setChooserOnFocus(
            TextField tf,
            boolean isFirstInput,
            Page page,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        final BooleanProperty forget = new SimpleBooleanProperty(true); // Variable to store the focus on stage load
        tf.focusedProperty().addListener((o, ov, nv) -> {
            boolean isFocused = Boolean.TRUE.equals(nv);
            if (isFirstInput && isFocused && forget.get()) {
                setFocused(tf.getParent());
                forget.setValue(false);
                return;
            }
            if (isFocused) {
                StageUtils.modal(page, false, we -> outputConsumer.accept(getPageData()));
                if (nextFocusNode != null) {
                    setFocused(nextFocusNode);
                }
            }
        });
    }

    protected <T> void setProductCategoryChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.MASTER_PRODUCT_CHOOSE_CATEGORY, outputConsumer, nextFocusNode);
    }

    protected <T> void setProductCategoryChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setProductCategoryChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setUnitChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.MASTER_PRODUCT_CHOOSE_UNIT, outputConsumer, nextFocusNode);
    }

    protected <T> void setUnitChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setUnitChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setProductChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.MASTER_PRODUCT_CHOOSE, outputConsumer, nextFocusNode);
    }

    protected <T> void setProductChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setProductChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setSupplierChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.MASTER_SUPPLIER_CHOOSE, outputConsumer, nextFocusNode);
    }

    protected <T> void setSupplierChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setSupplierChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setCustomerChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.MASTER_CUSTOMER_CHOOSE, outputConsumer, nextFocusNode);
    }

    protected <T> void setCustomerChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setCustomerChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setDoctorChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.MASTER_DOCTOR_CHOOSE, outputConsumer, nextFocusNode);
    }

    protected <T> void setDoctorChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setDoctorChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setDoctorCategoryChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.MASTER_DOCTOR_CHOOSE_CATEGORY, outputConsumer, nextFocusNode);
    }

    protected <T> void setDoctorCategoryChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setDoctorCategoryChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setDrugClassificationChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(
                tf,
                isFirstInput,
                Page.MASTER_PRODUCT_CHOOSE_DRUG_CLASSIFICATION,
                outputConsumer,
                nextFocusNode);
    }

    protected <T> void setDrugClassificationChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setDrugClassificationChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setUserGroupChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.SETTINGS_USER_GROUP_CHOOSE, outputConsumer, nextFocusNode);
    }

    protected <T> void setUserGroupChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setUserGroupChooser(tf, false, outputConsumer, nextFocusNode);
    }

}

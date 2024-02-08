package pinodesk.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.gitlab.mudiasoft.pandora.converter.DefaultDatePickerConverter;
import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.IMessage;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;
import com.gitlab.mudiasoft.pandora.utility.Translator;
import com.gitlab.mudiasoft.toolbox.data.SingletonStack;
import com.gitlab.mudiasoft.toolbox.jackson.JSON;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import oshi.util.Constants;
import pinodesk.apimodel.ActivateReleaseResponse;
import pinodesk.apimodel.CreateIssueRequest;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.CommonLabel;
import pinodesk.constant.ConfigurationConstants;
import pinodesk.constant.DomainError;
import pinodesk.constant.MessageCode;
import pinodesk.constant.Page;
import pinodesk.constant.SimpleStatus;
import pinodesk.exception.DefaultRuntimeException;
import pinodesk.exception.DomainException;
import pinodesk.exception.PinodeskApiException;
import pinodesk.exception.PrinterException;
import pinodesk.properties.ApplicationProperties;
import pinodesk.service.ConfigurationService;
import pinodesk.service.SessionService;
import pinodesk.service.api.PinodeskApiService;
import pinodesk.util.DeviceUtils;
import pinodesk.util.SpringUtils;
import pinodesk.util.TaskUtils;

@Slf4j
public abstract class BaseController {

    protected Translator t;

    protected ApplicationProperties applicationProperties;

    protected SessionService sessionService;

    protected ConfigurationService configurationService;

    protected PinodeskApiService pinodeskApiService;

    protected DateTimeFormatter datetimeFormatter = DateTimeFormatter
            .ofPattern(CommonConstants.DATETIME_DISPLAY_PATTERN);
    protected DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN);

    @FXML
    protected ResourceBundle resources;

    @FXML
    protected URL location;

    @FXML
    void initialize() {
        t = new Translator(resources);
        applicationProperties = SpringUtils.getBean(ApplicationProperties.class);
        sessionService = SpringUtils.getBean(SessionService.class);
        configurationService = SpringUtils.getBean(ConfigurationService.class);
        pinodeskApiService = SpringUtils.getBean(PinodeskApiService.class);
        setDefaultUncaughtExceptionHandler();
        initServices();
        initControlActions();
        initControlValues();
    }

    protected abstract void initServices();

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
        if (rootCause instanceof PrinterException printerException) {
            handlePrinterException(printerException);
            return;
        }
        if (rootCause instanceof PinodeskApiException pinodeskApiException) {
            handlePinodeskApiException(pinodeskApiException);
            return;
        }
        log.error("Uncaught exception detected in thread: " + t.getName(), rootCause);

        displayException(rootCause);
    }

    protected void handleDomainException(DomainException e) {
        DomainError err = e.getError();
        String message = String.format(t.translate(err.messageCode()), e.getArguments());
        displayError(String.format("%s. (%s)", message, err.code()));
    }

    protected void handlePrinterException(PrinterException e) {
        displayError(t.translate(e.getMessageCode()));
    }

    protected void handlePinodeskApiException(PinodeskApiException e) {
        String code = e.getCode();
        String message = e.getMessage();
        if (e.getMessageCode() != null) {
            message = t.translate(e.getMessageCode());
        }
        displayError(String.format("%s %s", message, code == null ? "" : "(" + code + ")"));
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
        try {
            ButtonType btnTypeOk = new ButtonType(t.translate(CommonLabel.BTN_OK), ButtonData.OK_DONE);
            ButtonType btnTypeYes = new ButtonType(t.translate(CommonLabel.BTN_YES), ButtonData.YES);
            ButtonType btnTypeNo = new ButtonType(t.translate(CommonLabel.BTN_NO), ButtonData.NO);
            Alert alert = new Alert(type);
            alert.setTitle(CommonConstants.APP_TITLE);
            alert.setHeaderText(t.translate(getAlertHeaderMessageCode(type)));
            DialogPane dialogPane = alert.getDialogPane();
            StageUtils.setIcons((Stage) dialogPane.getScene().getWindow(), CommonConstants.APP_ICON_PATHS);
            Text text = new Text(message);
            text.setWrappingWidth(dialogPane.getWidth());
            text.setStyle("-fx-font-size: 13px");
            AnchorPane.setLeftAnchor(text, 2d);
            AnchorPane.setTopAnchor(text, 2d);
            AnchorPane contentPane = new AnchorPane(text);
            dialogPane.setContent(contentPane);
            dialogPane.getButtonTypes().clear();
            dialogPane.getStylesheets().add(getClass().getResource("/assets/css/pinodesk.css").toExternalForm());
            ImageView headerIcon = null;
            switch (type) {
                case INFORMATION:
                    headerIcon = new ImageView("/assets/images/dialog-icon-info.png");
                    dialogPane.getButtonTypes().add(btnTypeOk);
                    dialogPane.lookupButton(btnTypeOk).getStyleClass().add("btn-primary");
                    break;
                case ERROR:
                    headerIcon = new ImageView("/assets/images/dialog-icon-error.png");
                    dialogPane.getButtonTypes().add(btnTypeOk);
                    dialogPane.lookupButton(btnTypeOk).getStyleClass().add("btn-primary");
                    break;
                case CONFIRMATION:
                    headerIcon = new ImageView("/assets/images/dialog-icon-confirmation.png");
                    dialogPane.getButtonTypes().addAll(btnTypeYes, btnTypeNo);
                    dialogPane.lookupButton(btnTypeYes).getStyleClass().add("btn-primary");
                    dialogPane.lookupButton(btnTypeNo).getStyleClass().add("btn-secondary");
                    break;
                default:
                    break;
            }
            headerIcon.setFitHeight(48); // Set size to API recommendation.
            headerIcon.setFitWidth(48);
            dialogPane.setGraphic(headerIcon);
            dialogPane.applyCss();
            HBox buttonContainer = (HBox) dialogPane.lookup(".container");
            buttonContainer.setSpacing(1);
            buttonContainer.requestFocus();
            return new AlertResult(alert.showAndWait());
        } catch (Exception e) {
            throw new DefaultRuntimeException(e);
        }
    }

    protected AlertResult displayError(String message) {
        return displayAlert(AlertType.ERROR, message);
    }

    protected AlertResult displayError(IMessage messageCode) {
        return displayError(t.translate(messageCode.toString()));
    }

    protected AlertResult displayWarning(String message) {
        return displayAlert(AlertType.WARNING, message);
    }

    protected AlertResult displayWarning(IMessage messageCode) {
        return displayWarning(t.translate(messageCode.toString()));
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
        dialogPane.setContentText(t.translate(CommonLabel.LBL_SYSTEM_ERROR_DESCRIPTION));

        ImageView headerIcon = new ImageView("/assets/images/dialog-icon-error.png");
        headerIcon.setFitHeight(48); // Set size to API recommendation.
        headerIcon.setFitWidth(48);
        dialogPane.setGraphic(headerIcon);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        dialogPane.setExpandableContent(expContent);
        dialogPane.setExpanded(true);

        dialogPane.getButtonTypes().clear();
        dialogPane.getStylesheets().add(getClass().getResource("/assets/css/pinodesk.css").toExternalForm());

        ButtonType btnTypeOk = new ButtonType(t.translate(CommonLabel.BTN_OK));
        ButtonType btnTypeSendReport = new ButtonType(t.translate(CommonLabel.BTN_SEND_REPORT));

        dialogPane.getButtonTypes().addAll(btnTypeSendReport, btnTypeOk);
        dialogPane.lookupButton(btnTypeOk).getStyleClass().add("btn-primary");

        Button btnSendReport = (Button) dialogPane.lookupButton(btnTypeSendReport);
        btnSendReport.getStyleClass().add("btn-secondary");
        btnSendReport.addEventFilter(ActionEvent.ACTION, event -> {
            AlertResult confirmation = displayConfirmation(MessageCode.CONFIRMATION_SEND_ERROR_REPORT);
            if (confirmation.isNo()) {
                event.consume();
                return;
            }
            Stage loading = displayLoading();
            TaskUtils.runTask("Send error report", () -> {
                sendErrorReport(ex, exceptionText);
                Platform.runLater(() -> {
                    loading.hide();
                    displayInfo(MessageCode.SUCCESS_SEND_ERROR_REPORT);
                    alert.close();
                });
            }, throwable -> Platform.runLater(() -> {
                loading.hide();
                handleException(throwable);
            }));
        });

        dialogPane.applyCss();

        HBox buttonContainer = (HBox) dialogPane.lookup(".container");
        buttonContainer.setSpacing(1);
        buttonContainer.requestFocus();

        alert.showAndWait();
    }

    private void sendErrorReport(Throwable ex, String stacktrace) {
        String strActivationData = configurationService.getConfiguration(ConfigurationConstants.ACTIVATION_DATA);
        ActivateReleaseResponse activationData = JSON.parse(strActivationData, ActivateReleaseResponse.class);
        CreateIssueRequest req = new CreateIssueRequest();
        req.setCategory("bug_report");
        req.setSource("app");
        req.setTitle(ex.toString());
        req.setDescription("Please help to check the following error stacktrace from the user report:");
        req.setErrorStacktrace(stacktrace);
        req.setActivationDeviceId(activationData.getActivationDeviceId());
        req.setReleasePlatform(applicationProperties.getReleasePlatform());
        req.setReleaseVersion(applicationProperties.getAppVersion());
        req.setDeviceSignature(DeviceUtils.getDeviceSignature());
        req.setDeviceManufacturer(defaultNullUnknown(DeviceUtils.getDeviceManufacturer()));
        req.setDeviceModel(defaultNullUnknown(DeviceUtils.getDeviceModel()));
        req.setOsName(defaultNullUnknown(DeviceUtils.getOsName()));
        req.setOsVersion(defaultNullUnknown(DeviceUtils.getOsVersion()));
        req.setOsFamily(defaultNullUnknown(DeviceUtils.getOsFamily()));
        req.setOsArch(defaultNullUnknown(DeviceUtils.getOsArch()));
        req.setOsBitness(DeviceUtils.getOsBitness());
        req.setCpuName(defaultNullUnknown(DeviceUtils.getCpuName()));
        req.setCpuVendor(defaultNullUnknown(DeviceUtils.getCpuVendor()));
        req.setCpuFamily(defaultNullUnknown(DeviceUtils.getCpuFamily()));
        req.setRamSize(DeviceUtils.getRamSize());
        req.setRamSizeAvailable(DeviceUtils.getRamSizeAvailable());
        req.setStorageSize(DeviceUtils.getStorageSize());
        req.setStorageSizeAvailable(DeviceUtils.getStorageSizeAvailable());
        pinodeskApiService.createIssue(req);
    }

    protected String defaultNullUnknown(String val) {
        return Constants.UNKNOWN.equals(val) ? null : val;
    }

    protected Stage displayLoading() {
        return StageUtils.modal(Page.LOADING, StageStyle.UNDECORATED);
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
        setChooserOnFocus(tf, isFirstInput, Page.CATALOG_PRODUCT_CHOOSE_CATEGORY, outputConsumer, nextFocusNode);
    }

    protected <T> void setProductCategoryChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setProductCategoryChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setUnitChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.CATALOG_PRODUCT_CHOOSE_UNIT, outputConsumer, nextFocusNode);
    }

    protected <T> void setUnitChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setUnitChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setProductChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.CATALOG_PRODUCT_CHOOSE, outputConsumer, nextFocusNode);
    }

    protected <T> void setProductChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setProductChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setSupplierChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.CATALOG_SUPPLIER_CHOOSE, outputConsumer, nextFocusNode);
    }

    protected <T> void setSupplierChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setSupplierChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setCustomerChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.CATALOG_CUSTOMER_CHOOSE, outputConsumer, nextFocusNode);
    }

    protected <T> void setCustomerChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setCustomerChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setDoctorChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.CATALOG_DOCTOR_CHOOSE, outputConsumer, nextFocusNode);
    }

    protected <T> void setDoctorChooser(TextField tf, Consumer<T> outputConsumer, Node nextFocusNode) {
        setDoctorChooser(tf, false, outputConsumer, nextFocusNode);
    }

    protected <T> void setDoctorCategoryChooser(
            TextField tf,
            boolean isFirstInput,
            Consumer<T> outputConsumer,
            Node nextFocusNode) {
        setChooserOnFocus(tf, isFirstInput, Page.CATALOG_DOCTOR_CHOOSE_CATEGORY, outputConsumer, nextFocusNode);
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
                Page.CATALOG_PRODUCT_CHOOSE_DRUG_CLASSIFICATION,
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

    protected void disableWriteAction(String menuCode, Button... btns) {
        if (ArrayUtils.isEmpty(btns)) {
            return;
        }
        if (!sessionService.isCurrentSessionActive()) {
            for (Button btn : btns) {
                btn.setDisable(true);
            }
            return;
        }
        sessionService.getCurrentSession().getUserGroupMenus().forEach(ugm -> {
            if (ugm.getMenuCode().equals(menuCode) && !SimpleStatus.YES.toString().equals(ugm.getWrite())) {
                for (Button btn : btns) {
                    btn.setDisable(true);
                }
                return;
            }
        });
    }

    // Reference:
    // https://edencoding.com/how-to-hide-a-button-in-javafx/#set-visibility
    /**
     * Sets visibility of a node in its layout.
     * 
     * @param visible
     * @param node
     */
    protected void setVisibleInLayout(boolean visible, Node node) {
        Platform.runLater(() -> {
            node.setVisible(visible);
            node.setManaged(visible);
        });
    }

    protected boolean isNullOrZero(Number num) {
        return num == null || num.doubleValue() == 0;
    }

    protected boolean isPharmacyFeatureEnabled() {
        String enabled = configurationService.getConfiguration(ConfigurationConstants.PHARMACY_FEATURES_ENABLED);
        return SimpleStatus.YES.toString().equals(enabled);
    }

    protected void initCustomDatePicker(DatePicker... datePickers) {
        if (ArrayUtils.isEmpty(datePickers)) {
            return;
        }
        for (DatePicker dp : datePickers) {
            dp.getEditor().focusedProperty().addListener((o, ov, nv) -> {
                if (!nv) {
                    dp.hide();
                    return;
                }
                dp.show();
            });
            dp.setOnHidden(event -> {
                setFocused(dp.getParent());
            });
            dp.setConverter(new DefaultDatePickerConverter(CommonConstants.DATE_DISPLAY_PATTERN));
            AnchorPane parent = (AnchorPane) dp.getParent();
            Button btnClear = (Button) parent.lookup(".date-picker-clear-btn");
            btnClear.setOnAction(event -> {
                dp.setValue(null);
            });
        }
    }

}

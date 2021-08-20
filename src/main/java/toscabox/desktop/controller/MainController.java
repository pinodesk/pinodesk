package toscabox.desktop.controller;

import java.io.IOException;

import com.gitlab.muhammadkholidb.pandora.utility.PageLoader;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import toscabox.desktop.constant.ConfigurationConstants;
import toscabox.desktop.constant.Page;
import toscabox.desktop.constant.StyleConstants;
import toscabox.desktop.service.ConfigurationService;

public class MainController extends BaseController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox vboxMenu;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label lblStoreName;

    private ConfigurationService configurationService;

    @Override
    protected void initServices(ApplicationContext ctx) {
        configurationService = ctx.getBean(ConfigurationService.class);
    }

    @Override
    protected void initControlActions() {
        // No controls to initialize
    }

    @Override
    protected void initControlValues() {
        String storeName = configurationService.getConfiguration(ConfigurationConstants.STORE_NAME);
        lblStoreName.setText(storeName);
    }

    @Override
    protected Stage getCurrentStage() {
        return null;
    }

    @FXML
    void onActionBtnMenuProducts(ActionEvent event) {
        changeContent(Page.MASTER_PRODUCT_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuCustomers(ActionEvent event) {
        changeContent(Page.MASTER_CUSTOMER_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuSuppliers(ActionEvent event) {
        changeContent(Page.MASTER_SUPPLIER_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuPurchases(ActionEvent event) {
        changeContent(Page.TRANSACTION_PURCHASE_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuConfiguration(ActionEvent event) {
        changeContent(Page.SETTINGS_CONFIGURATION_MAIN, (Button) event.getSource());
    }

    private void changeContent(Page page, Button btn) {
        Platform.runLater(() -> {
            try {
                setActiveMenu(btn);
                swapContentPane(page);
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        });
    }

    private void setActiveMenu(Button btn) {
        vboxMenu.getChildren().forEach(node -> {
            node.getStyleClass().remove(StyleConstants.BTN_PRIMARY_ACTIVE);
            if (btn.equals(node)) {
                node.getStyleClass().add(StyleConstants.BTN_PRIMARY_ACTIVE);
            }
        });
    }

    private void swapContentPane(Page page) throws IOException {
        VBox content = (VBox) PageLoader.load(page).getRoot();
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        contentPane.getChildren().clear();
        contentPane.getChildren().add(content);
    }

}

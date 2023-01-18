package pospino.desktop.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.utility.AlertResult;
import com.gitlab.muhammadkholidb.pandora.utility.PageLoader;
import com.gitlab.muhammadkholidb.pandora.utility.StageUtils;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.MenuCodeConstants;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.Page;
import pospino.desktop.constant.SimpleStatus;
import pospino.desktop.constant.StyleConstants;
import pospino.desktop.properties.ApplicationProperties;
import pospino.desktop.service.ConfigurationService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.CurrentSessionVM;
import pospino.desktop.viewmodel.UserGroupMenuVM;

public class MainController extends BaseController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox vboxMenu;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label lblStoreName;

    @FXML
    private Label lblUser;

    @FXML
    private Label lblUserGroup;

    @FXML
    private Label lblMenuMaster;

    @FXML
    private Button btnMenuProducts;

    @FXML
    private Button btnMenuCustomers;

    @FXML
    private Button btnMenuSuppliers;

    @FXML
    private Button btnMenuDoctors;

    @FXML
    private Label lblMenuTransaction;

    @FXML
    private Button btnMenuPurchases;

    @FXML
    private Button btnMenuSales;

    @FXML
    private Button btnMenuPayables;

    @FXML
    private Button btnMenuReceivables;

    @FXML
    private Label lblMenuSettings;

    @FXML
    private Button btnMenuConfiguration;

    @FXML
    private Button btnMenuUsers;

    @FXML
    private Button btnMenuUserGroups;

    @FXML
    private Button btnLogout;

    @FXML
    private Label lblVersion;

    private ConfigurationService configurationService;
    private ApplicationProperties applicationProperties;

    @Override
    protected void initServices(ApplicationContext ctx) {
        configurationService = SpringUtils.getBean(ConfigurationService.class);
        applicationProperties = SpringUtils.getBean(ApplicationProperties.class);
    }

    @Override
    protected void initControlActions() {
        // No controls to initialize
        // print();
    }

    @Override
    protected void initControlValues() {
        if (sessionService.isEmpty()) {
            ObservableList<Node> children = vboxMenu.getChildren();
            for (Node node : children) {
                vboxMenu.getChildren().remove(node);
            }
            return;
        }
        lblVersion.setText(String.format("%s %s", CommonConstants.APP_TITLE, applicationProperties.getVersion()));
        CurrentSessionVM currentSession = sessionService.get().getCurrentSession();
        String storeName = configurationService.getConfiguration(ConfigurationConstants.STORE_NAME);
        lblStoreName.setText(storeName);
        lblUser.setText(currentSession.getUser().getFullName());
        lblUserGroup.setText(currentSession.getUserGroup().getName());
        List<UserGroupMenuVM> userGroupMenus = currentSession.getUserGroupMenus();
        List<String> userGroupMenuCodes = userGroupMenus.stream()
                .filter(ugm -> SimpleStatus.YES.toString().equals(ugm.getRead())).map(UserGroupMenuVM::getMenuCode)
                .toList();
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.MASTER, lblMenuMaster);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.MASTER_PRODUCTS, btnMenuProducts);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.MASTER_CUSTOMERS, btnMenuCustomers);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.MASTER_SUPPLIERS, btnMenuSuppliers);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.MASTER_DOCTORS, btnMenuDoctors);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.TRANSACTION, lblMenuTransaction);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.TRANSACTION_PURCHASES, btnMenuPurchases);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.TRANSACTION_SALES, btnMenuSales);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.TRANSACTION_PAYABLES, btnMenuPayables);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.TRANSACTION_RECEIVABLES, btnMenuReceivables);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.SETTINGS, lblMenuSettings);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.SETTINGS_CONFIGURATION, btnMenuConfiguration);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.SETTINGS_USERS, btnMenuUsers);
        removeInaccessibleMenu(userGroupMenuCodes, MenuCodeConstants.SETTINGS_USER_GROUPS, btnMenuUserGroups);
    }

    @Override
    protected Stage getCurrentStage() {
        return (Stage) rootPane.getScene().getWindow();
    }

    @FXML
    void onActionBtnLogout(ActionEvent event) {
        if (sessionService.isEmpty()) {
            close();
            return;
        }
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_LOGOUT);
        if (result.isConfirmed()) {
            sessionService.get().logout();
            close();
            StageUtils.open(Page.LOGIN, false);
        }
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
    void onActionBtnMenuDoctors(ActionEvent event) {
        changeContent(Page.MASTER_DOCTOR_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuPurchases(ActionEvent event) {
        changeContent(Page.TRANSACTION_PURCHASE_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuSales(ActionEvent event) {
        changeContent(Page.TRANSACTION_SALE_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuPayables(ActionEvent event) {
        changeContent(Page.TRANSACTION_PAYABLE_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuReceivables(ActionEvent event) {
        changeContent(Page.TRANSACTION_RECEIVABLE_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuConfiguration(ActionEvent event) {
        changeContent(Page.SETTINGS_CONFIGURATION_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuUserGroups(ActionEvent event) {
        changeContent(Page.SETTINGS_USER_GROUP_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuUsers(ActionEvent event) {
        changeContent(Page.SETTINGS_USER_MAIN, (Button) event.getSource());
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

    private void removeInaccessibleMenu(List<String> userGroupMenuCodes, String menuCode, Node node) {
        if (!userGroupMenuCodes.contains(menuCode)) {
            vboxMenu.getChildren().remove(node);
        }
    }

}

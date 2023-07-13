package pospino.desktop.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gitlab.mudiasoft.pandora.utility.AlertResult;
import com.gitlab.mudiasoft.pandora.utility.PageLoader;
import com.gitlab.mudiasoft.pandora.utility.ScrollPaneUtils;
import com.gitlab.mudiasoft.pandora.utility.StageUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import pospino.desktop.viewmodel.CurrentSessionVM;
import pospino.desktop.viewmodel.SaleReportFilterVM;
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
    private Button btnMenuDashboard;

    @FXML
    private Label lblMenuCatalog;

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

    @FXML
    private Label lblHelloName;

    @FXML
    private ScrollPane menuScrollPane;

    @FXML
    private Label lblMenuReports;

    @FXML
    private Button btnMenuSalesReport;

    @FXML
    private Button btnMenuPurchasesReport;

    @Override
    protected void initServices() {
        // No services to init
    }

    @Override
    protected void initControlActions() {
        // No controls to init
    }

    @Override
    protected void initControlValues() {
        if (!sessionService.isCurrentSessionActive()) {
            vboxMenu.getChildren().removeAll(vboxMenu.getChildren());
            return;
        }
        lblVersion.setText(String.format("%s %s", CommonConstants.APP_TITLE, applicationProperties.getVersion()));
        CurrentSessionVM currentSession = sessionService.getCurrentSession();
        Map<String, String> configurationMap = configurationService.getConfigurationMap();
        lblStoreName.setText(configurationMap.get(ConfigurationConstants.STORE_NAME));
        lblUser.setText(currentSession.getUser().getFullName());
        lblUserGroup.setText(currentSession.getUserGroup().getName());
        lblHelloName.setText(currentSession.getUser().getFullName());
        List<UserGroupMenuVM> userGroupMenus = currentSession.getUserGroupMenus();
        List<String> userGroupMenuCodes = userGroupMenus.stream()
                .filter(ugm -> SimpleStatus.YES.toString().equals(ugm.getRead())).map(UserGroupMenuVM::getMenuCode)
                .toList();
        Set<Node> inaccessibleMenus = new HashSet<>();
        appendInaccessibleMenus(inaccessibleMenus, userGroupMenuCodes, MenuCodeConstants.DASHBOARD, btnMenuDashboard);
        appendInaccessibleMenus(inaccessibleMenus, userGroupMenuCodes, MenuCodeConstants.CATALOG, lblMenuCatalog);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.CATALOG_PRODUCTS,
                btnMenuProducts);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.CATALOG_CUSTOMERS,
                btnMenuCustomers);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.CATALOG_SUPPLIERS,
                btnMenuSuppliers);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.CATALOG_DOCTORS,
                btnMenuDoctors);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.TRANSACTION,
                lblMenuTransaction);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.TRANSACTION_PURCHASES,
                btnMenuPurchases);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.TRANSACTION_SALES,
                btnMenuSales);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.TRANSACTION_PAYABLES,
                btnMenuPayables);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.TRANSACTION_RECEIVABLES,
                btnMenuReceivables);
        appendInaccessibleMenus(inaccessibleMenus, userGroupMenuCodes, MenuCodeConstants.SETTINGS, lblMenuSettings);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.SETTINGS_CONFIGURATION,
                btnMenuConfiguration);
        appendInaccessibleMenus(inaccessibleMenus, userGroupMenuCodes, MenuCodeConstants.SETTINGS_USERS, btnMenuUsers);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.SETTINGS_USER_GROUPS,
                btnMenuUserGroups);
        if (!isPharmacyFeatureEnabled() && btnMenuDoctors.isVisible()) {
            inaccessibleMenus.add(btnMenuDoctors);
        }
        vboxMenu.getChildren().removeAll(inaccessibleMenus);
        Platform.runLater(() -> {
            ScrollPaneUtils.fixBlur(menuScrollPane);
        });
    }

    @Override
    protected Stage getCurrentStage() {
        return (Stage) rootPane.getScene().getWindow();
    }

    @FXML
    void onActionBtnLogout(ActionEvent event) {
        if (!sessionService.isCurrentSessionActive()) {
            close();
            return;
        }
        AlertResult result = displayConfirmation(MessageCode.CONFIRMATION_LOGOUT);
        if (result.isConfirmed()) {
            sessionService.logout();
            close();
            StageUtils.open(Page.LOGIN, false);
        }
    }

    @FXML
    void onActionBtnMenuDashboard(ActionEvent event) {
        changeContent(Page.DASHBOARD, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuProducts(ActionEvent event) {
        changeContent(Page.CATALOG_PRODUCT_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuCustomers(ActionEvent event) {
        changeContent(Page.CATALOG_CUSTOMER_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuSuppliers(ActionEvent event) {
        changeContent(Page.CATALOG_SUPPLIER_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuDoctors(ActionEvent event) {
        changeContent(Page.CATALOG_DOCTOR_MAIN, (Button) event.getSource());
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

    @FXML
    void onActionBtnMenuSalesReport(ActionEvent event) {
        LocalDate today = LocalDate.now();
        SaleReportFilterVM filter = new SaleReportFilterVM();
        filter.setInvoiceDateMax(today);
        filter.setInvoiceDateMin(today);
        setPageData(filter);
        changeContent(Page.REPORT_SALE_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuPurchasesReport(ActionEvent event) {
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

    private void appendInaccessibleMenus(
            Set<Node> inaccessibleNodes,
            List<String> userGroupMenuCodes,
            String menuCode,
            Node node) {
        if (!userGroupMenuCodes.contains(menuCode)) {
            inaccessibleNodes.add(node);
        }
    }

}

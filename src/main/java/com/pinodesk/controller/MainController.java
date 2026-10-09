package com.pinodesk.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.constant.Page;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.constant.StyleConstants;
import com.pinodesk.pandora.utility.AlertResult;
import com.pinodesk.pandora.utility.PageLoader;
import com.pinodesk.pandora.utility.ScrollPaneUtils;
import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.util.AccountPanelSupport;
import com.pinodesk.viewmodel.CurrentSessionVM;
import com.pinodesk.viewmodel.PurchaseReportFilterVM;
import com.pinodesk.viewmodel.SaleReportFilterVM;
import com.pinodesk.viewmodel.UserGroupMenuVM;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainController extends BaseController {

    private boolean sidebarCollapsed;

    @FXML
    private Button btnToggleSidebar;

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
    private Button btnMenuConsignments;

    @FXML
    private Label lblMenuSettings;

    @FXML
    private Button btnMenuConfiguration;

    @FXML
    private Button btnMenuUsers;

    @FXML
    private Button btnMenuPaymentMethods;

    @FXML
    private Button btnMenuExpenseCategories;

    @FXML
    private Button btnMenuUserGroups;

    @FXML
    private Button btnLogout;

    @FXML
    private ToggleButton profileMenu;

    @FXML
    private VBox accountPanel;

    @FXML
    private VBox sidebarPane;

    @FXML
    private Label lblVersion;

    @FXML
    private Label lblHelloName;

    @FXML
    private ScrollPane menuScrollPane;

    @FXML
    private Label lblMenuReport;

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
        vboxMenu.getChildren().stream().filter(Button.class::isInstance).map(Button.class::cast).forEach(button -> {
            button.setTooltip(new Tooltip(button.getText()));
            button.setAccessibleText(button.getText());
        });
        AccountPanelSupport.install(rootPane, profileMenu, accountPanel, lblUser, btnLogout);
        updateSidebar();
    }

    @FXML
    void onActionBtnToggleSidebar(ActionEvent event) {
        sidebarCollapsed = !sidebarCollapsed;
        updateSidebar();
    }

    private void updateSidebar() {
        double width = sidebarCollapsed ? 76 : 250;
        sidebarPane.setPrefWidth(width);
        AnchorPane.setLeftAnchor(contentPane, width);
        sidebarPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("collapsed"), sidebarCollapsed);
        menuScrollPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("collapsed"), sidebarCollapsed);
        // Only update remaining nodes; permission-filtered menus must never be
        // restored.
        vboxMenu.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                button.setContentDisplay(sidebarCollapsed ? ContentDisplay.GRAPHIC_ONLY : ContentDisplay.LEFT);
            } else if (node instanceof Label) {
                node.setVisible(!sidebarCollapsed);
                node.setManaged(!sidebarCollapsed);
            }
        });
        String label = resources.getString(sidebarCollapsed ? "btn_expand_sidebar" : "btn_collapse_sidebar");
        btnToggleSidebar.setText(label);
        btnToggleSidebar.setContentDisplay(sidebarCollapsed ? ContentDisplay.GRAPHIC_ONLY : ContentDisplay.LEFT);
        btnToggleSidebar.setAccessibleText(label);
        btnToggleSidebar.setTooltip(new Tooltip(label));
    }

    @Override
    protected void initControlValues() {
        if (!sessionService.isCurrentSessionActive()) {
            vboxMenu.getChildren().removeAll(vboxMenu.getChildren());
            return;
        }
        initVersionAndUserInfo();
        initMenuAccess();
        Platform.runLater(() -> ScrollPaneUtils.fixBlur(menuScrollPane));
    }

    private void initVersionAndUserInfo() {
        lblVersion.setText(String.format("%s %s", CommonConstants.APP_TITLE, applicationProperties.getAppVersion()));
        CurrentSessionVM currentSession = sessionService.getCurrentSession();
        Map<String, String> configurationMap = configurationService.getConfigurationMap();
        lblStoreName.setText(configurationMap.get(ConfigurationConstants.STORE_NAME));
        lblUser.setText(currentSession.getUser().getFullName());
        lblUserGroup.setText(currentSession.getUserGroup().getName());
        lblHelloName.setText(currentSession.getUser().getFullName());
    }

    private void initMenuAccess() {
        CurrentSessionVM currentSession = sessionService.getCurrentSession();
        List<String> userGroupMenuCodes = currentSession.getUserGroupMenus().stream()
                .filter(ugm -> SimpleStatus.YES.toString().equals(ugm.getRead())).map(UserGroupMenuVM::getMenuCode)
                .toList();

        Set<Node> inaccessibleMenus = new HashSet<>();
        initCatalogMenus(inaccessibleMenus, userGroupMenuCodes);
        initTransactionMenus(inaccessibleMenus, userGroupMenuCodes);
        initSettingsMenus(inaccessibleMenus, userGroupMenuCodes);
        initReportMenus(inaccessibleMenus, userGroupMenuCodes);

        if (!isPharmacyFeatureEnabled() && btnMenuDoctors.isVisible()) {
            inaccessibleMenus.add(btnMenuDoctors);
        }
        vboxMenu.getChildren().removeAll(inaccessibleMenus);
    }

    private void initCatalogMenus(Set<Node> inaccessibleMenus, List<String> userGroupMenuCodes) {
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
    }

    private void initTransactionMenus(Set<Node> inaccessibleMenus, List<String> userGroupMenuCodes) {
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
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.TRANSACTION_CONSIGNMENTS,
                btnMenuConsignments);
    }

    private void initSettingsMenus(Set<Node> inaccessibleMenus, List<String> userGroupMenuCodes) {
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.SETTINGS_PAYMENT_METHODS,
                btnMenuPaymentMethods);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.SETTINGS_EXPENSE_CATEGORIES,
                btnMenuExpenseCategories);
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
    }

    private void initReportMenus(Set<Node> inaccessibleMenus, List<String> userGroupMenuCodes) {
        appendInaccessibleMenus(inaccessibleMenus, userGroupMenuCodes, MenuCodeConstants.REPORT, lblMenuReport);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.REPORT_PURCHASES,
                btnMenuPurchasesReport);
        appendInaccessibleMenus(
                inaccessibleMenus,
                userGroupMenuCodes,
                MenuCodeConstants.REPORT_SALES,
                btnMenuSalesReport);
    }

    @Override
    protected Stage getCurrentStage() {
        return (Stage) rootPane.getScene().getWindow();
    }

    @FXML
    void onActionBtnLogout(ActionEvent event) {
        profileMenu.setSelected(false);
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
    void onActionBtnMenuConsignments(ActionEvent event) {
        changeContent(Page.TRANSACTION_CONSIGNMENT_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuConfiguration(ActionEvent event) {
        changeContent(Page.SETTINGS_CONFIGURATION_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuPaymentMethods(ActionEvent event) {
        changeContent(Page.SETTINGS_PAYMENT_METHOD_MAIN, (Button) event.getSource());
    }

    @FXML
    void onActionBtnMenuExpenseCategories(ActionEvent event) {
        changeContent(Page.SETTINGS_EXPENSE_CATEGORY_MAIN, (Button) event.getSource());
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
        LocalDate today = LocalDate.now();
        PurchaseReportFilterVM filter = new PurchaseReportFilterVM();
        filter.setInvoiceDateMax(today);
        filter.setInvoiceDateMin(today);
        setPageData(filter);
        changeContent(Page.REPORT_PURCHASE_MAIN, (Button) event.getSource());
    }

    private void changeContent(Page page, Button btn) {
        Platform.runLater(() -> {
            try {
                setActiveMenu(btn);
                swapContentPane(page);
            } catch (Exception e) {
                log.error("Error on change content", e);
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

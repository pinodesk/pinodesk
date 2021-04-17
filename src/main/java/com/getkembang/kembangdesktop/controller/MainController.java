package com.getkembang.kembangdesktop.controller;

import java.io.IOException;

import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.constant.StyleConstants;
import com.gitlab.muhammadkholidb.pandora.utility.PageLoader;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController extends BaseController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox vboxMenu;

    @FXML
    private AnchorPane contentPane;

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services to initialize
    }

    @Override
    protected void initControlActions() {
        // No controls to initialize
    }

    @Override
    protected void initControlValues() {
        // No fields values to initialize
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MAIN;
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
        VBox content = PageLoader.load(page);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        contentPane.getChildren().clear();
        contentPane.getChildren().add(content);
    }

}

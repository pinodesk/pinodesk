package com.gitlab.muhammadkholidb.bianglala.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.Page;
import com.gitlab.muhammadkholidb.bianglala.utility.FXUtils;
import com.gitlab.muhammadkholidb.bianglala.utility.PageLoader;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox vboxMenu;

    @FXML
    private AnchorPane contentPane;

    @FXML
    void initialize() {
        log.debug("Initialize");
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

    private void changeContent(Page page, Button btn) {
        Platform.runLater(() -> {
            try {
                setActiveMenu(btn);
                swapContentPane(page);
            } catch (Exception e) {
                FXUtils.showErrorDialog(e);
            }
        });
    }

    private void setActiveMenu(Button btn) {
        vboxMenu.getChildren().forEach(node -> {
            String styleClass = "btn-primary-active";
            node.getStyleClass().remove(styleClass);
            if (btn.equals(node)) {
                node.getStyleClass().add(styleClass);
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

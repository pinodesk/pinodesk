package com.gitlab.muhammadkholidb.bianglala.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.ViewConstants;
import com.gitlab.muhammadkholidb.bianglala.utility.ViewLoader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox vboxMenu;

    @FXML
    private AnchorPane contentPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initialize");
    }

    @FXML
    private void onActionBtnMenuProducts(ActionEvent event) throws IOException {
        loadViewToContentPane(ViewConstants.MASTER_PRODUCTS);
        setActiveMenu((Button) event.getSource());
    }

    @FXML
    private void onActionBtnMenuCustomers(ActionEvent event) throws IOException {
        loadViewToContentPane(ViewConstants.MASTER_CUSTOMERS);
        setActiveMenu((Button) event.getSource());
    }

    @FXML
    private void onActionBtnMenuSuppliers(ActionEvent event) throws IOException {
        loadViewToContentPane(ViewConstants.MASTER_SUPPLIERS);
        setActiveMenu((Button) event.getSource());
    }

    private void loadViewToContentPane(String name) throws IOException {
        VBox content = (VBox) ViewLoader.load(name, CommonConstants.BAHASA);
        AnchorPane.setTopAnchor(content, 0d);
        AnchorPane.setBottomAnchor(content, 0d);
        AnchorPane.setLeftAnchor(content, 0d);
        AnchorPane.setRightAnchor(content, 0d);
        contentPane.getChildren().clear();
        contentPane.getChildren().add(content);
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

}

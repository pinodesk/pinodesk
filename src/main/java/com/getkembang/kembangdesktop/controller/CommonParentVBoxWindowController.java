package com.getkembang.kembangdesktop.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class CommonParentVBoxWindowController extends BaseController {
    
    @FXML
    protected VBox contentPane;

    @Override
    protected Stage getCurrentStage() {
        return (Stage) contentPane.getScene().getWindow();
    }

}

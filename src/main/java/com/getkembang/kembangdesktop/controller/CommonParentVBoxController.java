package com.getkembang.kembangdesktop.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class CommonParentVBoxController extends BaseController {

    @FXML
    protected VBox contentPane;

    private List<EventHandler<KeyEvent>> contentPaneKeyPressedHandlers = new ArrayList<>();

    @Override
    protected Stage getCurrentStage() {
        return (Stage) contentPane.getScene().getWindow();
    }

    @Override
    protected void initControlActions() {
        initParentVBoxControlActions();
        contentPane.setOnKeyPressed(event -> contentPaneKeyPressedHandlers.forEach(handler -> handler.handle(event)));
    }

    protected void addContentPaneOnKeyPressedHandler(EventHandler<KeyEvent> handler) {
        contentPaneKeyPressedHandlers.add(handler);
    }

    protected abstract void initParentVBoxControlActions();

}

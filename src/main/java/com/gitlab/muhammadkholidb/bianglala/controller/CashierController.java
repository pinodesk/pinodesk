package com.gitlab.muhammadkholidb.bianglala.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class CashierController {

    @FXML
    private void onMouseClicked(MouseEvent event) {
        System.out.println("Mouse clicked on button: " + event.getSource());
    }

}

package com.gitlab.muhammadkholidb.bianglala.javafx.formatter;

import javafx.scene.control.TextFormatter;

public class DigitFormatter extends TextFormatter<String> {

    public DigitFormatter() {
        super(change -> change.getText().matches("\\d*") ? change : null);
    }

}

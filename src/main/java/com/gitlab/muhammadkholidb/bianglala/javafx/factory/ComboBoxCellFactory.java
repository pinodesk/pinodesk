package com.gitlab.muhammadkholidb.bianglala.javafx.factory;

import com.gitlab.muhammadkholidb.bianglala.viewmodel.ComboBoxBaseVM;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ComboBoxCellFactory<T extends ComboBoxBaseVM> implements Callback<ListView<T>, ListCell<T>> {

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else {
                    super.setText(item.getDisplayText());
                }
            }
        };
    }

}

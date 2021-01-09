package com.gitlab.muhammadkholidb.bianglala.javafx.factory;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class DateCellFactory<E, T extends Date> implements Callback<TableColumn<E, T>, TableCell<E, T>> {

    private String pattern;

    public DateCellFactory(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public TableCell<E, T> call(TableColumn<E, T> param) {

        return new TableCell<E, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem(item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else {
                    super.setText(DateFormatUtils.format(item, pattern));
                }
            }
        };
    }

}

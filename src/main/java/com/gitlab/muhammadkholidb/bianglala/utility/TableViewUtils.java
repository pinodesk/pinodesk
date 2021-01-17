package com.gitlab.muhammadkholidb.bianglala.utility;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TableViewUtils {

    private TableViewUtils() {
    }

    public static <M, T> void setColumnValue(TableColumn<M, T> col, Function<M, T> valueFunction) {
        col.setCellValueFactory(data -> new SimpleObjectProperty<>(valueFunction.apply(data.getValue())));
    }

    public static <M, T> void setColumnStyle(TableColumn<M, T> col, String style) {
        col.setStyle(style);
    }

    public static <M, T> void initTableColumn(TableColumn<M, T> col,
            Callback<TableColumn<M, T>, TableCell<M, T>> cellFactory, Function<M, T> valueFunction, String style) {
        if (cellFactory != null) {
            col.setCellFactory(cellFactory);
        }
        if (valueFunction != null) {
            setColumnValue(col, valueFunction);
        }
        if (StringUtils.isNotBlank(style)) {
            setColumnStyle(col, style);
        }
    }

    public static <M, T> void initTableColumn(TableColumn<M, T> col, Callback<TableColumn<M, T>, TableCell<M, T>> cellFactory, Function<M, T> valueFunction) {
        initTableColumn(col, cellFactory, valueFunction, null);
    }

    public static <M, T> void initTableColumn(TableColumn<M, T> col, Function<M, T> valueFunction, String style) {
        initTableColumn(col, null, valueFunction, style);
    }

}

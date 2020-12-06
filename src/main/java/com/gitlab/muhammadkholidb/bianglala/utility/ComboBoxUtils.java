package com.gitlab.muhammadkholidb.bianglala.utility;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.function.Supplier;

import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class ComboBoxUtils {
    
    private ComboBoxUtils() {}

    public static <T> void allowSpaceOnEditor(ComboBox<T> cb) {
        notNull(cb);
        ComboBoxListViewSkin<T> comboBoxListViewSkin = new ComboBoxListViewSkin<>(cb);
        comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                event.consume();
            }
        });
        cb.setSkin(comboBoxListViewSkin);
    }

    public static <T> void initEditable(ComboBox<T> cb, EventHandler<KeyEvent> keyEvent, StringConverter<T> converter, Supplier<T> selectedSupplier) {
        notNull(cb);
        if (!cb.isEditable()) {
            return;
        }
        allowSpaceOnEditor(cb);
        cb.getEditor().setOnKeyReleased(keyEvent);
        cb.setConverter(converter);
        select(cb, selectedSupplier.get());
    }

    public static <T> void initEditable(ComboBox<T> cb, EventHandler<KeyEvent> keyEvent, StringConverter<T> converter) {
        initEditable(cb, keyEvent, converter, () -> null);
    }

    public static <T> void select(ComboBox<T> cb, T item) {
        cb.getSelectionModel().select(item);
    }

    public static <T> void select(ComboBox<T> cb, Supplier<T> itemSupplier) {
        select(cb, itemSupplier.get());
    }

}

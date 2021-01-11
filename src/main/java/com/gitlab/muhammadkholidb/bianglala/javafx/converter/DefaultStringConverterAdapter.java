package com.gitlab.muhammadkholidb.bianglala.javafx.converter;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public abstract class DefaultStringConverterAdapter<T> extends StringConverter<T> {

    protected ComboBox<T> cb;

    protected DefaultStringConverterAdapter(ComboBox<T> cb) {
        this.cb = cb;
    }

    protected abstract String getDisplayText(T t);

    @Override
    public String toString(T t) {
        return t == null ? null : getDisplayText(t);
    }

    @Override
    public T fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return cb.getItems().stream().filter(t -> getDisplayText(t).equals(string)).findAny().orElse(null);
    }

}

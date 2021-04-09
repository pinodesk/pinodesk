package com.getkembang.kembangdesktop.javafx.factory;

import javafx.scene.control.TableCell;

public abstract class CommonTableCell<S,T> extends TableCell<S,T> {
    
    @Override
    protected void updateItem(T item, boolean empty) {
        setWrapText(true);
        realUpdateItem(item, empty);
    }

    protected abstract void realUpdateItem(T item, boolean empty);

}

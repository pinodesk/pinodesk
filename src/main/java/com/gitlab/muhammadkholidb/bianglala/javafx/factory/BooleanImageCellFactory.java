package com.gitlab.muhammadkholidb.bianglala.javafx.factory;

import java.util.function.Function;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class BooleanImageCellFactory<E, T> implements Callback<TableColumn<E, T>, TableCell<E, T>> {

    private Function<Object, Boolean> booleanFunc;

    public BooleanImageCellFactory(Function<Object, Boolean> booleanFunc) {
        this.booleanFunc = booleanFunc;
    }

    @Override
    public TableCell<E, T> call(TableColumn<E, T> param) {
        final ImageView imageview = new ImageView();
        imageview.setFitHeight(15);
        imageview.setFitWidth(15);
        return new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                if (Boolean.TRUE.equals(booleanFunc.apply(item))) {
                    imageview.setImage(new Image(getClass().getResourceAsStream("/assets/images/success-48.png")));
                } else {
                    imageview.setImage(new Image(getClass().getResourceAsStream("/assets/images/error-48.png")));
                }
                super.setGraphic(imageview);
            }
        };
    }

}

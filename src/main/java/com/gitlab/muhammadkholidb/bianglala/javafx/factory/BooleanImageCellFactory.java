package com.gitlab.muhammadkholidb.bianglala.javafx.factory;

import java.util.function.Function;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class BooleanImageCellFactory<E, T> implements Callback<TableColumn<E, T>, TableCell<E, T>> {

    private final Image imgSuccess = new Image(getClass().getResourceAsStream("/assets/images/success-48.png"));
    private final Image imgError = new Image(getClass().getResourceAsStream("/assets/images/error-48.png"));

    private Function<Object, Boolean> booleanFunc;

    public BooleanImageCellFactory(Function<Object, Boolean> booleanFunc) {
        this.booleanFunc = booleanFunc;
    }

    @Override
    public TableCell<E, T> call(TableColumn<E, T> param) {
        final ImageView imageview = new ImageView();
        imageview.setFitHeight(20);
        imageview.setFitWidth(20);
        return new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                if (empty) {
                    super.setGraphic(null);
                    return;
                }
                if (Boolean.TRUE.equals(booleanFunc.apply(item))) {
                    imageview.setImage(imgSuccess);
                } else {
                    imageview.setImage(imgError);
                }
                super.setGraphic(imageview);
            }
        };
    }

}

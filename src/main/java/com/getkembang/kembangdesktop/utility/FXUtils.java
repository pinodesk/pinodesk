package com.getkembang.kembangdesktop.utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import com.getkembang.kembangdesktop.Kembang;
import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.javafx.formatter.DigitFormatter;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FXUtils {

    private FXUtils() {
    }

    public static void show(Page page, boolean resizeable, Consumer<WindowEvent> onClose) throws IOException {
        Pane container = PageLoader.load(page);
        Scene scene = new Scene(container);
        Stage stage = new Stage();
        setDefaultIcons(stage);
        stage.setResizable(resizeable);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(CommonConstants.APP_TITLE);
        stage.setScene(scene);
        if (onClose != null) {
            stage.setOnHidden(onClose::accept);
        }
        stage.show();
    }

    public static void show(Page page, boolean resizeable) throws IOException {
        show(page, resizeable, null);
    }

    public static void show(Page page, Consumer<WindowEvent> onClose) throws IOException {
        show(page, true, onClose);
    }

    public static void show(Page page) throws IOException {
        show(page, null);
    }

    public static void setDefaultIcons(Stage stage) {
        Arrays.stream(Kembang.ICON_PATHS)
                .forEach(path -> stage.getIcons().add(new Image(FXUtils.class.getResourceAsStream(path))));
    }

    public static void setDigitFormatter(TextField... controls) {
        Arrays.asList(controls).forEach(tf -> tf.setTextFormatter(new DigitFormatter()));
    }

}

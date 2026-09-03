package com.pinodesk.javafx.listener;

import com.pinodesk.service.UnitService;
import com.pinodesk.toolbox.future.AsyncUtils;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.UnitVM;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;

public class UnitComboBoxKeyEventHandler implements EventHandler<KeyEvent> {

    private final ComboBox<UnitVM> cb;
    private final UnitService unitService;

    public UnitComboBoxKeyEventHandler(ComboBox<UnitVM> cb) {
        this.cb = cb;
        this.unitService = SpringUtils.getBean(UnitService.class);
    }

    @Override
    public void handle(KeyEvent event) {
        String value = cb.getEditor().getText();
        UnitVM selected = cb.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getName().equals(value)) {
            return;
        }
        cb.hide();
        if (StringUtils.isNotBlank(value) && value.length() >= 1) {
            AsyncUtils.supply(() -> unitService.searchUnitByKeyword(value)).thenAccept(list -> {
                if (!list.isEmpty()) {
                    Platform.runLater(() -> {
                        cb.setItems(FXCollections.observableList(list));
                        cb.setVisibleRowCount(list.size() > 10 ? 10 : list.size());
                        cb.show();
                    });
                }
            });
        }
    }

}

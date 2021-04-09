package com.getkembang.kembangdesktop.javafx.listener;

import com.getkembang.kembangdesktop.service.UnitService;
import com.getkembang.kembangdesktop.utility.ApplicationContextHolder;
import com.getkembang.kembangdesktop.utility.Async;
import com.getkembang.kembangdesktop.viewmodel.UnitVM;

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
        this.unitService = ApplicationContextHolder.getApplicationContext().getBean(UnitService.class);
    }

    @Override
    public void handle(KeyEvent event) {
        String value = cb.getEditor().getText();
        UnitVM selected = cb.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getName().equals(value)) {
            return;
        }
        cb.hide();
        if (StringUtils.isNotBlank(value) && value.length() >= 3) {
            Async.supply(() -> unitService.searchUnitByKeyword(value)).thenAccept(list -> {
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

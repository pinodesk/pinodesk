package com.gitlab.muhammadkholidb.bianglala.listener;

import com.gitlab.muhammadkholidb.bianglala.service.UnitService;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;
import com.gitlab.muhammadkholidb.bianglala.utility.Async;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.UnitSearchResult;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;

public class UnitComboBoxKeyEventHandler implements EventHandler<KeyEvent> {

    private final ComboBox<UnitSearchResult> cb;
    private final UnitService unitService;

    public UnitComboBoxKeyEventHandler(ComboBox<UnitSearchResult> cb) {
        this.cb = cb;
        this.unitService = ApplicationContextHolder.getApplicationContext().getBean(UnitService.class);
    }

    @Override
    public void handle(KeyEvent event) {
        String value = cb.getEditor().getText();
        UnitSearchResult selected = cb.getSelectionModel().getSelectedItem();
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

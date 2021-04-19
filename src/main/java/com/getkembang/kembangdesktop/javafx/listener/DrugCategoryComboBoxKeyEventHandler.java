package com.getkembang.kembangdesktop.javafx.listener;

import com.getkembang.kembangdesktop.service.DrugCategoryService;
import com.getkembang.kembangdesktop.utility.ApplicationContextHolder;
import com.getkembang.kembangdesktop.viewmodel.DrugCategoryVM;
import com.gitlab.muhammadkholidb.gearbox.future.Async;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;

public class DrugCategoryComboBoxKeyEventHandler implements EventHandler<KeyEvent> {

    private final ComboBox<DrugCategoryVM> cb;
    private final DrugCategoryService drugCategoryService;

    public DrugCategoryComboBoxKeyEventHandler(ComboBox<DrugCategoryVM> cb) {
        this.cb = cb;
        this.drugCategoryService = ApplicationContextHolder.getApplicationContext().getBean(DrugCategoryService.class);
    }

    @Override
    public void handle(KeyEvent event) {
        String value = cb.getEditor().getText();
        DrugCategoryVM selected = cb.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getName().equals(value)) {
            return;
        }
        cb.hide();
        if (StringUtils.isNotBlank(value) && value.length() >= 3) {
            Async.supply(() -> drugCategoryService.searchDrugCategoriesByKeyword(value)).thenAccept(list -> {
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

package pinus.desktop.javafx.listener;

import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import pinus.desktop.service.RackService;
import pinus.desktop.utility.SpringUtils;
import pinus.desktop.viewmodel.RackVM;

public class RackComboBoxKeyEventHandler implements EventHandler<KeyEvent> {

    private final ComboBox<RackVM> cb;
    private final RackService rackService;

    public RackComboBoxKeyEventHandler(ComboBox<RackVM> cb) {
        this.cb = cb;
        this.rackService = SpringUtils.getBean(RackService.class);
    }

    @Override
    public void handle(KeyEvent event) {
        String value = cb.getEditor().getText();
        RackVM selected = cb.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getName().equals(value)) {
            return;
        }
        cb.hide();
        if (StringUtils.isNotBlank(value) && value.length() >= 1) {
            AsyncUtils.supply(() -> rackService.searchRackByKeyword(value)).thenAccept(list -> {
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

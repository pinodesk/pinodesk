package toska.desktop.javafx.listener;

import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import toska.desktop.service.DrugCategoryService;
import toska.desktop.utility.SpringUtils;
import toska.desktop.viewmodel.DrugCategoryVM;

public class DrugCategoryComboBoxKeyEventHandler implements EventHandler<KeyEvent> {

    private final ComboBox<DrugCategoryVM> cb;
    private final DrugCategoryService drugCategoryService;

    public DrugCategoryComboBoxKeyEventHandler(ComboBox<DrugCategoryVM> cb) {
        this.cb = cb;
        this.drugCategoryService = SpringUtils.getBean(DrugCategoryService.class);
    }

    @Override
    public void handle(KeyEvent event) {
        String value = cb.getEditor().getText();
        DrugCategoryVM selected = cb.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getName().equals(value)) {
            return;
        }
        cb.hide();
        if (StringUtils.isNotBlank(value) && value.length() >= 1) {
            AsyncUtils.supply(() -> drugCategoryService.searchDrugCategoriesByKeyword(value)).thenAccept(list -> {
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

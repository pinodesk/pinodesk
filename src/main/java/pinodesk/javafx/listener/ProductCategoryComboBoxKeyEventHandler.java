package pinodesk.javafx.listener;

import com.mudiatech.toolbox.future.AsyncUtils;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import pinodesk.service.ProductCategoryService;
import pinodesk.viewmodel.ProductCategoryVM;
import pinodesk.util.SpringUtils;

public class ProductCategoryComboBoxKeyEventHandler implements EventHandler<KeyEvent> {

    private final ComboBox<ProductCategoryVM> comboBox;
    private final ProductCategoryService productCategoryService;

    public ProductCategoryComboBoxKeyEventHandler(ComboBox<ProductCategoryVM> comboBox) {
        this.comboBox = comboBox;
        this.productCategoryService = SpringUtils.getBean(ProductCategoryService.class);
    }

    @Override
    public void handle(KeyEvent event) {
        String value = comboBox.getEditor().getText();
        ProductCategoryVM selected = comboBox.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getName().equals(value)) {
            return;
        }
        comboBox.hide();
        if (StringUtils.isNotBlank(value) && value.length() >= 1) {
            AsyncUtils.supply(() -> productCategoryService.searchProductCategoryByKeyword(value)).thenAccept(list -> {
                if (!list.isEmpty()) {
                    Platform.runLater(() -> {
                        comboBox.setItems(FXCollections.observableList(list));
                        comboBox.setVisibleRowCount(list.size() > 10 ? 10 : list.size());
                        comboBox.show();
                    });
                }
            });
        }
    }

}

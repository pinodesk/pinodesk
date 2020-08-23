package com.gitlab.muhammadkholidb.bianglala.listener;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.data.model.Product;
import com.gitlab.muhammadkholidb.bianglala.service.ProductService;
import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class ComboBoxProductAutoCompleteListener implements ChangeListener<String> {

    private ProductService productService;

    private ComboBox<Product> comboBox;

    private static final String SEPARATOR = " - ";

    public ComboBoxProductAutoCompleteListener(ComboBox<Product> comboBox) {
        this.comboBox = comboBox;
        productService = ApplicationContextHolder.get().getBean(ProductService.class);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String ov, String nv) {
        if (StringUtils.isBlank(nv)) {
            comboBox.getItems().clear();
            comboBox.hide();
            return;
        }
        if (nv.contains(SEPARATOR)) {
            Product selected = comboBox.getSelectionModel().getSelectedItem();
            String[] s = nv.split(SEPARATOR);
            if (selected.getCode().equals(s[0]) && selected.getName().equals(s[1])) {
                return;
            }
        }
        if (nv.length() >= 3) {
            List<Product> products = productService.getProductsByKeyword(nv);
            comboBox.getItems().clear();
            comboBox.hide();
            if (!products.isEmpty()) {
                comboBox.setVisibleRowCount(products.size());
                comboBox.getItems().addAll(FXCollections.observableArrayList(products));
                comboBox.show();
            }
        }
    }
    
}

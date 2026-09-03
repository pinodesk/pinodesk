package com.pinodesk.controller.catalog.product;

import com.pinodesk.constant.CommonLabel;
import com.pinodesk.controller.CommonDataChooseController;
import com.pinodesk.pandora.utility.EventUtils;
import com.pinodesk.pandora.utility.TableViewUtils;
import com.pinodesk.service.ProductCategoryService;
import com.pinodesk.toolbox.future.AsyncUtils;
import com.pinodesk.util.SpringUtils;
import com.pinodesk.viewmodel.ProductCategoryVM;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ProductCategoryChooseController extends CommonDataChooseController<ProductCategoryVM> {

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<ProductCategoryVM> tblProductCategory;

    @FXML
    private TableColumn<ProductCategoryVM, String> colCode;

    @FXML
    private TableColumn<ProductCategoryVM, String> colName;

    @FXML
    private TableColumn<ProductCategoryVM, String> colDescription;

    private ProductCategoryService productCategoryService;

    @Override
    protected void initDataChooseControlActions() {
        TableViewUtils.setColumnValue(colCode, ProductCategoryVM::getCode);
        TableViewUtils.setColumnValue(colName, ProductCategoryVM::getName);
        TableViewUtils.setColumnValue(colDescription, ProductCategoryVM::getDescription);
        tblProductCategory.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        registerKeyListener();
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        // Nothing to do
    }

    @Override
    protected ProductCategoryVM getSelectedData() {
        return tblProductCategory.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices() {
        productCategoryService = SpringUtils.getBean(ProductCategoryService.class);
    }

    private void searchProductCategories() {
        tblProductCategory.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblProductCategory.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> productCategoryService.searchProductCategoryByKeyword(tfSearch.getText()))
                .thenAccept(categories -> Platform.runLater(() -> {
                    if (categories.isEmpty()) {
                        tblProductCategory.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblProductCategory.setItems(FXCollections.observableList(categories));
                    TableViewUtils.sortAscending(tblProductCategory, colName);
                }));
    }

    private void registerKeyListener() {
        tblProductCategory.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblProductCategory.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                btnChoose.fire();
            }
        });
        tfSearch.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                searchProductCategories();
            }
        });
    }

}

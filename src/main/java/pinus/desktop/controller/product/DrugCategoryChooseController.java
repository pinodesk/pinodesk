package pinus.desktop.controller.product;

import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import org.springframework.context.ApplicationContext;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.controller.CommonDataChooseController;
import pinus.desktop.service.DrugCategoryService;
import pinus.desktop.viewmodel.DrugCategoryVM;

public class DrugCategoryChooseController extends CommonDataChooseController<DrugCategoryVM> {

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<DrugCategoryVM> tblDrugCategory;

    @FXML
    private TableColumn<DrugCategoryVM, String> colCode;

    @FXML
    private TableColumn<DrugCategoryVM, String> colName;

    @FXML
    private TableColumn<DrugCategoryVM, String> colDescription;

    private DrugCategoryService drugCategoryService;

    @Override
    protected void initDataChooseControlActions() {
        TableViewUtils.setColumnValue(colCode, DrugCategoryVM::getCode);
        TableViewUtils.setColumnValue(colName, DrugCategoryVM::getName);
        TableViewUtils.setColumnValue(colDescription, DrugCategoryVM::getDescription);
        registerKeyListener();
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        // Nothing to do
    }

    @Override
    protected DrugCategoryVM getSelectedData() {
        return tblDrugCategory.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        drugCategoryService = ctx.getBean(DrugCategoryService.class);
    }

    private void searchProductCategories() {
        tblDrugCategory.setPlaceholder(new Label(translate(CommonLabel.LBL_LOADING_DATA)));
        tblDrugCategory.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> drugCategoryService.searchDrugCategoriesByKeyword(tfSearch.getText()))
                .thenAccept(categories -> Platform.runLater(() -> {
                    if (categories.isEmpty()) {
                        tblDrugCategory.setPlaceholder(new Label(translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblDrugCategory.setItems(FXCollections.observableList(categories));
                    TableViewUtils.sortAscending(tblDrugCategory, colName);
                }));
    }

    private void registerKeyListener() {
        tblDrugCategory.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblDrugCategory.setOnKeyPressed(event -> {
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

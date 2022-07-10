package pinus.desktop.controller.product;

import org.springframework.context.ApplicationContext;

import com.gitlab.muhammadkholidb.pandora.utility.EventUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TableViewUtils;
import com.gitlab.muhammadkholidb.toolbox.future.AsyncUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.controller.CommonDataChooseController;
import pinus.desktop.service.DrugClassificationService;
import pinus.desktop.viewmodel.DrugClassificationVM;

public class DrugClassificationChooseController extends CommonDataChooseController<DrugClassificationVM> {

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<DrugClassificationVM> tblDrugClassification;

    @FXML
    private TableColumn<DrugClassificationVM, String> colCode;

    @FXML
    private TableColumn<DrugClassificationVM, String> colName;

    @FXML
    private TableColumn<DrugClassificationVM, String> colDescription;

    private DrugClassificationService drugClassificationService;

    @Override
    protected void initDataChooseControlActions() {
        TableViewUtils.setColumnValue(colCode, DrugClassificationVM::getCode);
        TableViewUtils.setColumnValue(colName, DrugClassificationVM::getName);
        TableViewUtils.setColumnValue(colDescription, DrugClassificationVM::getDescription);
        registerKeyListener();
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        // Nothing to do
    }

    @Override
    protected DrugClassificationVM getSelectedData() {
        return tblDrugClassification.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        drugClassificationService = ctx.getBean(DrugClassificationService.class);
    }

    private void searchProductCategories() {
        tblDrugClassification.setPlaceholder(new Label(translate(CommonLabel.LBL_LOADING_DATA)));
        tblDrugClassification.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(
                () -> drugClassificationService
                        .searchDrugClassificationsByKeyword(tfSearch.getText(), resources.getLocale().getLanguage()))
                .thenAccept(classifications -> Platform.runLater(() -> {
                    if (classifications.isEmpty()) {
                        tblDrugClassification.setPlaceholder(new Label(translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblDrugClassification.setItems(FXCollections.observableList(classifications));
                    TableViewUtils.sortAscending(tblDrugClassification, colName);
                }));
    }

    private void registerKeyListener() {
        tblDrugClassification.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblDrugClassification.setOnKeyPressed(event -> {
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

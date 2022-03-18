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
import pinus.desktop.service.UnitService;
import pinus.desktop.viewmodel.UnitVM;

public class UnitChooseController extends CommonDataChooseController<UnitVM> {

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<UnitVM> tblUnit;

    @FXML
    private TableColumn<UnitVM, String> colLabel;

    @FXML
    private TableColumn<UnitVM, String> colName;

    private UnitService unitService;

    @Override
    protected void initDataChooseControlActions() {
        TableViewUtils.setColumnValue(colLabel, UnitVM::getLabel);
        TableViewUtils.setColumnValue(colName, UnitVM::getName);
        registerKeyListener();
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        // Nothing to do
    }

    @Override
    protected UnitVM getSelectedData() {
        return tblUnit.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        unitService = ctx.getBean(UnitService.class);
    }

    private void searchProductCategories() {
        tblUnit.setPlaceholder(new Label(translate(CommonLabel.LBL_LOADING_DATA)));
        tblUnit.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> unitService.searchUnitByKeyword(tfSearch.getText()))
                .thenAccept(units -> Platform.runLater(() -> {
                    if (units.isEmpty()) {
                        tblUnit.setPlaceholder(new Label(translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblUnit.setItems(FXCollections.observableList(units));
                    TableViewUtils.sortAscending(tblUnit, colName);
                }));
    }

    private void registerKeyListener() {
        tblUnit.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblUnit.setOnKeyPressed(event -> {
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

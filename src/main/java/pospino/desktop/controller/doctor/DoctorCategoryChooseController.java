package pospino.desktop.controller.doctor;

import java.time.LocalDateTime;

import com.gitlab.muhammadkholidb.pandora.factory.LocalDateTimeCellFactory;
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
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.controller.CommonDataChooseController;
import pospino.desktop.service.DoctorService;
import pospino.desktop.util.SpringUtils;
import pospino.desktop.viewmodel.DoctorCategoryVM;

public class DoctorCategoryChooseController extends CommonDataChooseController<DoctorCategoryVM> {

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<DoctorCategoryVM> tblDoctorCategory;

    @FXML
    private TableColumn<DoctorCategoryVM, String> colCode;

    @FXML
    private TableColumn<DoctorCategoryVM, String> colName;

    @FXML
    private TableColumn<DoctorCategoryVM, LocalDateTime> colCreatedAt;

    @FXML
    private TableColumn<DoctorCategoryVM, LocalDateTime> colUpdatedAt;

    private DoctorService doctorService;

    @Override
    protected void initDataChooseControlActions() {
        TableViewUtils.setColumnValue(colCode, DoctorCategoryVM::getCode);
        TableViewUtils.setColumnValue(colName, DoctorCategoryVM::getName);
        TableViewUtils.initTableColumn(
                colCreatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                DoctorCategoryVM::getCreatedAt);
        TableViewUtils.initTableColumn(
                colUpdatedAt,
                new LocalDateTimeCellFactory<>(CommonConstants.DATETIME_DISPLAY_PATTERN),
                DoctorCategoryVM::getUpdatedAt);
        tblDoctorCategory.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
        registerKeyListener();
        setFocused(contentPane);
    }

    @Override
    protected void initDataChooseControlValues() {
        // Nothing to do
    }

    @Override
    protected DoctorCategoryVM getSelectedData() {
        return tblDoctorCategory.getSelectionModel().getSelectedItem();
    }

    @Override
    protected void initServices() {
        doctorService = SpringUtils.getBean(DoctorService.class);
    }

    private void searchDoctorCategories() {
        tblDoctorCategory.setPlaceholder(new Label(t.translate(CommonLabel.LBL_LOADING_DATA)));
        tblDoctorCategory.setItems(FXCollections.observableArrayList());
        AsyncUtils.supply(() -> doctorService.searchDoctorCategoryByKeyword(tfSearch.getText()))
                .thenAccept(categories -> Platform.runLater(() -> {
                    if (categories.isEmpty()) {
                        tblDoctorCategory.setPlaceholder(new Label(t.translate(CommonLabel.LBL_NO_DATA)));
                    }
                    tblDoctorCategory.setItems(FXCollections.observableList(categories));
                    TableViewUtils.sortAscending(tblDoctorCategory, colName);
                }));
    }

    private void registerKeyListener() {
        tblDoctorCategory.setOnMouseClicked(event -> {
            if (EventUtils.isDoubleClick(event)) {
                btnChoose.fire();
            }
        });
        tblDoctorCategory.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                btnChoose.fire();
            }
        });
        tfSearch.setOnKeyPressed(event -> {
            if (EventUtils.isEnter(event)) {
                searchDoctorCategories();
            }
        });
    }

}

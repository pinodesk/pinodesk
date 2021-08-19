package toska.desktop.controller.purchase;

import org.controlsfx.validation.ValidationSupport;
import org.springframework.context.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import toska.desktop.controller.CommonDataSaveController;

public class PurchaseAddController extends CommonDataSaveController {

    @FXML
    private Button btnSaveAndAdd;

    @FXML
    void onActionBtnSaveAndAdd(ActionEvent event) {
        
    }

    @FXML
    void onActionBtnAddProduct(ActionEvent event) {

    }

    @FXML
    void onActionBtnNewProduct(ActionEvent event) {

    }

    @FXML
    void onActionBtnNewSupplier(ActionEvent event) {

    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        
    }

    @Override
    protected void initDataSaveControlActions() {

    }

    @Override
    protected void initDataSaveControlValues() {
        
    }

    @Override
    protected void registerValidator(ValidationSupport vs) {
        
    }

    @Override
    protected Object save() {
        return true;
    }

}

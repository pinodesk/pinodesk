package pospino.desktop.controller.product;

import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toStringOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.gitlab.mudiasoft.pandora.model.SimpleComboBoxModel;
import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.TextFieldUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import pospino.desktop.constant.CommonLabel;
import pospino.desktop.constant.ProductStatus;
import pospino.desktop.constant.StringConstants;
import pospino.desktop.controller.CommonDataFilterController;
import pospino.desktop.viewmodel.ChooseResultVM;
import pospino.desktop.viewmodel.ProductCategoryVM;
import pospino.desktop.viewmodel.ProductFilterVM;
import pospino.desktop.viewmodel.UnitVM;

public class ProductFilterController extends CommonDataFilterController<ProductFilterVM> {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfBarcode;

    @FXML
    private TextField tfCategory;

    @FXML
    private TextField tfUnit;

    @FXML
    private TextField tfStockQuantityMin;

    @FXML
    private TextField tfStockQuantityMax;

    @FXML
    private TextField tfGeneralSellingPriceMin;

    @FXML
    private TextField tfGeneralSellingPriceMax;

    @FXML
    private TextField tfPrescriptionSellingPriceMin;

    @FXML
    private TextField tfPrescriptionSellingPriceMax;

    @FXML
    private DatePicker dpExpiredDateMin;

    @FXML
    private DatePicker dpExpiredDateMax;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;

    @FXML
    private TextField tfBatchNumber;

    @FXML
    private TextField tfDescription;

    @FXML
    private HBox hboxPrescriptionSellingPrice;

    private UnitVM selectedUnit;

    private ProductCategoryVM selectedProductCategory;

    @Override
    protected void initDataFilterControlValues() {
        ComboBoxUtils.selectIndex(cbStatus, 0);
        if (currentFilter != null) {
            Integer stockQuantityMin = currentFilter.getStockQuantityMin();
            Integer stockQuantityMax = currentFilter.getStockQuantityMax();
            BigDecimal prescriptionSellingPriceMax = currentFilter.getPrescriptionSellingPriceMax();
            BigDecimal prescriptionSellingPriceMin = currentFilter.getPrescriptionSellingPriceMin();
            BigDecimal generalSellingPriceMax = currentFilter.getGeneralSellingPriceMax();
            BigDecimal generalSellingPriceMin = currentFilter.getGeneralSellingPriceMin();
            ProductCategoryVM category = currentFilter.getCategory();
            UnitVM unit = currentFilter.getUnit();
            LocalDate expiredDateMin = currentFilter.getExpiredDateMin();
            LocalDate expiredDateMax = currentFilter.getExpiredDateMax();
            ProductStatus status = currentFilter.getStatus();
            tfName.setText(currentFilter.getName());
            tfDescription.setText(currentFilter.getDescription());
            tfCode.setText(currentFilter.getCode());
            tfBarcode.setText(currentFilter.getBarcode());
            tfStockQuantityMin.setText(toStringOrNull(stockQuantityMin));
            tfStockQuantityMax.setText(toStringOrNull(stockQuantityMax));
            tfPrescriptionSellingPriceMax.setText(toStringOrNull(prescriptionSellingPriceMax));
            tfPrescriptionSellingPriceMin.setText(toStringOrNull(prescriptionSellingPriceMin));
            tfGeneralSellingPriceMax.setText(toStringOrNull(generalSellingPriceMax));
            tfGeneralSellingPriceMin.setText(toStringOrNull(generalSellingPriceMin));
            dpExpiredDateMax.setValue(expiredDateMax);
            dpExpiredDateMin.setValue(expiredDateMin);
            tfBatchNumber.setText(currentFilter.getBatchNumber());
            if (status != null) {
                ComboBoxUtils.select(
                        cbStatus,
                        () -> cbStatus.getItems().stream().filter(vm -> status.equals(vm.getValue())).findAny()
                                .orElseThrow());
            }
            if (category != null) {
                selectedProductCategory = category;
                tfCategory.setText(category.getName());
            }
            if (unit != null) {
                selectedUnit = unit;
                tfUnit.setText(unit.getLabel());
            }
        }
        if (!isPharmacyFeatureEnabled()) {
            setVisibleInLayout(false, hboxPrescriptionSellingPrice);
        }
    }

    @Override
    protected ProductFilterVM getFreshFilterValues() {
        String generalSellingPriceMax = tfGeneralSellingPriceMax.getText();
        String generalSellingPriceMin = tfGeneralSellingPriceMin.getText();
        String prescriptionSellingPriceMax = tfPrescriptionSellingPriceMax.getText();
        String prescriptionSellingPriceMin = tfPrescriptionSellingPriceMin.getText();
        String stockQuantityMax = tfStockQuantityMax.getText();
        String stockQuantityMin = tfStockQuantityMin.getText();
        LocalDate expiredDateMin = dpExpiredDateMin.getValue();
        LocalDate expiredDateMax = dpExpiredDateMax.getValue();
        ProductStatus status = ComboBoxUtils.getSelectedItem(cbStatus).getValue();
        ProductFilterVM filter = new ProductFilterVM();
        filter.setName(tfName.getText());
        filter.setDescription(tfDescription.getText());
        filter.setCode(tfCode.getText());
        filter.setBarcode(tfBarcode.getText());
        filter.setBatchNumber(tfBatchNumber.getText());
        filter.setExpiredDateMin(expiredDateMin);
        filter.setExpiredDateMax(expiredDateMax);
        if (StringUtils.isNotBlank(generalSellingPriceMax)) {
            filter.setGeneralSellingPriceMax(NumberUtils.toScaledBigDecimal(generalSellingPriceMax));
        }
        if (StringUtils.isNotBlank(generalSellingPriceMin)) {
            filter.setGeneralSellingPriceMin(NumberUtils.toScaledBigDecimal(generalSellingPriceMin));
        }
        if (StringUtils.isNotBlank(prescriptionSellingPriceMax)) {
            filter.setPrescriptionSellingPriceMax(NumberUtils.toScaledBigDecimal(prescriptionSellingPriceMax));
        }
        if (StringUtils.isNotBlank(prescriptionSellingPriceMin)) {
            filter.setPrescriptionSellingPriceMin(NumberUtils.toScaledBigDecimal(prescriptionSellingPriceMin));
        }
        if (StringUtils.isNotBlank(stockQuantityMax)) {
            filter.setStockQuantityMax(NumberUtils.toInt(stockQuantityMax));
        }
        if (StringUtils.isNotBlank(stockQuantityMin)) {
            filter.setStockQuantityMin(NumberUtils.toInt(stockQuantityMin));
        }
        filter.setStatus(status);
        filter.setUnit(selectedUnit);
        filter.setCategory(selectedProductCategory);
        return filter;
    }

    @Override
    protected void resetControls() {
        TextFieldUtils.setTextEmpty(
                tfName,
                tfCode,
                tfBarcode,
                tfDescription,
                tfCategory,
                tfUnit,
                tfGeneralSellingPriceMin,
                tfGeneralSellingPriceMax,
                tfPrescriptionSellingPriceMax,
                tfPrescriptionSellingPriceMin,
                tfStockQuantityMax,
                tfStockQuantityMin,
                tfBatchNumber);
        dpExpiredDateMax.setValue(null);
        dpExpiredDateMin.setValue(null);
        ComboBoxUtils.selectIndex(cbStatus, 0);
        selectedUnit = null;
        selectedProductCategory = null;
    }

    @Override
    protected void initServices() {
        // No services used
    }

    @Override
    protected void initDataFilterControlActions() {
        initCustomDatePicker(dpExpiredDateMax, dpExpiredDateMin);
        TextFieldUtils.setDigitTextFields(
                tfBarcode,
                tfPrescriptionSellingPriceMin,
                tfPrescriptionSellingPriceMax,
                tfGeneralSellingPriceMin,
                tfGeneralSellingPriceMax,
                tfStockQuantityMin,
                tfStockQuantityMax);
        ComboBoxUtils.initSimple(
                cbStatus,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(ProductStatus.ACTIVE, t.translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(ProductStatus.INACTIVE, t.translate(CommonLabel.LBL_INACTIVE)));
        setProductCategoryChooser(tfCategory, this::handleSelectedProductCategory, tfUnit.getParent());
        setUnitChooser(tfUnit, this::handleSelectedUnit, cbStatus);
    }

    public void handleSelectedUnit(ChooseResultVM<UnitVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(unit -> {
            selectedUnit = unit;
            tfUnit.setText(unit.getLabel());
        }, () -> {
            selectedUnit = null;
            tfUnit.setText("");
        });
    }

    private void handleSelectedProductCategory(ChooseResultVM<ProductCategoryVM> result) {
        if (result == null || result.isCancelled()) {
            return;
        }
        result.getData().ifPresentOrElse(category -> {
            selectedProductCategory = category;
            tfCategory.setText(category.getName());
        }, () -> {
            selectedProductCategory = null;
            tfCategory.setText("");
        });
    }

}

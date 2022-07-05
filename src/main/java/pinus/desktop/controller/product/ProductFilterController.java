package pinus.desktop.controller.product;

import static com.gitlab.muhammadkholidb.toolbox.data.DateTimeUtils.parseLocalDateQuietly;
import static com.gitlab.muhammadkholidb.toolbox.data.StringNumberUtils.toStringOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.gitlab.muhammadkholidb.pandora.control.MaskedTextField;
import com.gitlab.muhammadkholidb.pandora.model.SimpleComboBoxModel;
import com.gitlab.muhammadkholidb.pandora.utility.ComboBoxUtils;
import com.gitlab.muhammadkholidb.pandora.utility.TextFieldUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.CommonLabel;
import pinus.desktop.constant.ProductStatus;
import pinus.desktop.constant.StringConstants;
import pinus.desktop.controller.CommonDataFilterController;
import pinus.desktop.viewmodel.ChooseResultVM;
import pinus.desktop.viewmodel.ProductCategoryVM;
import pinus.desktop.viewmodel.ProductFilterVM;
import pinus.desktop.viewmodel.UnitVM;

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
    private MaskedTextField tfExpiredDateMin;

    @FXML
    private MaskedTextField tfExpiredDateMax;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbStatus;

    @FXML
    private TextField tfBatchNumber;

    @FXML
    private TextField tfDescription;

    private UnitVM selectedUnit;

    private ProductCategoryVM selectedProductCategory;

    @Override
    protected void initDataFilterControlValues() {
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
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN);
            tfExpiredDateMax.setPlainText(expiredDateMax == null ? "" : expiredDateMax.format(dateFormatter));
            tfExpiredDateMin.setPlainText(expiredDateMin == null ? "" : expiredDateMin.format(dateFormatter));
            tfBatchNumber.setText(currentFilter.getBatchNumber());
            if (status != null) {
                ComboBoxUtils.select(
                        cbStatus,
                        () -> cbStatus.getItems().stream().filter(vm -> status.toString().equals(vm.getValue()))
                                .findAny().orElseThrow());
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
    }

    @Override
    protected ProductFilterVM getFreshFilterValues() {
        String generalSellingPriceMax = tfGeneralSellingPriceMax.getText();
        String generalSellingPriceMin = tfGeneralSellingPriceMin.getText();
        String prescriptionSellingPriceMax = tfPrescriptionSellingPriceMax.getText();
        String prescriptionSellingPriceMin = tfPrescriptionSellingPriceMin.getText();
        String stockQuantityMax = tfStockQuantityMax.getText();
        String stockQuantityMin = tfStockQuantityMin.getText();
        String expiredDateMin = tfExpiredDateMin.getTextMasked();
        String expiredDateMax = tfExpiredDateMax.getTextMasked();
        String status = ComboBoxUtils.getSelectedItem(cbStatus).getValue();
        ProductFilterVM filter = new ProductFilterVM();
        filter.setName(tfName.getText());
        filter.setDescription(tfDescription.getText());
        filter.setCode(tfCode.getText());
        filter.setBarcode(tfBarcode.getText());
        filter.setBatchNumber(tfBatchNumber.getText());
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
        if (StringUtils.isNotBlank(expiredDateMin)) {
            filter.setExpiredDateMin(parseLocalDateQuietly(expiredDateMin, CommonConstants.DATE_DISPLAY_PATTERN));
        }
        if (StringUtils.isNotBlank(expiredDateMax)) {
            filter.setExpiredDateMax(parseLocalDateQuietly(expiredDateMax, CommonConstants.DATE_DISPLAY_PATTERN));
        }
        if (StringUtils.isNotBlank(status)) {
            filter.setStatus(ProductStatus.valueOf(status));
        }
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
        tfExpiredDateMax.setPlainText("");
        tfExpiredDateMin.setPlainText("");
        ComboBoxUtils.selectIndex(cbStatus, 0);
        selectedUnit = null;
        selectedProductCategory = null;
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        // No services used
    }

    @Override
    protected void initDataFilterControlActions() {
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
                new SimpleComboBoxModel(StringConstants.EMPTY, StringConstants.EMPTY),
                new SimpleComboBoxModel(ProductStatus.ACTIVE.toString(), translate(CommonLabel.LBL_ACTIVE)),
                new SimpleComboBoxModel(ProductStatus.INACTIVE.toString(), translate(CommonLabel.LBL_INACTIVE)));
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

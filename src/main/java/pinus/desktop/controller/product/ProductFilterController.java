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
import pinus.desktop.constant.SimpleStatus;
import pinus.desktop.constant.StringConstants;
import pinus.desktop.controller.CommonDataFilterController;
import pinus.desktop.javafx.converter.ProductCategoryComboBoxConverter;
import pinus.desktop.javafx.converter.RackComboBoxConverter;
import pinus.desktop.javafx.converter.UnitComboBoxConverter;
import pinus.desktop.javafx.listener.ProductCategoryComboBoxKeyEventHandler;
import pinus.desktop.javafx.listener.RackComboBoxKeyEventHandler;
import pinus.desktop.javafx.listener.UnitComboBoxKeyEventHandler;
import pinus.desktop.service.ProductCategoryService;
import pinus.desktop.service.RackService;
import pinus.desktop.service.UnitService;
import pinus.desktop.viewmodel.ProductCategoryVM;
import pinus.desktop.viewmodel.ProductFilterVM;
import pinus.desktop.viewmodel.RackVM;
import pinus.desktop.viewmodel.UnitVM;

public class ProductFilterController extends CommonDataFilterController<ProductFilterVM> {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfBarcode;

    @FXML
    private ComboBox<ProductCategoryVM> cbCategory;

    @FXML
    private ComboBox<UnitVM> cbUnit;

    @FXML
    private TextField tfQuantityMin;

    @FXML
    private TextField tfQuantityMax;

    @FXML
    private TextField tfPurchasePriceMin;

    @FXML
    private TextField tfPurchasePriceMax;

    @FXML
    private TextField tfSellingPriceMin;

    @FXML
    private TextField tfSellingPriceMax;

    @FXML
    private MaskedTextField tfExpiredDateMin;

    @FXML
    private MaskedTextField tfExpiredDateMax;

    @FXML
    private ComboBox<RackVM> cbRack;

    @FXML
    private ComboBox<SimpleComboBoxModel> cbIncludesVat;

    private ProductCategoryService productCategoryService;

    private RackService rackService;

    private UnitService unitService;

    @Override
    protected void initDataFilterControlValues() {
        if (currentFilter != null) {
            Integer quantityMin = currentFilter.getQuantityMin();
            Integer quantityMax = currentFilter.getQuantityMax();
            BigDecimal purchasePriceMax = currentFilter.getPurchasePriceMax();
            BigDecimal purchasePriceMin = currentFilter.getPurchasePriceMin();
            BigDecimal sellingPriceMax = currentFilter.getSellingPriceMax();
            BigDecimal sellingPriceMin = currentFilter.getSellingPriceMin();
            Long categoryId = currentFilter.getCategoryId();
            Long unitId = currentFilter.getUnitId();
            Long rackId = currentFilter.getRackId();
            LocalDate expiredDateMin = currentFilter.getExpiredDateMin();
            LocalDate expiredDateMax = currentFilter.getExpiredDateMax();
            String includesVat = currentFilter.getIncludesVat();
            tfName.setText(currentFilter.getName());
            tfCode.setText(currentFilter.getCode());
            tfBarcode.setText(currentFilter.getBarcode());
            tfQuantityMin.setText(toStringOrNull(quantityMin));
            tfQuantityMax.setText(toStringOrNull(quantityMax));
            tfPurchasePriceMax.setText(toStringOrNull(purchasePriceMax));
            tfPurchasePriceMin.setText(toStringOrNull(purchasePriceMin));
            tfSellingPriceMax.setText(toStringOrNull(sellingPriceMax));
            tfSellingPriceMin.setText(toStringOrNull(sellingPriceMin));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_DISPLAY_PATTERN);
            tfExpiredDateMax.setPlainText(expiredDateMax == null ? null : expiredDateMax.format(dateFormatter));
            tfExpiredDateMin.setPlainText(expiredDateMin == null ? null : expiredDateMin.format(dateFormatter));
            if (categoryId != null) {
                ComboBoxUtils.select(cbCategory, () -> productCategoryService.getProductCategoryById(categoryId));
            }
            if (unitId != null) {
                ComboBoxUtils.select(cbUnit, () -> unitService.getUnitById(unitId));
            }
            if (rackId != null) {
                ComboBoxUtils.select(cbRack, () -> rackService.getRackById(rackId));
            }
            if (StringUtils.isNotBlank(includesVat)) {
                ComboBoxUtils.select(
                        cbIncludesVat,
                        () -> cbIncludesVat.getItems().stream().filter(vm -> includesVat.equals(vm.getValue()))
                                .findAny().orElseThrow());
            }
        }
    }

    @Override
    protected ProductFilterVM getFreshFilterValues() {
        String purchasePriceMax = tfPurchasePriceMax.getText();
        String purchasePriceMin = tfPurchasePriceMin.getText();
        String sellingPriceMax = tfSellingPriceMax.getText();
        String sellingPriceMin = tfSellingPriceMin.getText();
        String quantityMax = tfQuantityMax.getText();
        String quantityMin = tfQuantityMin.getText();
        String expiredDateMin = tfExpiredDateMin.getTextMasked();
        String expiredDateMax = tfExpiredDateMax.getTextMasked();
        ProductCategoryVM selectedCategory = ComboBoxUtils.getSelectedItem(cbCategory);
        UnitVM selectedUnit = ComboBoxUtils.getSelectedItem(cbUnit);
        RackVM selectedRack = ComboBoxUtils.getSelectedItem(cbRack);
        SimpleComboBoxModel selectedIncludesVat = ComboBoxUtils.getSelectedItem(cbIncludesVat);
        ProductFilterVM filter = new ProductFilterVM();
        filter.setName(tfName.getText());
        filter.setCode(tfCode.getText());
        filter.setBarcode(tfBarcode.getText());
        if (StringUtils.isNotBlank(purchasePriceMax)) {
            filter.setPurchasePriceMax(NumberUtils.toScaledBigDecimal(purchasePriceMax));
        }
        if (StringUtils.isNotBlank(purchasePriceMin)) {
            filter.setPurchasePriceMin(NumberUtils.toScaledBigDecimal(purchasePriceMin));
        }
        if (StringUtils.isNotBlank(sellingPriceMax)) {
            filter.setSellingPriceMax(NumberUtils.toScaledBigDecimal(sellingPriceMax));
        }
        if (StringUtils.isNotBlank(sellingPriceMin)) {
            filter.setSellingPriceMin(NumberUtils.toScaledBigDecimal(sellingPriceMin));
        }
        if (StringUtils.isNotBlank(quantityMax)) {
            filter.setQuantityMax(NumberUtils.toInt(quantityMax));
        }
        if (StringUtils.isNotBlank(quantityMin)) {
            filter.setQuantityMin(NumberUtils.toInt(quantityMin));
        }
        if (StringUtils.isNotBlank(expiredDateMin)) {
            filter.setExpiredDateMin(parseLocalDateQuietly(expiredDateMin, CommonConstants.DATE_DISPLAY_PATTERN));
        }
        if (StringUtils.isNotBlank(expiredDateMax)) {
            filter.setExpiredDateMax(parseLocalDateQuietly(expiredDateMax, CommonConstants.DATE_DISPLAY_PATTERN));
        }
        if (selectedCategory != null) {
            filter.setCategoryId(selectedCategory.getId());
            filter.setCategoryCode(selectedCategory.getCode());
        }
        if (selectedUnit != null) {
            filter.setUnitId(selectedUnit.getId());
        }
        if (selectedRack != null) {
            filter.setRackId(selectedRack.getId());
        }
        if (selectedIncludesVat != null) {
            filter.setIncludesVat(selectedIncludesVat.getValue());
        }
        return filter;
    }

    @Override
    protected void resetControls() {
        tfName.setText(null);
        tfCode.setText(null);
        tfBarcode.setText(null);
        tfQuantityMax.setText(null);
        tfQuantityMin.setText(null);
        tfPurchasePriceMax.setText(null);
        tfPurchasePriceMin.setText(null);
        tfSellingPriceMax.setText(null);
        tfSellingPriceMin.setText(null);
        tfExpiredDateMax.setPlainText(null);
        tfExpiredDateMin.setPlainText(null);
        cbCategory.getSelectionModel().clearSelection();
        cbUnit.getSelectionModel().clearSelection();
        cbRack.getSelectionModel().clearSelection();
        cbIncludesVat.getSelectionModel().clearSelection();
    }

    @Override
    protected void initServices(ApplicationContext ctx) {
        productCategoryService = ctx.getBean(ProductCategoryService.class);
        rackService = ctx.getBean(RackService.class);
        unitService = ctx.getBean(UnitService.class);
    }

    @Override
    protected void initDataFilterControlActions() {
        ComboBoxUtils.initAutoComplete(
                cbCategory,
                new ProductCategoryComboBoxKeyEventHandler(cbCategory),
                new ProductCategoryComboBoxConverter(cbCategory));
        ComboBoxUtils
                .initAutoComplete(cbUnit, new UnitComboBoxKeyEventHandler(cbUnit), new UnitComboBoxConverter(cbUnit));
        ComboBoxUtils
                .initAutoComplete(cbRack, new RackComboBoxKeyEventHandler(cbRack), new RackComboBoxConverter(cbRack));
        ComboBoxUtils.initSimple(
                cbIncludesVat,
                new SimpleComboBoxModel(null, StringConstants.EMPTY),
                new SimpleComboBoxModel(SimpleStatus.YES.name(), translate("lbl.yes")),
                new SimpleComboBoxModel(SimpleStatus.NO.name(), translate("lbl.no")));

        TextFieldUtils.setDigitTextFields(
                tfBarcode,
                tfSellingPriceMin,
                tfSellingPriceMax,
                tfPurchasePriceMin,
                tfPurchasePriceMax,
                tfQuantityMin,
                tfQuantityMax);

    }

}

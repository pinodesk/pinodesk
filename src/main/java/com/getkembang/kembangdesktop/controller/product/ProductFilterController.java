package com.getkembang.kembangdesktop.controller.product;

import java.math.BigDecimal;
import java.util.Date;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.Page;
import com.getkembang.kembangdesktop.constant.StringConstants;
import com.getkembang.kembangdesktop.controller.BaseDataFilterController;
import com.getkembang.kembangdesktop.javafx.control.MaskedTextField;
import com.getkembang.kembangdesktop.javafx.converter.ProductCategoryComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.converter.RackComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.converter.UnitComboBoxConverter;
import com.getkembang.kembangdesktop.javafx.listener.ProductCategoryComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.javafx.listener.RackComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.javafx.listener.UnitComboBoxKeyEventHandler;
import com.getkembang.kembangdesktop.service.ProductCategoryService;
import com.getkembang.kembangdesktop.service.RackService;
import com.getkembang.kembangdesktop.service.UnitService;
import com.getkembang.kembangdesktop.utility.ComboBoxUtils;
import com.getkembang.kembangdesktop.utility.FXUtils;
import com.getkembang.kembangdesktop.viewmodel.BasicComboBoxVM;
import com.getkembang.kembangdesktop.viewmodel.ProductCategoryVM;
import com.getkembang.kembangdesktop.viewmodel.ProductFilterVM;
import com.getkembang.kembangdesktop.viewmodel.RackVM;
import com.getkembang.kembangdesktop.viewmodel.UnitVM;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductFilterController extends BaseDataFilterController<ProductFilterVM> {

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
    private ComboBox<BasicComboBoxVM> cbIncludesVat;

    private ProductCategoryService productCategoryService;

    private RackService rackService;

    private UnitService unitService;

    @Override
    protected void initFilterControlsValues(final ProductFilterVM currentProductFilter) {
        if (currentProductFilter != null) {
            Integer quantityMin = currentProductFilter.getQuantityMin();
            Integer quantityMax = currentProductFilter.getQuantityMax();
            BigDecimal purchasePriceMax = currentProductFilter.getPurchasePriceMax();
            BigDecimal purchasePriceMin = currentProductFilter.getPurchasePriceMin();
            BigDecimal sellingPriceMax = currentProductFilter.getSellingPriceMax();
            BigDecimal sellingPriceMin = currentProductFilter.getSellingPriceMin();
            Long categoryId = currentProductFilter.getCategoryId();
            Long unitId = currentProductFilter.getUnitId();
            Long rackId = currentProductFilter.getRackId();
            Date expiredDateMin = currentProductFilter.getExpiredDateMin();
            Date expiredDateMax = currentProductFilter.getExpiredDateMax();
            String includesVat = currentProductFilter.getIncludesVat();
            tfName.setText(currentProductFilter.getName());
            tfCode.setText(currentProductFilter.getCode());
            tfBarcode.setText(currentProductFilter.getBarcode());
            tfQuantityMin.setText(toStringOrDefault(quantityMin, null));
            tfQuantityMax.setText(toStringOrDefault(quantityMax, null));
            tfPurchasePriceMax.setText(toStringOrDefault(purchasePriceMax, null));
            tfPurchasePriceMin.setText(toStringOrDefault(purchasePriceMin, null));
            tfSellingPriceMax.setText(toStringOrDefault(sellingPriceMax, null));
            tfSellingPriceMin.setText(toStringOrDefault(sellingPriceMin, null));
            tfExpiredDateMax.setPlainText(expiredDateMax == null ? null
                    : DateFormatUtils.format(expiredDateMax, CommonConstants.DATE_PATTERN));
            tfExpiredDateMin.setPlainText(expiredDateMin == null ? null
                    : DateFormatUtils.format(expiredDateMin, CommonConstants.DATE_PATTERN));
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
                ComboBoxUtils.select(cbIncludesVat, () -> cbIncludesVat.getItems().stream()
                        .filter(vm -> includesVat.equals(vm.getValue())).findAny().orElseThrow());
            }
        }
    }

    @Override
    protected void setFilterValues(final ProductFilterVM filter) {
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
        BasicComboBoxVM selectedIncludesVat = ComboBoxUtils.getSelectedItem(cbIncludesVat);
        filter.setName(tfName.getText());
        filter.setCode(tfCode.getText());
        filter.setBarcode(tfBarcode.getText());
        filter.setPurchasePriceMax(
                StringUtils.isBlank(purchasePriceMax) ? null : NumberUtils.toScaledBigDecimal(purchasePriceMax));
        filter.setPurchasePriceMin(
                StringUtils.isBlank(purchasePriceMin) ? null : NumberUtils.toScaledBigDecimal(purchasePriceMin));
        filter.setSellingPriceMax(
                StringUtils.isBlank(sellingPriceMax) ? null : NumberUtils.toScaledBigDecimal(sellingPriceMax));
        filter.setSellingPriceMin(
                StringUtils.isBlank(sellingPriceMin) ? null : NumberUtils.toScaledBigDecimal(sellingPriceMin));
        filter.setQuantityMax(StringUtils.isBlank(quantityMax) ? null : NumberUtils.toInt(quantityMax));
        filter.setQuantityMin(StringUtils.isBlank(quantityMin) ? null : NumberUtils.toInt(quantityMin));
        filter.setExpiredDateMin(StringUtils.isBlank(expiredDateMin) ? null
                : parseDateQuietly(expiredDateMin, CommonConstants.DATE_PATTERN));
        filter.setExpiredDateMax(StringUtils.isBlank(expiredDateMax) ? null
                : parseDateQuietly(expiredDateMax, CommonConstants.DATE_PATTERN));
        filter.setCategoryId(selectedCategory == null ? null : selectedCategory.getId());
        filter.setCategoryCode(selectedCategory == null ? null : selectedCategory.getCode());
        filter.setUnitId(selectedUnit == null ? null : selectedUnit.getId());
        filter.setRackId(selectedRack == null ? null : selectedRack.getId());
        filter.setIncludesVat(selectedIncludesVat == null ? null : selectedIncludesVat.getValue());
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
    protected void initControlsActions() {
        ComboBoxUtils.initAutoComplete(cbCategory, new ProductCategoryComboBoxKeyEventHandler(cbCategory),
                new ProductCategoryComboBoxConverter(cbCategory));
        ComboBoxUtils.initAutoComplete(cbUnit, new UnitComboBoxKeyEventHandler(cbUnit),
                new UnitComboBoxConverter(cbUnit));
        ComboBoxUtils.initAutoComplete(cbRack, new RackComboBoxKeyEventHandler(cbRack),
                new RackComboBoxConverter(cbRack));
        ComboBoxUtils.initBasic(cbIncludesVat, new BasicComboBoxVM(null, StringConstants.EMPTY),
                new BasicComboBoxVM(CommonConstants.YES, translate("lbl.yes")),
                new BasicComboBoxVM(CommonConstants.NO, translate("lbl.no")));
        // @formatter:off
        FXUtils.setDigitFormatter(
                tfBarcode,
                tfSellingPriceMin,
                tfSellingPriceMax,  
                tfPurchasePriceMin, 
                tfPurchasePriceMax, 
                tfQuantityMin,
                tfQuantityMax);
        // @formatter:on
    }

    @Override
    protected Page getCurrentPage() {
        return Page.MASTER_PRODUCT_FILTER;
    }

}

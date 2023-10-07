package pospino.desktop.controller.transaction.sale;

import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toBigDecimalOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toIntegerOrNull;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toIntegerOrZero;
import static com.gitlab.mudiasoft.toolbox.data.StringNumberUtils.toStringOrEmpty;

import java.math.BigDecimal;
import java.util.List;

import com.gitlab.mudiasoft.pandora.utility.ComboBoxUtils;
import com.gitlab.mudiasoft.pandora.utility.ControlValidator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pospino.desktop.constant.MessageCode;
import pospino.desktop.constant.SellingMode;
import pospino.desktop.controller.CommonDataSaveController;
import pospino.desktop.controller.transaction.sale.CashierController.ConfirmProduct;
import pospino.desktop.javafx.converter.GroupedProductExpiryComboBoxConverter;
import pospino.desktop.viewmodel.GroupedProductExpiryVM;
import pospino.desktop.viewmodel.ProductVM;
import pospino.desktop.viewmodel.SaleProductVM;

public class CashierConfirmProductController extends CommonDataSaveController {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfBarcode;

    @FXML
    private TextField tfProductCategory;

    @FXML
    private TextField tfProductUnit;

    @FXML
    private TextField tfCurrentQuantity;

    @FXML
    private TextField tfSellingPrice;

    @FXML
    private TextField tfSaleQuantity;

    @FXML
    private ComboBox<GroupedProductExpiryVM> cbExpiredDate;

    @FXML
    private Button btnRemove;

    private ProductVM currentProduct;

    private SaleProductVM currentSaleProduct;

    private ConfirmProduct confirmProduct;

    private boolean isEdit;

    @FXML
    void onActionBtnRemove(ActionEvent event) {
        confirmProduct.setDelete(true);
        setPageData(confirmProduct);
        close();
    }

    @Override
    protected void initDataSaveControlActions() {
        confirmProduct = getPageData();
        isEdit = confirmProduct.isEdit();
        setVisibleInLayout(isEdit, btnRemove);
    }

    @Override
    protected void initDataSaveControlValues() {
        if (isEdit) {
            initValuesForEdit();
        } else {
            initValuesForAdd();
        }
    }

    @Override
    protected Object save() {
        Integer saleQty = toIntegerOrNull(tfSaleQuantity.getText());
        BigDecimal sellingPrice = toBigDecimalOrNull(tfSellingPrice.getText());
        GroupedProductExpiryVM productExpiry = ComboBoxUtils.getSelectedItem(cbExpiredDate);
        SaleProductVM saleProduct = currentSaleProduct;
        if (!isEdit) {
            saleProduct = new SaleProductVM();
            saleProduct.setProductId(currentProduct.getId());
            saleProduct.setProductName(currentProduct.getName());
            saleProduct.setProductCode(currentProduct.getCode());
            saleProduct.setProductBarcode(currentProduct.getBarcode());
            saleProduct.setProductCategoryCode(currentProduct.getCategoryCode());
            saleProduct.setProductCategoryName(currentProduct.getCategoryName());
            saleProduct.setProductUnitLabel(currentProduct.getUnitLabel());
            saleProduct.setCurrentQuantity(toIntegerOrNull(tfCurrentQuantity.getText()));
            saleProduct.setGeneralSellingPrice(currentProduct.getGeneralSellingPrice());
            saleProduct.setPrescriptionSellingPrice(currentProduct.getPrescriptionSellingPrice());
            if (isNullOrZero(currentProduct.getGeneralSellingPrice())) {
                saleProduct.setGeneralSellingPrice(sellingPrice);
                saleProduct.setPrescriptionSellingPrice(
                        SellingMode.PRESCRIPTION.equals(confirmProduct.getSellingMode()) ? sellingPrice : null);
            }
        }
        saleProduct.setSaleQuantity(saleQty);
        saleProduct.setSellingPrice(sellingPrice);
        saleProduct.setSubtotal(sellingPrice.multiply(BigDecimal.valueOf(saleQty)));
        saleProduct.setExpiredDate(productExpiry == null ? null : productExpiry.getExpiredDate());
        confirmProduct.setSaleProduct(saleProduct);
        return confirmProduct;
    }

    @Override
    protected void validate(ControlValidator validator) {
        validator.validatePositive(tfCurrentQuantity, MessageCode.ERROR_EMPTY_CURRENT_QUANTITY);
        validator.validatePositive(tfSellingPrice, MessageCode.ERROR_EMPTY_SELLING_PRICE);
        validator.validatePositive(tfSaleQuantity, MessageCode.ERROR_EMPTY_QUANTITY);
        validator.validateCustom(() -> {
            Integer currentQty = toIntegerOrZero(tfCurrentQuantity.getText());
            Integer saleQty = toIntegerOrZero(tfSaleQuantity.getText());
            GroupedProductExpiryVM px = ComboBoxUtils.getSelectedItem(cbExpiredDate);
            if (px != null) {
                return saleQty > px.getQuantity();
            }
            List<SaleProductVM> saleProducts = confirmProduct.getCurrentSaleProducts();
            int qtySameProductsDifferentExpiry = saleProducts.stream()
                    .filter(sp -> equalsProductIdAndNotExpiredDate(sp, px)).map(SaleProductVM::getSaleQuantity)
                    .reduce(0, Integer::sum);
            return qtySameProductsDifferentExpiry + saleQty > currentQty;
        }, MessageCode.ERROR_SALE_QUANTITY_GREATER_THAN_PRODUCT_QUANTITY);
    }

    @Override
    protected void initServices() {
        // Nothing to do
    }

    private void initFocus(List<GroupedProductExpiryVM> productExpiries) {
        if (!productExpiries.isEmpty()) {
            setFocused(cbExpiredDate);
        }
        if (tfSellingPrice.isEditable()) {
            setFocused(tfSellingPrice);
        }
        if (tfCurrentQuantity.isEditable()) {
            setFocused(tfCurrentQuantity);
        }
    }

    private boolean equalsProductIdAndNotExpiredDate(SaleProductVM sp, GroupedProductExpiryVM px) {
        Long productId = isEdit ? currentSaleProduct.getProductId() : currentProduct.getId();
        boolean equalsProductId = sp.getProductId().equals(productId);
        boolean notEqualsExpiry = px == null ?
                sp.getExpiredDate() != null : !sp.getExpiredDate().equals(px.getExpiredDate());
        return equalsProductId && notEqualsExpiry;
    }

    private void initValuesForEdit() {
        currentSaleProduct = confirmProduct.getSaleProduct();
        List<GroupedProductExpiryVM> productExpiries = confirmProduct.getProductExpiries();
        tfName.setText(currentSaleProduct.getProductName());
        tfCode.setText(currentSaleProduct.getProductCode());
        tfBarcode.setText(currentSaleProduct.getProductBarcode());
        tfProductCategory.setText(currentSaleProduct.getProductCategoryName());
        tfProductUnit.setText(currentSaleProduct.getProductUnitLabel());
        tfSaleQuantity.setText(toStringOrEmpty(currentSaleProduct.getSaleQuantity()));
        tfCurrentQuantity.setText(toStringOrEmpty(currentSaleProduct.getCurrentQuantity()));
        tfCurrentQuantity.setEditable(!isEdit);
        tfSellingPrice.setText(toStringOrEmpty(currentSaleProduct.getSellingPrice()));
        if (!productExpiries.isEmpty()) {
            productExpiries.add(0, null);
            ComboBoxUtils
                    .init(cbExpiredDate, new GroupedProductExpiryComboBoxConverter(cbExpiredDate), productExpiries);
        }
        if (currentSaleProduct.getExpiredDate() != null) {
            ComboBoxUtils.select(
                    cbExpiredDate,
                    () -> cbExpiredDate.getItems().stream().filter(
                            exp -> exp != null && exp.getExpiredDate().equals(currentSaleProduct.getExpiredDate()))
                            .findAny().orElseThrow());
        }
    }

    private void initValuesForAdd() {
        currentProduct = confirmProduct.getProduct();
        List<GroupedProductExpiryVM> productExpiries = confirmProduct.getProductExpiries();
        SellingMode sellingMode = confirmProduct.getSellingMode();
        tfName.setText(currentProduct.getName());
        tfCode.setText(currentProduct.getCode());
        tfBarcode.setText(currentProduct.getBarcode());
        tfProductCategory.setText(currentProduct.getCategoryName());
        tfProductUnit.setText(currentProduct.getUnitLabel());
        tfSaleQuantity.setText(toStringOrEmpty(confirmProduct.getSaleQuantity()));
        tfCurrentQuantity.setText(toStringOrEmpty(currentProduct.getQuantity()));
        if (currentProduct.getQuantity() == null) {
            tfCurrentQuantity.setEditable(true);
        }
        BigDecimal generalSellingPrice = currentProduct.getGeneralSellingPrice();
        BigDecimal prescriptionSellingPrice = currentProduct.getPrescriptionSellingPrice();
        tfSellingPrice.setText(toStringOrEmpty(generalSellingPrice));
        if (SellingMode.PRESCRIPTION.equals(sellingMode) && prescriptionSellingPrice != null) {
            tfSellingPrice.setText(toStringOrEmpty(prescriptionSellingPrice));
        }
        confirmProduct.getCurrentSaleProducts().stream().filter(sp -> sp.getProductId().equals(currentProduct.getId()))
                .findAny().ifPresent(sp -> {
                    tfCurrentQuantity.setText(toStringOrEmpty(sp.getCurrentQuantity()));
                    tfSellingPrice.setText(toStringOrEmpty(sp.getSellingPrice()));
                });
        if (!productExpiries.isEmpty()) {
            productExpiries.add(0, null);
            ComboBoxUtils
                    .init(cbExpiredDate, new GroupedProductExpiryComboBoxConverter(cbExpiredDate), productExpiries);
        }
        initFocus(productExpiries);
    }

}

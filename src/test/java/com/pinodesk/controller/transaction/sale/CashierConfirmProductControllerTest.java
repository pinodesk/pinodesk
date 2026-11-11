package com.pinodesk.controller.transaction.sale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import com.pinodesk.JavaFXTestBase;
import com.pinodesk.pandora.utility.ControlValidator;
import com.pinodesk.pandora.utility.ValidationResult;
import com.pinodesk.viewmodel.GroupedProductExpiryVM;
import com.pinodesk.viewmodel.ProductVM;
import com.pinodesk.viewmodel.SaleProductVM;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

class CashierConfirmProductControllerTest extends JavaFXTestBase {
    private final ResourceBundle resources = ResourceBundle.getBundle("pinodesk.lang", Locale.ENGLISH);

    @Test
    void rejectsQuantityBeyondSelectedExpiryStock(FxRobot robot) {
        robot.interact(() -> {
            LocalDate expiryDate = LocalDate.of(2027, 1, 1);
            SaleProductVM existing = saleProduct(12L, expiryDate, 4);
            CashierConfirmProductController controller = form(
                    product(12L, 10),
                    List.of(existing),
                    expiry(expiryDate, 5),
                    2);

            assertFalse(validate(controller).isValid());
        });
    }

    @Test
    void rejectsQuantityBeyondTotalStockWhenNoExpiryIsSelected(FxRobot robot) {
        robot.interact(() -> {
            SaleProductVM existing = saleProduct(12L, null, 8);
            CashierConfirmProductController controller = form(product(12L, 10), List.of(existing), null, 3);

            assertFalse(validate(controller).isValid());
        });
    }

    @Test
    void ignoresSalesFromDifferentExpiryWhenValidatingBatchStock(FxRobot robot) {
        robot.interact(() -> {
            SaleProductVM existing = saleProduct(12L, LocalDate.of(2027, 1, 2), 4);
            CashierConfirmProductController controller = form(
                    product(12L, 10),
                    List.of(existing),
                    expiry(LocalDate.of(2027, 1, 1), 5),
                    2);

            assertTrue(validate(controller).isValid());
        });
    }

    @Test
    void ignoresOtherExpiryLinesAndHandlesNullExpiryDates(FxRobot robot) {
        robot.interact(() -> {
            LocalDate expiryDate = LocalDate.of(2027, 1, 1);
            SaleProductVM existing = saleProduct(12L, null, 4);
            CashierConfirmProductController controller = form(
                    product(12L, 10),
                    List.of(existing),
                    expiry(expiryDate, 5),
                    2);

            assertTrue(validate(controller).isValid());
        });
    }

    @Test
    void doesNotCountTheEditedSaleLineAgainstItself(FxRobot robot) {
        robot.interact(() -> {
            LocalDate expiryDate = LocalDate.of(2027, 1, 1);
            SaleProductVM existing = saleProduct(12L, expiryDate, 4);
            CashierController cashier = new CashierController();
            CashierController.ConfirmProduct confirm = cashier.new ConfirmProduct();
            confirm.setCurrentSaleProducts(new ArrayList<>(List.of(existing)));
            CashierConfirmProductController controller = form(confirm, null, existing, expiry(expiryDate, 5), 5);

            assertTrue(validate(controller).isValid());
        });
    }

    private CashierConfirmProductController form(
            ProductVM product,
            List<SaleProductVM> currentSales,
            GroupedProductExpiryVM selectedExpiry,
            int saleQuantity) {
        CashierController cashier = new CashierController();
        CashierController.ConfirmProduct confirm = cashier.new ConfirmProduct();
        confirm.setCurrentSaleProducts(new ArrayList<>(currentSales));
        return form(confirm, product, null, selectedExpiry, saleQuantity);
    }

    private CashierConfirmProductController form(
            CashierController.ConfirmProduct confirm,
            ProductVM product,
            SaleProductVM saleProduct,
            GroupedProductExpiryVM selectedExpiry,
            int saleQuantity) {
        CashierConfirmProductController controller = new CashierConfirmProductController();
        setField(controller, "confirmProduct", confirm);
        setField(controller, "currentProduct", product);
        setField(controller, "currentSaleProduct", saleProduct);
        setField(controller, "isEdit", saleProduct != null);
        setField(controller, "tfCurrentQuantity", new TextField("10"));
        setField(controller, "tfSellingPrice", new TextField("5.00"));
        setField(controller, "tfSaleQuantity", new TextField(Integer.toString(saleQuantity)));
        ComboBox<GroupedProductExpiryVM> expiryBox = new ComboBox<>();
        expiryBox.setValue(selectedExpiry);
        setField(controller, "cbExpiredDate", expiryBox);
        return controller;
    }

    private ValidationResult validate(CashierConfirmProductController controller) {
        ControlValidator validator = new ControlValidator(resources);
        invokeMethod(controller, "validate", validator);
        return validator.getResult();
    }

    private ProductVM product(Long id, int quantity) {
        ProductVM product = new ProductVM();
        product.setId(id);
        product.setQuantity(quantity);
        return product;
    }

    private SaleProductVM saleProduct(Long productId, LocalDate expiryDate, int quantity) {
        SaleProductVM saleProduct = new SaleProductVM();
        saleProduct.setProductId(productId);
        saleProduct.setExpiredDate(expiryDate);
        saleProduct.setSaleQuantity(quantity);
        return saleProduct;
    }

    private GroupedProductExpiryVM expiry(LocalDate expiryDate, int quantity) {
        GroupedProductExpiryVM expiry = new GroupedProductExpiryVM();
        expiry.setExpiredDate(expiryDate);
        expiry.setQuantity(quantity);
        return expiry;
    }
}

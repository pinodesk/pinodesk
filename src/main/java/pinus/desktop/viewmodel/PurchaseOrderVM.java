package pinus.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import pinus.desktop.constant.PaymentMethod;
import pinus.desktop.constant.PaymentPeriodUnit;

@Data
public class PurchaseOrderVM {

    private Long supplierId;
    private String orderNumber;
    private LocalDate orderDate;
    private PaymentMethod paymentMethod;
    private PaymentPeriodUnit paymentPeriodUnit;
    private Integer paymentPeriodCount;
    private LocalDate dueDate;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private BigDecimal totalPurchase;
    private List<PurchaseProductVM> purchaseProducts;
    private BigDecimal discount;
    private BigDecimal tax;

    @Data
    public static class PurchaseProductVM {
        private ProductVM product;
        private Integer purchaseQuantity;
        private BigDecimal purchasePrice;
        private BigDecimal sellingPrice;
        private BigDecimal subtotalPurchase;
    }

}

package pinus.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import pinus.desktop.constant.PaymentStatus;

@Data
public class PurchaseAddVM {
    private Long supplierId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private LocalDate paymentDueDate;
    private PaymentStatus paymentStatus;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal totalPurchase;
    private List<PurchaseProductVM> purchaseProducts;

    @Data
    public static class PurchaseProductVM {
        private SearchProductsByFilterVM product;
        private Integer quantity;
        private BigDecimal generalSellingPrice;
        private BigDecimal prescriptionSellingPrice;
        private BigDecimal buyingPrice;
        private BigDecimal subtotal;
        private String batchNumber;
        private LocalDate expiredDate;
    }

}

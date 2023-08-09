package pospino.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import pospino.desktop.constant.PaymentStatus;

@Data
public class PurchaseAddVM {
    private Long supplierId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private LocalDate paymentDueDate;
    private PaymentStatus paymentStatus;
    private BigDecimal totalDiscount;
    private BigDecimal tax;
    private BigDecimal totalPrice;
    private List<PurchaseProductVM> purchaseProducts;
}

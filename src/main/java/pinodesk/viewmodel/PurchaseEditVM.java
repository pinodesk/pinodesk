package pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import pinodesk.constant.PaymentStatus;

@Data
public class PurchaseEditVM {
    private Long supplierId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Integer totalProduct;
    private BigDecimal totalPayment;
    private LocalDate paymentDueDate;
    private PaymentStatus paymentStatus;
    private BigDecimal additionalDiscount;
    private BigDecimal totalDiscount;
    private BigDecimal tax;
    private BigDecimal totalPrice;
    private List<PurchaseProductVM> purchaseProducts;
}

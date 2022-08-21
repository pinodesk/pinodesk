package stoready.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import stoready.desktop.constant.PaymentStatus;
import stoready.desktop.constant.SellingMode;

@Data
public class SaleEditVM {
    private Long customerId;
    private Long doctorId;
    private SellingMode sellingMode;
    private String invoiceNumber;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDueDate;
    private Integer totalProduct;
    private BigDecimal totalSale;
    private BigDecimal totalPayment;
    private List<SaleProductVM> saleProducts;
}

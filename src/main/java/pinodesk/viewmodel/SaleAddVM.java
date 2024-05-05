package pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import pinodesk.constant.PaymentStatus;
import pinodesk.constant.SellingMode;

@Data
public class SaleAddVM {
    private Long customerId;
    private Long doctorId;
    private SellingMode sellingMode;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDueDate;
    private Integer totalProduct;
    private BigDecimal totalSale;
    private BigDecimal totalPayment;
    private List<SaleProductVM> saleProducts;
}

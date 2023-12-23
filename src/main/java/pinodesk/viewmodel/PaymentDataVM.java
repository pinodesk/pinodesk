package pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import pinodesk.constant.PaymentStatus;

@Data
public class PaymentDataVM {
    private BigDecimal paymentAmount;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDueDate;
    private BigDecimal changeAmount;
    private String invoiceNumber;
}

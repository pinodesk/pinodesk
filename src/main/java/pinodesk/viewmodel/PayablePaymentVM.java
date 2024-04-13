package pinodesk.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class PayablePaymentVM {
    private BigDecimal amount;
    private LocalDate paymentDate;
}

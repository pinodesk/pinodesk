package pinus.desktop.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ReceivablePaymentVM {
    private BigDecimal amount;
    private LocalDate paymentDate;
}

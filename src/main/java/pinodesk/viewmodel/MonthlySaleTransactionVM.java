package pinodesk.viewmodel;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MonthlySaleTransactionVM {
    private Integer totalTransaction;
    private BigDecimal totalPayment;
    private Integer monthNumber;
}

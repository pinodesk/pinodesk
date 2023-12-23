package pinodesk.viewmodel;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TotalSaleTransactionVM {
    private Integer totalTransaction;
    private BigDecimal totalPayment;
}

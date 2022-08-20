package stoready.desktop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReceivablePayment extends DataModel {

    public static final String C_RECEIVABLE_ID = "receivable_id";
    public static final String C_AMOUNT = "amount";
    public static final String C_PAYMENT_DATE = "payment_date";

    private Long receivableId;
    private BigDecimal amount;
    private LocalDate paymentDate;
}

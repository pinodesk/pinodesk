package pinodesk.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PayablePayment extends DataModel {

    public static final String C_PAYABLE_ID = "payable_id";
    public static final String C_AMOUNT = "amount";
    public static final String C_PAYMENT_DATE = "payment_date";

    private Long payableId;
    private BigDecimal amount;
    private LocalDate paymentDate;
}

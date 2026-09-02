package pinodesk.entity;

import java.time.LocalDate;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Consignment extends DataModel {

    public static final String C_INVOICE_NUMBER = "invoice_number";
    public static final String C_INVOICE_DATE = "invoice_date";
    public static final String C_SUPPLIER_ID = "supplier_id";
    public static final String C_USER_ID = "user_id";
    public static final String C_TOTAL_PRODUCT = "total_product";

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Long supplierId;
    private Long userId;
    private Integer totalProduct;
}

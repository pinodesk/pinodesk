package pinodesk.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Product extends DataModel {

    public static final String C_CODE = "code";
    public static final String C_BARCODE = "barcode";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";
    public static final String C_QUANTITY = "quantity";
    public static final String C_UNIT_CODE = "unit_code";
    public static final String C_CATEGORY_CODE = "category_code";
    public static final String C_GENERAL_SELLING_PRICE = "general_selling_price";
    public static final String C_PRESCRIPTION_SELLING_PRICE = "prescription_selling_price";
    public static final String C_AVERAGE_BUYING_PRICE = "average_buying_price";
    public static final String C_CLOSEST_EXPIRED_DATE = "closest_expired_date";
    public static final String C_STATUS = "status";

    private String code;
    private String barcode;
    private String name;
    private String description;
    private Integer quantity;
    private String categoryCode;
    private String unitCode;
    private BigDecimal generalSellingPrice;
    private BigDecimal prescriptionSellingPrice;
    private BigDecimal averageBuyingPrice;
    private LocalDate closestExpiredDate;
    private String status;
}

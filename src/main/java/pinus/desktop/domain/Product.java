package pinus.desktop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Product extends DataModel {

    public static final String TABLE_NAME = "product";

    public static final String C_CODE = "code";
    public static final String C_BARCODE = "barcode";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";
    public static final String C_QUANTITY = "quantity";
    public static final String C_UNIT_ID = "unit_id";
    public static final String C_UNIT_LABEL = "unit_label";
    public static final String C_CATEGORY_CODE = "category_code";
    public static final String C_GENERAL_SELLING_PRICE = "general_selling_price";
    public static final String C_PRESCRIPTION_SELLING_PRICE = "prescription_selling_price";
    public static final String C_AVERAGE_BUYING_PRICE = "average_buying_price";
    public static final String C_CLOSEST_EXPIRED_DATE = "closest_expired_date";
    public static final String C_STATUS = "status";

    @DataColumn(C_CODE)
    private String code;

    @DataColumn(C_BARCODE)
    private String barcode;

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_DESCRIPTION)
    private String description;

    @DataColumn(C_QUANTITY)
    private Integer quantity;

    @DataColumn(C_CATEGORY_CODE)
    private String categoryCode;

    @DataColumn(C_UNIT_ID)
    private Long unitId;

    @DataColumn(C_UNIT_LABEL)
    private String unitLabel;

    @DataColumn(C_GENERAL_SELLING_PRICE)
    private BigDecimal generalSellingPrice;

    @DataColumn(C_PRESCRIPTION_SELLING_PRICE)
    private BigDecimal prescriptionSellingPrice;

    @DataColumn(C_AVERAGE_BUYING_PRICE)
    private BigDecimal averageBuyingPrice;

    @DataColumn(C_CLOSEST_EXPIRED_DATE)
    private LocalDate closestExpiredDate;

    @DataColumn(C_STATUS)
    private String status;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

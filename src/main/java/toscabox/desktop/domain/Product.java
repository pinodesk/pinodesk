package toscabox.desktop.domain;

import java.math.BigDecimal;
import java.sql.Date;

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
    public static final String C_PURCHASE_PRICE = "purchase_price";
    public static final String C_SELLING_PRICE = "selling_price";
    public static final String C_VAT_INCLUDED = "vat_included";
    public static final String C_RACK_ID = "rack_id";
    public static final String C_RACK_CODE = "rack_code";
    public static final String C_RACK_NAME = "rack_name";
    public static final String C_EXPIRED_DATE = "expired_date";

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

    @DataColumn(C_PURCHASE_PRICE)
    private BigDecimal purchasePrice;

    @DataColumn(C_SELLING_PRICE)
    private BigDecimal sellingPrice;

    @DataColumn(C_VAT_INCLUDED)
    private String vatIncluded;

    @DataColumn(C_RACK_ID)
    private Long rackId;

    @DataColumn(C_RACK_CODE)
    private String rackCode;

    @DataColumn(C_RACK_NAME)
    private String rackName;

    @DataColumn(C_EXPIRED_DATE)
    private Date expiredDate;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

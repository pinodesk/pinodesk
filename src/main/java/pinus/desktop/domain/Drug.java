package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Drug extends DataModel {

    public static final String TABLE_NAME = "drug";

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_DRUG_CATEGORY_ID = "drug_category_id";
    public static final String C_INDICATION = "indication";
    public static final String C_CONTRAINDICATION = "contraindication";

    @DataColumn(C_PRODUCT_ID)
    private Long productId;

    @DataColumn(C_DRUG_CATEGORY_ID)
    private Long drugCategoryId;

    @DataColumn(C_INDICATION)
    private String indication;

    @DataColumn(C_CONTRAINDICATION)
    private String contraindication;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Drug extends DataModel {

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_DRUG_CATEGORY_ID = "drug_category_id";
    public static final String C_INDICATION = "indication";
    public static final String C_CONTRAINDICATION = "contraindication";

    private Long productId;
    private Long drugCategoryId;
    private String indication;
    private String contraindication;
}

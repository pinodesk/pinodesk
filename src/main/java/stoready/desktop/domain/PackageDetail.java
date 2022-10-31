package stoready.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PackageDetail extends DataModel {

    public static final String C_PACKAGE_PRODUCT_ID = "package_product_id";
    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_QUANTITY = "quantity";

    private Long packageProductId;
    private Long productId;
    private Integer quantity;
}

package toscabox.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductCategory extends DataModel {

    public static final String TABLE_NAME = "product_category";

    public static final String C_PARENT_CATEGORY_ID = "parent_category_id";
    public static final String C_LANGUAGE_CODE = "language_code";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";

    @DataColumn(C_PARENT_CATEGORY_ID)
    private Long parentCategoryId;

    @DataColumn(C_LANGUAGE_CODE)
    private String languageCode;

    @DataColumn(C_CODE)
    private String code;

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_DESCRIPTION)
    private String description;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

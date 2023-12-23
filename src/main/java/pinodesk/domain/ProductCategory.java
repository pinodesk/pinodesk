package pinodesk.domain;

import com.gitlab.mudiasoft.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductCategory extends DataModel {

    public static final String C_PARENT_CATEGORY_ID = "parent_category_id";
    public static final String C_LANGUAGE = "language";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";

    private Long parentCategoryId;
    private String language;
    private String code;
    private String name;
    private String description;
}

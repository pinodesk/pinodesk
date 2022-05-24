package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductCategory extends DataModel {

    public static final String C_PARENT_CATEGORY_ID = "parent_category_id";
    public static final String C_LANGUAGE_CODE = "language_code";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";

    private Long parentCategoryId;
    private String languageCode;
    private String code;
    private String name;
    private String description;
}

package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DrugCategory extends DataModel {

    public static final String C_CATEGORY_BASE_ID = "drug_category_base_id";
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_DESCRIPTION = "description";

    private Long drugCategoryBaseId;
    private String code;
    private String name;
    private String description;
}

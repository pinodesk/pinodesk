package pospino.desktop.domain;

import com.gitlab.mudiasoft.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Menu extends DataModel {

    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_LANGUAGE = "language";
    public static final String C_PARENT_MENU_ID = "parent_menu_id";

    private String code;
    private String name;
    private String language;
    private Long parentMenuId;
    private Integer seqNum;
}

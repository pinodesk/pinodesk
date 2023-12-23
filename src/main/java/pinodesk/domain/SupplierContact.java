package pinodesk.domain;

import com.gitlab.mudiasoft.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SupplierContact extends DataModel {

    public static final String C_SUPPLIER_ID = "supplier_id";
    public static final String C_NAME = "name";
    public static final String C_PHONE = "phone";
    public static final String C_EMAIL = "email";

    private Long supplierId;
    private String name;
    private String phone;
    private String email;
}

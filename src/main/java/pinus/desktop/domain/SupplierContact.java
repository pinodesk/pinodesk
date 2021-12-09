package pinus.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SupplierContact extends DataModel {

    public static final String TABLE_NAME = "supplier_contact";

    public static final String C_SUPPLIER_ID = "supplier_id";
    public static final String C_NAME = "name";
    public static final String C_PHONE = "phone";
    public static final String C_EMAIL = "email";

    @DataColumn(C_SUPPLIER_ID)
    private Long supplierId;

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_PHONE)
    private String phone;

    @DataColumn(C_EMAIL)
    private String email;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

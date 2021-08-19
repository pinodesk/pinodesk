package toska.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Configuration extends DataModel {

    public static final String TABLE_NAME = "configuration";

    public static final String C_CODE = "code";
    public static final String C_VALUE = "value";

    @DataColumn(C_CODE)
    private String code;

    @DataColumn(C_VALUE)
    private String value;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

package tosca.desktop.domain;

import com.gitlab.muhammadkholidb.sequel.annotation.DataColumn;
import com.gitlab.muhammadkholidb.sequel.model.DataModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Unit extends DataModel {

    public static final String TABLE_NAME = "unit";

    public static final String C_NAME = "name";
    public static final String C_LABEL = "label";

    @DataColumn(C_NAME)
    private String name;

    @DataColumn(C_LABEL)
    private String label;

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

}

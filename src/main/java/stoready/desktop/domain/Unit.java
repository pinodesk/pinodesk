package stoready.desktop.domain;

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

    public static final String C_NAME = "name";
    public static final String C_LABEL = "label";

    private String name;
    private String label;
}

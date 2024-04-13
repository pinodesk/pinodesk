package pinodesk.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;
import pinodesk.constant.SimpleStatus;

@Data
public class UserGroupMenuVM {
    private Long id;
    private Long userGroupId;
    private Long menuId;
    private String menuCode;
    private String menuName;
    private String language;
    private Long parentMenuId;
    private String read = SimpleStatus.NO.toString();
    private String write = SimpleStatus.NO.toString();

    // Reference:
    // https://stackoverflow.com/questions/51294612/javafx-checkboxes-in-tableview-when-one-is-selected-others-in-row-are-disabled

    private SimpleBooleanProperty booleanRead = new SimpleBooleanProperty();
    private SimpleBooleanProperty booleanWrite = new SimpleBooleanProperty();

    public boolean isBooleanRead() {
        return booleanRead.get();
    }

    public void setBooleanRead(boolean val) {
        booleanRead.set(val);
    }

    public SimpleBooleanProperty booleanReadProperty() {
        return booleanRead;
    }

    public boolean isBooleanWrite() {
        return booleanWrite.get();
    }

    public void setBooleanWrite(boolean val) {
        booleanWrite.set(val);
    }

    public SimpleBooleanProperty booleanWriteProperty() {
        return booleanWrite;
    }

}

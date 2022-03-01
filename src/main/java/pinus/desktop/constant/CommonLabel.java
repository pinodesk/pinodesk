package pinus.desktop.constant;

import com.gitlab.muhammadkholidb.pandora.utility.IMessage;

public enum CommonLabel implements IMessage {
    LBL_LOADING_DATA,
    LBL_NO_DATA,
    LBL_ACTIVE,
    LBL_INACTIVE,
    BTN_SAVE_AND_COPY, 
    LBL_PAID, 
    LBL_UNPAID;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}

package pinus.desktop.constant;

import com.gitlab.muhammadkholidb.pandora.utility.IMessage;

public enum CommonLabel implements IMessage {
    LBL_LOADING_DATA,
    LBL_NO_DATA,
    LBL_INFORMATION,
    LBL_ERROR,
    LBL_CONFIRMATION,
    LBL_ACTIVE,
    LBL_INACTIVE,
    BTN_OK,
    BTN_YES,
    BTN_NO,
    BTN_SAVE_AND_COPY,
    LBL_PAID,
    LBL_UNPAID,
    LBL_SYSTEM_ERROR,
    LBL_DETAILS;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}

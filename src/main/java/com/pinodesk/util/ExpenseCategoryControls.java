package com.pinodesk.util;

import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.service.SessionService;

public final class ExpenseCategoryControls {
    private ExpenseCategoryControls() {
    }

    public static boolean canWrite(SessionService session) {
        return session.isCurrentSessionActive() && session.getCurrentSession().getUserGroupMenus().stream().anyMatch(
                menu -> MenuCodeConstants.SETTINGS_EXPENSE_CATEGORIES.equals(menu.getMenuCode())
                        && SimpleStatus.YES.toString().equals(menu.getWrite()));
    }
}

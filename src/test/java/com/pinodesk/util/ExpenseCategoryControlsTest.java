package com.pinodesk.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.viewmodel.UserGroupMenuVM;
import com.pinodesk.viewmodel.CurrentSessionVM;
import com.pinodesk.service.SessionService;

class ExpenseCategoryControlsTest {

    @Test
    void canWriteReturnsTrueWhenUserHasWritePermission() {
        SessionService session = mock(SessionService.class);
        CurrentSessionVM currentSession = mock(CurrentSessionVM.class);
        UserGroupMenuVM menu = mock(UserGroupMenuVM.class);

        when(session.isCurrentSessionActive()).thenReturn(true);
        when(session.getCurrentSession()).thenReturn(currentSession);
        when(currentSession.getUserGroupMenus()).thenReturn(List.of(menu));
        when(menu.getMenuCode()).thenReturn(MenuCodeConstants.SETTINGS_EXPENSE_CATEGORIES);
        when(menu.getWrite()).thenReturn(SimpleStatus.YES.toString());

        assertTrue(ExpenseCategoryControls.canWrite(session));
    }

    @Test
    void canWriteReturnsFalseWhenSessionNotActive() {
        SessionService session = mock(SessionService.class);

        when(session.isCurrentSessionActive()).thenReturn(false);

        assertFalse(ExpenseCategoryControls.canWrite(session));
    }

    @Test
    void canWriteReturnsFalseWhenMenuNotInUserPermissions() {
        SessionService session = mock(SessionService.class);
        CurrentSessionVM currentSession = mock(CurrentSessionVM.class);
        UserGroupMenuVM menu = mock(UserGroupMenuVM.class);

        when(session.isCurrentSessionActive()).thenReturn(true);
        when(session.getCurrentSession()).thenReturn(currentSession);
        when(currentSession.getUserGroupMenus()).thenReturn(List.of(menu));
        when(menu.getMenuCode()).thenReturn(MenuCodeConstants.SETTINGS_CONFIGURATION);
        when(menu.getWrite()).thenReturn(SimpleStatus.YES.toString());

        assertFalse(ExpenseCategoryControls.canWrite(session));
    }

    @Test
    void canWriteReturnsFalseWhenWritePermissionIsNo() {
        SessionService session = mock(SessionService.class);
        CurrentSessionVM currentSession = mock(CurrentSessionVM.class);
        UserGroupMenuVM menu = mock(UserGroupMenuVM.class);

        when(session.isCurrentSessionActive()).thenReturn(true);
        when(session.getCurrentSession()).thenReturn(currentSession);
        when(currentSession.getUserGroupMenus()).thenReturn(List.of(menu));
        when(menu.getMenuCode()).thenReturn(MenuCodeConstants.SETTINGS_EXPENSE_CATEGORIES);
        when(menu.getWrite()).thenReturn(SimpleStatus.NO.toString());

        assertFalse(ExpenseCategoryControls.canWrite(session));
    }

    @Test
    void canWriteReturnsFalseWhenNoMenus() {
        SessionService session = mock(SessionService.class);
        CurrentSessionVM currentSession = mock(CurrentSessionVM.class);

        when(session.isCurrentSessionActive()).thenReturn(true);
        when(session.getCurrentSession()).thenReturn(currentSession);
        when(currentSession.getUserGroupMenus()).thenReturn(List.of());

        assertFalse(ExpenseCategoryControls.canWrite(session));
    }
}

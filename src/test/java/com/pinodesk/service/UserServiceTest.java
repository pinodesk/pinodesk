package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.pinodesk.constant.DomainError;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.UserRepository;
import com.pinodesk.viewmodel.CurrentSessionVM;
import com.pinodesk.viewmodel.UserVM;

class UserServiceTest {
    @Test
    void deletingCurrentUserIsRejectedBeforeDatabaseWrite() {
        SessionService sessions = mock(SessionService.class);
        UserRepository users = mock(UserRepository.class);
        UserService service = new UserService(sessions, users);
        CurrentSessionVM current = new CurrentSessionVM();
        UserVM user = new UserVM();
        user.setId(7L);
        current.setUser(user);
        when(sessions.getCurrentSession()).thenReturn(current);

        DomainException failure = assertThrows(DomainException.class, () -> service.removeUsers(List.of(7L)));

        assertEquals(DomainError.DELETE_CURRENT_USER_FORBIDDEN, failure.getError());
        verifyNoInteractions(users);
    }

    @Test
    void deletingOtherUserKeepsAnActiveAdministrator() {
        SessionService sessions = mock(SessionService.class);
        UserRepository users = mock(UserRepository.class);
        UserService service = new UserService(sessions, users);
        CurrentSessionVM current = new CurrentSessionVM();
        UserVM user = new UserVM();
        user.setId(7L);
        current.setUser(user);
        when(sessions.getCurrentSession()).thenReturn(current);
        when(
                users.existsByUserGroupIdAndStatusAndDeletedAtIsNull(
                        com.pinodesk.constant.CommonConstants.USER_GROUP_ID_ADMINISTRATOR,
                        com.pinodesk.constant.UserStatus.ACTIVE.toString()))
                .thenReturn(true);

        service.removeUsers(List.of(8L));

        org.mockito.Mockito.verify(users).deleteUpdateByIdIn(List.of(8L));
    }

    @Test
    void deletingLastAdministratorRaisesDomainError() {
        SessionService sessions = mock(SessionService.class);
        UserRepository users = mock(UserRepository.class);
        UserService service = new UserService(sessions, users);
        CurrentSessionVM current = new CurrentSessionVM();
        UserVM user = new UserVM();
        user.setId(7L);
        current.setUser(user);
        when(sessions.getCurrentSession()).thenReturn(current);

        DomainException failure = assertThrows(DomainException.class, () -> service.removeUsers(List.of(8L)));

        assertEquals(DomainError.USER_GROUP_ADMINISTRATOR_MUST_HAVE_USER, failure.getError());
    }
}

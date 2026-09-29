package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
        UserService service = new UserService(sessions);
        ReflectionTestUtils.setField(service, "userRepository", users);
        CurrentSessionVM current = new CurrentSessionVM();
        UserVM user = new UserVM();
        user.setId(7L);
        current.setUser(user);
        when(sessions.getCurrentSession()).thenReturn(current);

        DomainException failure = assertThrows(DomainException.class, () -> service.removeUsers(List.of(7L)));

        assertEquals(DomainError.DELETE_CURRENT_USER_FORBIDDEN, failure.getError());
        verifyNoInteractions(users);
    }
}

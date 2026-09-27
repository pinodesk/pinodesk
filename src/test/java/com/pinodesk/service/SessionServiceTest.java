package com.pinodesk.service;

import com.pinodesk.viewmodel.SessionVM;

import com.pinodesk.viewmodel.CurrentSessionVM;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import com.pinodesk.constant.Activity;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.entity.Session;
import com.pinodesk.entity.User;
import com.pinodesk.entity.UserGroup;
import com.pinodesk.repository.SessionRepository;
import com.pinodesk.repository.UserGroupMenuRepository;
import com.pinodesk.repository.UserGroupRepository;
import com.pinodesk.repository.UserRepository;
import com.pinodesk.util.PasswordUtils;

class SessionServiceTest extends BaseServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private UserGroupMenuRepository userGroupMenuRepository;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private SessionRepository sessionRepository;
    private SessionService service;

    @org.junit.jupiter.api.BeforeEach
    void wireDependencies() {
        service = new SessionService(userRepository, userGroupRepository, userGroupMenuRepository);
        ReflectionTestUtils.setField(service, "objectConverter", objectConverter);
        ReflectionTestUtils.setField(service, "configurationService", configurationService);
        ReflectionTestUtils.setField(service, "sessionRepository", sessionRepository);
    }

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-26T18:30:00Z"), ZoneId.of("Asia/Jakarta"));
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 27, 1, 30);

    @Test
    void loginUsesApplicationZoneForBothSessionTimestamps() {
        User user = new User();
        user.setId(7L);
        user.setUserGroupId(2L);
        user.setPasswordHash(PasswordUtils.encrypt("password"));
        UserGroup group = new UserGroup();
        group.setId(2L);
        when(userRepository.findByUsernameAndDeletedAtIsNull("alice")).thenReturn(Optional.of(user));
        when(userGroupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(configurationService.getConfigurationMap()).thenReturn(Map.of(ConfigurationConstants.LANGUAGE, "id"));
        when(userGroupMenuRepository.findByUserGroupId(2L, "id")).thenReturn(List.of());
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));
        try (MockedStatic<Clock> clocks = mockStatic(Clock.class)) {
            clocks.when(Clock::systemDefaultZone).thenReturn(CLOCK);
            service.login("alice", "password");
        }
        ArgumentCaptor<Session> saved = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(saved.capture());
        assertEquals(NOW, saved.getValue().getLoginAt());
        assertEquals(NOW, saved.getValue().getLastActivityAt());
        assertEquals(7L, saved.getValue().getUserId());
        assertEquals(Activity.LOGIN.toString(), saved.getValue().getLastActivity());
        verify(sessionRepository).deleteUpdateByDeletedAtIsNull();
        assertTrue(service.isCurrentSessionActive());
    }

    @Test
    void logoutUsesApplicationZoneAndClearsSession() {
        CurrentSessionVM current = new CurrentSessionVM();
        SessionVM session = new SessionVM();
        session.setId(9L);
        current.setSession(session);
        ReflectionTestUtils.setField(service, "currentSession", current);
        try (MockedStatic<Clock> clocks = mockStatic(Clock.class)) {
            clocks.when(Clock::systemDefaultZone).thenReturn(CLOCK);
            service.logout();
        }
        ArgumentCaptor<Session> saved = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(saved.capture());
        assertEquals(9L, saved.getValue().getId());
        assertEquals(NOW, saved.getValue().getLogoutAt());
        assertEquals(NOW, saved.getValue().getDeletedAt());
        assertFalse(service.isCurrentSessionActive());
    }

    @Test
    void activityUsesApplicationZoneAndKeepsPersistedSession() {
        CurrentSessionVM current = new CurrentSessionVM();
        SessionVM session = new SessionVM();
        session.setId(9L);
        current.setSession(session);
        ReflectionTestUtils.setField(service, "currentSession", current);
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));
        try (MockedStatic<Clock> clocks = mockStatic(Clock.class)) {
            clocks.when(Clock::systemDefaultZone).thenReturn(CLOCK);
            service.updateLastActivity(Activity.GET_CONFIGURATION_MAP);
        }
        assertEquals(NOW, service.getCurrentSession().getSession().getLastActivityAt());
        assertEquals(
                Activity.GET_CONFIGURATION_MAP.toString(),
                service.getCurrentSession().getSession().getLastActivity());
        assertEquals(9L, service.getCurrentSession().getSession().getId());
    }

    @Test
    void sessionExpiresAtExactConfiguredHourInApplicationZone() {
        Session session = new Session();
        session.setUserId(7L);
        session.setLastActivityAt(NOW.minusHours(2));
        when(sessionRepository.findFirstByDeletedAtIsNullOrderByIdDesc()).thenReturn(Optional.of(session));
        when(configurationService.getConfiguration(ConfigurationConstants.SESSION_MAX_DURATION_HOUR)).thenReturn("2");
        try (MockedStatic<Clock> clocks = mockStatic(Clock.class)) {
            clocks.when(Clock::systemDefaultZone).thenReturn(CLOCK);
            service.activateLastSession();
        }
        assertFalse(service.isCurrentSessionActive());
        org.mockito.Mockito.verifyNoInteractions(userRepository);
    }

    @Test
    void sessionBeforeExpiryCanBeReactivatedAcrossMidnight() {
        Session session = new Session();
        session.setUserId(7L);
        session.setLastActivityAt(NOW.minusHours(2).plusSeconds(1));
        User user = new User();
        user.setId(7L);
        user.setUserGroupId(2L);
        UserGroup group = new UserGroup();
        group.setId(2L);
        when(sessionRepository.findFirstByDeletedAtIsNullOrderByIdDesc()).thenReturn(Optional.of(session));
        when(configurationService.getConfiguration(ConfigurationConstants.SESSION_MAX_DURATION_HOUR)).thenReturn("2");
        when(configurationService.getConfiguration(ConfigurationConstants.LANGUAGE)).thenReturn("id");
        when(userRepository.findByIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(user));
        when(userGroupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(userGroupMenuRepository.findByUserGroupId(2L, "id")).thenReturn(List.of());
        try (MockedStatic<Clock> clocks = mockStatic(Clock.class)) {
            clocks.when(Clock::systemDefaultZone).thenReturn(CLOCK);
            service.activateLastSession();
        }
        assertTrue(service.isCurrentSessionActive());
        assertEquals(7L, service.getCurrentSession().getUser().getId());
    }

    @Test
    void missingUserCannotStartSession() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("missing")).thenReturn(Optional.empty());
        com.pinodesk.exception.DomainException error = org.junit.jupiter.api.Assertions
                .assertThrows(com.pinodesk.exception.DomainException.class, () -> service.login("missing", "password"));
        assertEquals(com.pinodesk.constant.DomainError.USER_NOT_FOUND, error.getError());
        assertFalse(service.isCurrentSessionActive());
        org.mockito.Mockito.verifyNoInteractions(sessionRepository);
    }

    @Test
    void missingGroupCannotDeleteExistingSessions() {
        User user = new User();
        user.setPasswordHash(PasswordUtils.encrypt("password"));
        user.setUserGroupId(2L);
        when(userRepository.findByUsernameAndDeletedAtIsNull("alice")).thenReturn(Optional.of(user));
        when(userGroupRepository.findById(2L)).thenReturn(Optional.empty());
        com.pinodesk.exception.DomainException error = org.junit.jupiter.api.Assertions
                .assertThrows(com.pinodesk.exception.DomainException.class, () -> service.login("alice", "password"));
        assertEquals(com.pinodesk.constant.DomainError.USER_GROUP_NOT_FOUND_BY_ID, error.getError());
        org.mockito.Mockito.verifyNoInteractions(sessionRepository);
    }

    @Test
    void menuLookupFailureCannotStartSession() {
        User user = new User();
        user.setPasswordHash(PasswordUtils.encrypt("password"));
        user.setUserGroupId(2L);
        UserGroup group = new UserGroup();
        group.setId(2L);
        when(userRepository.findByUsernameAndDeletedAtIsNull("alice")).thenReturn(Optional.of(user));
        when(userGroupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(configurationService.getConfigurationMap()).thenReturn(Map.of(ConfigurationConstants.LANGUAGE, "en"));
        IllegalStateException failure = new IllegalStateException("Menu unavailable");
        when(userGroupMenuRepository.findByUserGroupId(2L, "en")).thenThrow(failure);
        org.junit.jupiter.api.Assertions.assertSame(
                failure,
                org.junit.jupiter.api.Assertions
                        .assertThrows(IllegalStateException.class, () -> service.login("alice", "password")));
        org.mockito.Mockito.verifyNoInteractions(sessionRepository);
    }
}

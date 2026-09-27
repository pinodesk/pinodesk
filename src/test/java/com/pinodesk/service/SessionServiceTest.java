package com.pinodesk.service;

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
import org.mockito.InjectMocks;
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
    @InjectMocks
    private SessionService service;

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
}

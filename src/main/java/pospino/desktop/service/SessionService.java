package pospino.desktop.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pospino.desktop.constant.Activity;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.DomainError;
import pospino.desktop.domain.Session;
import pospino.desktop.domain.User;
import pospino.desktop.domain.UserGroup;
import pospino.desktop.exception.DomainException;
import pospino.desktop.repository.SessionRepository;
import pospino.desktop.repository.UserGroupMenuRepository;
import pospino.desktop.repository.UserGroupRepository;
import pospino.desktop.repository.UserRepository;
import pospino.desktop.util.PasswordUtils;
import pospino.desktop.viewmodel.CurrentSessionVM;
import pospino.desktop.viewmodel.SessionVM;
import pospino.desktop.viewmodel.UserGroupMenuVM;
import pospino.desktop.viewmodel.UserGroupVM;
import pospino.desktop.viewmodel.UserVM;

@Slf4j
@Service
public class SessionService extends BaseService {

    @Getter
    private CurrentSessionVM currentSession;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupMenuRepository userGroupMenuRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SessionRepository sessionRepository;

    @Transactional
    public void login(String username, String password) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new DomainException(DomainError.USER_NOT_FOUND));
        if (!PasswordUtils.isValid(password, user.getPasswordHash())) {
            throw new DomainException(DomainError.USER_NOT_FOUND);
        }
        UserGroup userGroup = userGroupRepository.findById(user.getUserGroupId())
                .orElseThrow(() -> new DomainException(DomainError.USER_GROUP_NOT_FOUND_BY_ID));
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        List<UserGroupMenuVM> userGroupMenus = userGroupMenuRepository.findByUserGroupId(userGroup.getId(), language);
        // Delete all unlogged out sessions to make sure only one session is active
        sessionRepository.deleteUpdateByDeletedAtIsNull();
        Session session = new Session();
        session.setLoginAt(LocalDateTime.now());
        session.setUserId(user.getId());
        Session created = sessionRepository.save(session);
        currentSession = new CurrentSessionVM();
        currentSession.setSession(objectConverter.convertObject(created, SessionVM.class));
        currentSession.setUser(objectConverter.convertObject(user, UserVM.class));
        currentSession.setUserGroup(objectConverter.convertObject(userGroup, UserGroupVM.class));
        currentSession.setUserGroupMenus(userGroupMenus);
    }

    @Transactional
    public void logout() {
        LocalDateTime now = LocalDateTime.now();
        Session session = objectConverter.convertObject(currentSession.getSession(), Session.class);
        session.setLogoutAt(now);
        session.setDeletedAt(now);
        sessionRepository.save(session);
        currentSession = null;
    }

    public boolean isCurrentSessionActive() {
        return currentSession != null;
    }

    @Transactional
    public void updateLastActivity(Activity activity) {
        Session session = objectConverter.convertObject(currentSession.getSession(), Session.class);
        session.setLastActivity(activity.toString());
        session.setLastActivityAt(LocalDateTime.now());
        currentSession.setSession(objectConverter.convertObject(sessionRepository.save(session), SessionVM.class));
    }

    public void activateLastSession() {
        Optional<Session> lastSession = sessionRepository.findFirstByDeletedAtIsNullOrderByIdDesc();
        if (lastSession.isEmpty()) {
            return;
        }
        Session session = lastSession.get();
        if (log.isDebugEnabled()) {
            log.debug("Session id: {}", session.getId());
        }
        LocalDateTime lastActivityAt = session.getLastActivityAt();
        if (lastActivityAt == null) {
            return;
        }
        String strMaxDuration = configurationService.getConfiguration(ConfigurationConstants.SESSION_MAX_DURATION_HOUR);
        long actualDuration = lastActivityAt.until(LocalDateTime.now(), ChronoUnit.HOURS);
        if (log.isDebugEnabled()) {
            log.debug("Actual duration: {}", actualDuration);
        }
        if (actualDuration >= Long.valueOf(strMaxDuration)) {
            return;
        }
        Optional<User> oUser = userRepository.findByIdAndDeletedAtIsNull(session.getUserId());
        if (oUser.isEmpty()) {
            return;
        }
        User user = oUser.get();
        UserGroup userGroup = userGroupRepository.findById(user.getUserGroupId())
                .orElseThrow(() -> new DomainException(DomainError.USER_GROUP_NOT_FOUND_BY_ID));
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        List<UserGroupMenuVM> userGroupMenus = userGroupMenuRepository.findByUserGroupId(userGroup.getId(), language);
        currentSession = new CurrentSessionVM();
        currentSession.setSession(objectConverter.convertObject(session, SessionVM.class));
        currentSession.setUser(objectConverter.convertObject(user, UserVM.class));
        currentSession.setUserGroup(objectConverter.convertObject(userGroup, UserGroupVM.class));
        currentSession.setUserGroupMenus(userGroupMenus);
    }

}

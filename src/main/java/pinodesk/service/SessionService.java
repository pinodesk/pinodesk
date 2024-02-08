package pinodesk.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pinodesk.constant.Activity;
import pinodesk.constant.ConfigurationConstants;
import pinodesk.constant.DomainError;
import pinodesk.exception.DomainException;
import pinodesk.repository.SessionRepository;
import pinodesk.repository.UserRepository;
import pinodesk.viewmodel.CurrentSessionVM;
import pinodesk.viewmodel.SessionVM;
import pinodesk.viewmodel.UserGroupMenuVM;
import pinodesk.viewmodel.UserGroupVM;
import pinodesk.viewmodel.UserVM;
import pinodesk.domain.Session;
import pinodesk.domain.User;
import pinodesk.domain.UserGroup;
import pinodesk.repository.UserGroupMenuRepository;
import pinodesk.repository.UserGroupRepository;
import pinodesk.util.PasswordUtils;

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
        Map<String, String> configurationMap = configurationService.getConfigurationMap();
        String language = configurationMap.get(ConfigurationConstants.LANGUAGE);
        List<UserGroupMenuVM> userGroupMenus = userGroupMenuRepository.findByUserGroupId(userGroup.getId(), language);
        // Delete all unlogged-out sessions to make sure only one session is active
        sessionRepository.deleteUpdateByDeletedAtIsNull();
        LocalDateTime now = LocalDateTime.now();
        Session session = new Session();
        session.setLoginAt(now);
        session.setUserId(user.getId());
        session.setLastActivity(Activity.LOGIN.toString());
        session.setLastActivityAt(now);
        Session created = sessionRepository.save(session);
        currentSession = new CurrentSessionVM();
        currentSession.setSession(objectConverter.convertObject(created, SessionVM.class));
        currentSession.setUser(objectConverter.convertObject(user, UserVM.class));
        currentSession.setUserGroup(objectConverter.convertObject(userGroup, UserGroupVM.class));
        currentSession.setUserGroupMenus(userGroupMenus);
        currentSession.setConfigurationMap(configurationMap);
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
    public synchronized void updateLastActivity(Activity activity) {
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
        log.debug("Session id: {}", session.getId());

        LocalDateTime lastActivityAt = session.getLastActivityAt();
        if (lastActivityAt == null) {
            return;
        }
        String strMaxDuration = configurationService.getConfiguration(ConfigurationConstants.SESSION_MAX_DURATION_HOUR);
        long actualDuration = lastActivityAt.until(LocalDateTime.now(), ChronoUnit.HOURS);
        log.debug("Actual duration: {}", actualDuration);
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

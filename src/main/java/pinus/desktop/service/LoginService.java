package pinus.desktop.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import pinus.desktop.constant.Activity;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.Login;
import pinus.desktop.domain.User;
import pinus.desktop.domain.UserGroup;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.LoginRepository;
import pinus.desktop.repository.UserGroupMenuRepository;
import pinus.desktop.repository.UserGroupRepository;
import pinus.desktop.repository.UserRepository;
import pinus.desktop.util.PasswordUtils;
import pinus.desktop.viewmodel.LoginDetailsVM;
import pinus.desktop.viewmodel.LoginVM;
import pinus.desktop.viewmodel.UserGroupMenuVM;
import pinus.desktop.viewmodel.UserGroupVM;
import pinus.desktop.viewmodel.UserVM;

@Service
public class LoginService extends BaseService {

    @Getter
    private LoginDetailsVM loginDetails;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupMenuRepository userGroupMenuRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private LoginRepository loginRepository;

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
        Login login = new Login();
        login.setLoginAt(LocalDateTime.now());
        login.setUserId(user.getId());
        Login created = loginRepository.save(login);
        loginDetails = new LoginDetailsVM();
        loginDetails.setLogin(objectConverter.convertObject(created, LoginVM.class));
        loginDetails.setUser(objectConverter.convertObject(user, UserVM.class));
        loginDetails.setUserGroup(objectConverter.convertObject(userGroup, UserGroupVM.class));
        loginDetails.setUserGroupMenus(userGroupMenus);
    }

    public void logout() {
        loginDetails = null;
    }

    public boolean loginCheck() {
        return loginDetails != null;
    }

    public void updateLastActivity(Activity activity) {
        Login login = objectConverter.convertObject(loginDetails.getLogin(), Login.class);
        login.setLastActivity(activity.toString());
        login.setLastActivityAt(LocalDateTime.now());
        loginDetails.setLogin(objectConverter.convertObject(loginRepository.save(login), LoginVM.class));
    }

}

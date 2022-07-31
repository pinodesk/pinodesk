package pinus.desktop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.constant.SimpleStatus;
import pinus.desktop.domain.Menu;
import pinus.desktop.domain.User;
import pinus.desktop.domain.UserGroup;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.MenuRepository;
import pinus.desktop.repository.UserGroupMenuRepository;
import pinus.desktop.repository.UserGroupRepository;
import pinus.desktop.repository.UserRepository;
import pinus.desktop.util.PasswordUtils;
import pinus.desktop.viewmodel.LoginUserVM;
import pinus.desktop.viewmodel.LoginUserVM.LoginMenu;
import pinus.desktop.viewmodel.UserGroupMenuVM;

@Service
public class LoginService extends BaseService {

    @Getter
    private LoginUserVM loginUser;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupMenuRepository userGroupMenuRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ConfigurationService configurationService;

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
        List<Menu> menus = menuRepository.findByLanguageAndDeletedAtIsNull(language);
        loginUser = new LoginUserVM();
        loginUser.setFullName(user.getFullName());
        loginUser.setId(user.getId());
        loginUser.setStatus(user.getStatus());
        loginUser.setUserGroupId(userGroup.getId());
        loginUser.setUserGroupName(userGroup.getName());
        loginUser.setUserGroupStatus(userGroup.getStatus());
        loginUser.setUsername(user.getUsername());
        loginUser.setMenus(new ArrayList<>());
        loginUser.setMenuCodes(new ArrayList<>());
        menus.forEach(menu -> {
            LoginMenu lm = new LoginMenu();
            lm.setId(menu.getId());
            lm.setCode(menu.getCode());
            lm.setName(menu.getName());
            String read = userGroupMenus.stream().filter(ugm -> ugm.getMenuId().equals(menu.getId()))
                    .map(UserGroupMenuVM::getRead).findAny().orElse(SimpleStatus.NO.toString());
            String write = userGroupMenus.stream().filter(ugm -> ugm.getMenuId().equals(menu.getId()))
                    .map(UserGroupMenuVM::getWrite).findAny().orElse(SimpleStatus.NO.toString());
            lm.setRead(read);
            lm.setWrite(write);
            loginUser.getMenus().add(lm);
            loginUser.getMenuCodes().add(menu.getCode());
        });
    }

    public void logout() {
        loginUser = null;
    }

    public boolean loginCheck() {
        return loginUser != null;
    }

}
